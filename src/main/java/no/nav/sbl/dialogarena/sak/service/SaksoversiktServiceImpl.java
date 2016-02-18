package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.transformers.FilterImpl;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.temaVMTransformer;
import static org.slf4j.LoggerFactory.getLogger;

public class SaksoversiktServiceImpl implements SaksoversiktService {

    private static final Logger LOG = getLogger(SaksoversiktServiceImpl.class);

    @Inject
    private AktoerPortType fodselnummerAktorService;
    @Inject
    private SakOgBehandlingService sakOgBehandlingService;
    @Inject
    private FilterImpl filter;
    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Override
    @SuppressWarnings("PMD")
    public List<Tema> hentTemaer(String fnr) {
        LOG.info("Henter tema fra Sak og Behandling til Modiasaksoversikt. Fnr: " + fnr);
        List<WSSak> saker = on(sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr))).collect();
        PreparedIterable<Tema> temaer = on(filter.filtrerSaker(saker)).map(temaVMTransformer(filter, bulletproofKodeverkService));
        try {
            return temaer.collect(new SistOppdaterteBehandlingComparator());
        } catch (NullPointerException npe) {
            throw new ApplicationException("Nullpointer i service, antar comparator", npe);
        }
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
