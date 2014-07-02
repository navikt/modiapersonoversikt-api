package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.core.exception.SystemException;
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
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalToIgnoreCase;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sak.transformers.HenvendelseTransformers.KVITTERING;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.BEHANDLINGSIDER_FRA_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.TEMAKODE_FOR_SAK;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.TEMA_VM;


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
        return on(hentSakerForAktor(hentAktorId(fnr))).map(TEMA_VM).collect(new SistOppdaterteBehandlingComparator());
    }

    /**
     * Henter alle behandlinger for et gitt tema fra flere baksystemer
     */
    public List<GenerellBehandling> hentBehandlingerForTemakode(String fnr, String temakode) {
        List<GenerellBehandling> behandlinger = new ArrayList<>();
        WSSak wsSak = hentSakForAktorPaaTema(hentAktorId(fnr), temakode);
        List<Kvittering> kvitteringerForTema = hentKvitteringer(fnr, BEHANDLINGSIDER_FRA_SAK.transform(wsSak));
        behandlinger.addAll(kvitteringerForTema);
        return behandlinger;
    }

    private List<Kvittering> hentKvitteringer(String fnr, List<String> behandlingIDer) {
        List<Kvittering> kvitteringer = new ArrayList<>();
        for (String behandlingId : behandlingIDer) {
            leggTilKvitteringHvisEksisterer(fnr, behandlingId, kvitteringer);
        }
        return kvitteringer;
    }

    private void leggTilKvitteringHvisEksisterer(String fnr, String behandlingId, List<Kvittering> kvitteringer) {
        List<WSSoknad> soknader = henvendelseSoknaderPortType.hentSoknadListe(fnr);
        for (WSSoknad soknad : soknader) {
            if (soknad.getBehandlingsId().equals(behandlingId)) {
                kvitteringer.add(KVITTERING.transform(soknad));
            }
        }
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

}
