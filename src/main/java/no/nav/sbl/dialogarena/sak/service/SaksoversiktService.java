package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.temaVMTransformer;
import static org.slf4j.LoggerFactory.getLogger;

public class SaksoversiktService {

    private static final Logger LOG = getLogger(SaksoversiktService.class);

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private HenvendelseService henvendelseService;

    @Inject
    private SakOgBehandlingService sakOgBehandlingService;

    @Inject
    private SakOgBehandlingFilter filter;

    @Inject
    private DataFletter dataFletter;

    /**
     * Henter alle tema for en gitt person
     */
    public List<TemaVM> hentTemaer(String fnr) {
        LOG.info("Henter tema fra Sak og Behandling til Modiasaksoversikt. Fnr: " + fnr);
        List<WSSak> saker = on(sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr))).collect();
        PreparedIterable<TemaVM> temaer = on(filter.filtrerSaker(saker)).map(temaVMTransformer(filter));
        return temaer.collect(new SistOppdaterteBehandlingComparator());
    }

    /**
     * Henter alle behandlinger mappet på tema
     */
    public Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(String fnr) {
        return dataFletter.hentBehandlingerByTema(sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr)), henvendelseService.hentInnsendteSoknader(fnr));
    }

    private String hentAktorId(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(lagAktorRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new SystemException("Klarte ikke hente aktørId", hentAktoerIdForIdentPersonIkkeFunnet);
        }
    }

    private HentAktoerIdForIdentRequest lagAktorRequest(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        return request;
    }

<<<<<<< HEAD
=======
    private List<GenerellBehandling> hentSorterteBehandlinger(String fnr, WSSak sak) {
        return on(hentBehandlingerForSak(sak, fnr)).collect(new OmvendtKronologiskBehandlingComparator());
    }

    private List<GenerellBehandling> hentBehandlingerForSak(WSSak sak, String fnr) {
        List<WSBehandlingskjede> alleBehandlingskjeder = sak.getBehandlingskjede();
        List<GenerellBehandling> behandlinger = new ArrayList<>();

        List<Kvittering> kvitteringer = hentKvitteringer(fnr, BEHANDLINGSIDER_FRA_SAK.transform(sak));
        behandlinger.addAll(behandlingerSomIkkeErKvitteringer(alleBehandlingskjeder, kvitteringer));

        Map<String, Kvittering> kvitteringerForBehandlingsID = mapKvitteringMedBehandlingsID(kvitteringer);
        Map<String, WSBehandlingskjede> kjederForBehandlingsID = mappedeKjeder(kvitteringerForBehandlingsID.keySet(), finnBehandlingskjederSomHarKvittering(alleBehandlingskjeder, kvitteringer));
        for (String kvitteringsID : kvitteringerForBehandlingsID.keySet()) {
            behandlinger.add(beriketKvittering(kvitteringerForBehandlingsID.get(kvitteringsID), kjederForBehandlingsID.get(kvitteringsID)));
        }

        final String temakode = temaVMTransformer(filter).transform(sak).temakode;
        behandlinger = on(behandlinger).map(new Transformer<GenerellBehandling, GenerellBehandling>() {
            @Override public GenerellBehandling transform(GenerellBehandling generellBehandling) {
                return generellBehandling.withSaksTema(temakode);
            }
        }).collect();

        return behandlinger;
    }

    private Kvittering beriketKvittering(Kvittering kvittering, WSBehandlingskjede wsBehandlingskjede) {
        return (Kvittering) ((Kvittering) kvittering
                .withBehandlingsDato(behandlingsDato(wsBehandlingskjede)))
                .withAvsluttet(wsBehandlingskjede.getSlutt() != null)
                .withBehandlingsType(null) //setter eksplisitt for å unngå duplisering fra filteret
                .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede));
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
        try {
            return on(henvendelseSoknaderPortType.hentSoknadListe(fnr)).filter(where(INNSENDT, equalTo(true))).collect();
        } catch (Exception e) {
            throw new SystemException("Feil ved kall til henvendelse", e);
        }
    }

    private List<WSSak> hentSakerForAktor(String aktorId) {
        try {
            return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
        } catch (RuntimeException ex) {
            throw new SystemException("Feil ved kall til sakogbehandling", ex);
        }
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

>>>>>>> develop
}
