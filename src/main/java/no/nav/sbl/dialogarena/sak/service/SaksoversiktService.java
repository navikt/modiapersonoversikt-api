package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.Kvittering;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import org.apache.commons.collections15.Predicate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.equalToIgnoreCase;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.INNSENDT;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.KVITTERING;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSIDER_FRA_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSKJEDER_TIL_BEHANDLINGER;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.TEMAKODE_FOR_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.TEMA_VM;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.behandlingsDato;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.behandlingsStatus;

public class SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    /**
     * Henter alle tema for en gitt person
     */
    public List<TemaVM> hentTemaer(String fnr) {
        PreparedIterable<TemaVM> map = on(hentSakerForAktor(hentAktorId(fnr))).map(TEMA_VM);
        List<TemaVM> collect = map.collect(new SistOppdaterteBehandlingComparator());
        return collect;
    }

    /**
     * Henter alle behandlinger for et gitt tema fra flere baksystemer
     */
    public List<GenerellBehandling> hentBehandlingerForTemakode(String fnr, String temakode) {
        List<GenerellBehandling> behandlinger = new ArrayList<>();
        WSSak wsSak = hentSakForAktorPaaTema(hentAktorId(fnr), temakode);
        List<WSBehandlingskjede> alleBehandlingskjeder = wsSak.getBehandlingskjede();

        List<Kvittering> kvitteringer = hentKvitteringer(fnr, BEHANDLINGSIDER_FRA_SAK.transform(wsSak));
        behandlinger.addAll(behandlingerSomIkkeErKvitteringer(alleBehandlingskjeder, kvitteringer)); // TODO: Legg på temakode her også

        Map<String, Kvittering> kvitteringerForBehandlingsID = mapKvitteringMedBehandlingsID(kvitteringer);
        Map<String, WSBehandlingskjede> kjederForBehandlingsID = mappedeKjeder(kvitteringerForBehandlingsID.keySet(), finnBehandlingskjederSomHarKvittering(alleBehandlingskjeder, kvitteringer));
        for (String kvitteringsID : kvitteringerForBehandlingsID.keySet()) {
            behandlinger.add(beriketKvittering(kvitteringerForBehandlingsID.get(kvitteringsID), kjederForBehandlingsID.get(kvitteringsID), temakode));
        }
        return behandlinger;
    }

    private Kvittering beriketKvittering(Kvittering kvittering, WSBehandlingskjede wsBehandlingskjede, String sakstema) {
        return (Kvittering) kvittering.withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                .withOpprettetDato(wsBehandlingskjede.getStart())
                .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede))
                .withBehandlingsTema(sakstema);
    }

    private List<GenerellBehandling> behandlingerSomIkkeErKvitteringer(List<WSBehandlingskjede> alleBehandlingskjeder, List<Kvittering> kvitteringer) {
        return BEHANDLINGSKJEDER_TIL_BEHANDLINGER.transform(behandlingskjederUtenKvitteringer(alleBehandlingskjeder, kvitteringer).collect());
    }

    private List<WSBehandlingskjede> finnBehandlingskjederSomHarKvittering(List<WSBehandlingskjede> alleBehandlingskjeder, List<Kvittering> kvitteringer) {
        return on(alleBehandlingskjeder).filter(finnesKvitteringMedSammeBehandlingsid(kvitteringer)).collect();
    }

    private Map<String, Kvittering> mapKvitteringMedBehandlingsID(List<Kvittering> kvitteringer) {
        Map<String, Kvittering> map = new HashMap<>();
        for (Kvittering kvittering : kvitteringer) {
            map.put(kvittering.behandlingsId, kvittering);
        }
        return map;
    }

    private Map<String, WSBehandlingskjede> mappedeKjeder(Set<String> kvitteringsIDer, List<WSBehandlingskjede> behandlingskjederMedKvitteringskobling) {
        Map<String, WSBehandlingskjede> kjederMappetViaID = new HashMap<>();
        for (WSBehandlingskjede behandlingskjede : behandlingskjederMedKvitteringskobling) {
            for (String potensiellKvitteringsID : behandlingskjede.getBehandlingsListeRef()) {
                if (kvitteringsIDer.contains(potensiellKvitteringsID)) {
                    kjederMappetViaID.put(potensiellKvitteringsID, behandlingskjede);
                }
            }
        }
        return kjederMappetViaID;
    }

    private PreparedIterable<WSBehandlingskjede> behandlingskjederUtenKvitteringer(List<WSBehandlingskjede> behandlingskjeder, List<Kvittering> kvitteringerForTema) {
        return on(behandlingskjeder).filter(not(finnesKvitteringMedSammeBehandlingsid(kvitteringerForTema)));
    }

    private List<Kvittering> hentKvitteringer(String fnr, List<String> behandlingsIDer) {
        List<Kvittering> kvitteringer = new ArrayList<>();
        for (String behandlingId : behandlingsIDer) {
            leggTilKvitteringHvisEksisterer(fnr, behandlingId, kvitteringer);
        }
        return kvitteringer;
    }

    private void leggTilKvitteringHvisEksisterer(String fnr, String behandlingsId, List<Kvittering> kvitteringer) {
        List<WSSoknad> soknader = hentInnsendteSoknader(fnr);
        for (WSSoknad soknad : soknader) {
            if (soknad.getBehandlingsId().equals(behandlingsId)) {
                kvitteringer.add(KVITTERING.transform(soknad));
            }
        }
    }

    private List<WSSoknad> hentInnsendteSoknader(String fnr) {
        return on(henvendelseSoknaderPortType.hentSoknadListe(fnr)).filter(where(INNSENDT, equalTo(true))).collect();
    }

    private String hentAktorId(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(lagAktorRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new SystemException("Klarte ikke hente aktørId", hentAktoerIdForIdentPersonIkkeFunnet);
        }
    }

    private WSSak hentSakForAktorPaaTema(String aktorId, String temakode) {
        return on(hentSakerForAktor(aktorId)).filter(where(TEMAKODE_FOR_SAK, equalToIgnoreCase(temakode))).head().get();
    }

    private List<WSSak> hentSakerForAktor(String aktorId) {
        try {
            return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
        } catch (RuntimeException ex) {
            throw new SystemException("Feil ved kall til sakogbehandling", ex);
        }
    }

    private HentAktoerIdForIdentRequest lagAktorRequest(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        return request;
    }

    private static Predicate<WSBehandlingskjede> finnesKvitteringMedSammeBehandlingsid(final List<Kvittering> kvitteringer) {
        return new Predicate<WSBehandlingskjede>() {
            @Override
            public boolean evaluate(final WSBehandlingskjede wsBehandlingskjede) {
                boolean exists = on(kvitteringer).exists(new Predicate<Kvittering>() {
                    @Override
                    public boolean evaluate(Kvittering kvittering) {
                        return on(wsBehandlingskjede.getBehandlingsListeRef()).head().is(kvittering.behandlingsId);
                    }
                });
                return exists;
            }
        };
    }

}
