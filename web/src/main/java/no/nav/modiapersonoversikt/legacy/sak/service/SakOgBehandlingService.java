package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandling;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.FeilendeBaksystemException;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem.SAK_OG_BEHANDLING;
import static no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter.filtrerBehandlinger;
import static no.nav.modiapersonoversikt.legacy.sak.service.filter.Filter.filtrerSaker;
import static no.nav.modiapersonoversikt.legacy.sak.transformers.Transformers.TIL_BEHANDLING;
import static org.slf4j.LoggerFactory.getLogger;

public class SakOgBehandlingService {

    private static final Logger logger = getLogger(SakOgBehandlingService.class);

    @Autowired
    private SakOgBehandlingV1 sakOgBehandlingPortType;

    @Autowired
    private PdlOppslagService pdlOppslagService;

    private static List<Behandling> hentBehandlingerFraBehandlingskjeder(List<Behandlingskjede> behandlingskjedeListe) {
        return behandlingskjedeListe.stream()
                .map(TIL_BEHANDLING)
                .collect(toList());
    }

    protected List<Sak> hentAlleSaker(String fnr) {
        try {
            String aktorId = pdlOppslagService.hentAktorId(fnr);
            FinnSakOgBehandlingskjedeListeRequest request = new FinnSakOgBehandlingskjedeListeRequest();
            request.setAktoerREF(aktorId);
            List<Sak> sobSaker = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(request).getSak();
            return filtrerSaker(sobSaker);
        } catch (RuntimeException ex) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex);
            throw new FeilendeBaksystemException(SAK_OG_BEHANDLING);
        }
    }

    public Map<String, List<no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede>> hentBehandlingskjederGruppertPaaTema(String fnr) {
        return hentAlleSaker(fnr)
                .stream()
                .collect(toMap(SAKSTEMA, (wsSak) -> tilBehandligskjeder(wsSak)));
    }

    private List<Behandling> filtrerteBehandlinger(Sak sak) {
        return filtrerBehandlinger(hentBehandlingerFraBehandlingskjeder(sak.getBehandlingskjede()));
    }

    private static final Function<Sak, String> SAKSTEMA = wsSak -> wsSak.getSakstema().getValue();

    private static final Function<Behandling, no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede> TIL_BEHANDLINGSKJEDE = behandling -> new no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede()
            .withStatus(behandling.getBehandlingsStatus())
            .withSistOppdatert(LocalDateTime.from(behandling.getBehandlingDato().toGregorianCalendar().toZonedDateTime()));

    private List<no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede> tilBehandligskjeder(Sak wsSak) {
        return filtrerteBehandlinger(wsSak)
                .stream()
                .map(TIL_BEHANDLINGSKJEDE)
                .collect(toList());
    }
}
