package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.sak.transformers.TemaTransformer.tilTema;

public class SaksoversiktServiceImpl implements SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;
    @Inject
    private SakOgBehandlingService sakOgBehandlingService;
    @Inject
    private Filter filter;
    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;


    public List<Tema> hentTemaer(String fnr) {
        List<WSSak> saker = sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr));
        return filter.filtrerSaker(saker).stream()
                .map(wsSak -> tilTema(wsSak, bulletproofKodeverkService, filter))
                .sorted((o1, o2) -> o2.behandlingsdato.compareTo(o1.behandlingsdato))
                .collect(toList());
    }

    private String hentAktorId(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(lagAktorRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new SystemException("Klarte ikke hente aktørId", e);
        }
    }

    private HentAktoerIdForIdentRequest lagAktorRequest(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        return request;
    }
}
