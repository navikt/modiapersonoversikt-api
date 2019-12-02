package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.binding.SakOgBehandlingV1;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.SAK_OG_BEHANDLING;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.Transformers.TIL_BEHANDLING;
import static org.slf4j.LoggerFactory.getLogger;

public class SakOgBehandlingService {

    private static final Logger logger = getLogger(SakOgBehandlingService.class);

    @Inject
    private SakOgBehandlingV1 sakOgBehandlingPortType;

    @Inject
    private Filter filter;

    @Inject
    private FodselnummerAktorService fnrAktor;

    private static List<Behandling> hentBehandlingerFraBehandlingskjeder(List<Behandlingskjede> behandlingskjedeListe) {
        return behandlingskjedeListe.stream()
                .map(TIL_BEHANDLING)
                .collect(toList());
    }

    protected List<Sak> hentAlleSaker(String fnr) {
        try {
            String aktorId = fnrAktor.hentAktorIdForFnr(fnr);
            FinnSakOgBehandlingskjedeListeRequest request = new FinnSakOgBehandlingskjedeListeRequest();
            request.setAktoerREF(aktorId);
            List<Sak> sobSaker = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(request).getSak();
            return filter.filtrerSaker(sobSaker);
        } catch (RuntimeException ex) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex);
            throw new FeilendeBaksystemException(SAK_OG_BEHANDLING);
        }
    }

    public Map<String, List<no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede>> hentBehandlingskjederGruppertPaaTema(String fnr) {
        return hentAlleSaker(fnr)
                .stream()
                .collect(toMap(SAKSTEMA, (wsSak) -> tilBehandligskjeder(wsSak)));
    }

    private List<Behandling> filtrerteBehandlinger(Sak sak) {
        return filter.filtrerBehandlinger(hentBehandlingerFraBehandlingskjeder(sak.getBehandlingskjede()));
    }

    private static final Function<Sak, String> SAKSTEMA = wsSak -> wsSak.getSakstema().getValue();

    private static final Function<Behandling, no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede> TIL_BEHANDLINGSKJEDE = behandling -> new no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede()
            .withStatus(behandling.getBehandlingsStatus())
            .withSistOppdatert(LocalDateTime.from(behandling.getBehandlingDato().toGregorianCalendar().toZonedDateTime()));

    private List<no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede> tilBehandligskjeder(Sak wsSak) {
        return filtrerteBehandlinger(wsSak)
                .stream()
                .map(TIL_BEHANDLINGSKJEDE)
                .collect(toList());
    }
}
