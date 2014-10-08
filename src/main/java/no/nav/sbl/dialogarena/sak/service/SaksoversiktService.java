package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
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
        return on(filter.filtrerSaker(saker)).map(temaVMTransformer(filter)).collect(new SistOppdaterteBehandlingComparator());
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

}
