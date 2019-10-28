package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.Filter;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
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
    private SakOgBehandling_v1PortType sakOgBehandlingPortType;

    @Inject
    private Filter filter;

    @Inject
    private FodselnummerAktorService fnrAktor;

    private static List<Behandling> hentBehandlingerFraBehandlingskjeder(List<WSBehandlingskjede> behandlingskjedeListe) {
        return behandlingskjedeListe.stream()
                .map(TIL_BEHANDLING)
                .collect(toList());
    }

    protected List<WSSak> hentAlleSaker(String fnr) {
        try {
            String aktorId = fnrAktor.hentAktorIdForFnr(fnr);
            List<WSSak> sobSaker = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
            return filter.filtrerSaker(sobSaker);
        } catch (RuntimeException ex) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex);
            throw new FeilendeBaksystemException(SAK_OG_BEHANDLING);
        }
    }

    public Map<String, List<Behandlingskjede>> hentBehandlingskjederGruppertPaaTema(String fnr) {
        return hentAlleSaker(fnr)
                .stream()
                .collect(toMap(SAKSTEMA, (wsSak) -> tilBehandligskjeder(wsSak)));
    }

    private List<Behandling> filtrerteBehandlinger(WSSak sak) {
        return filter.filtrerBehandlinger(hentBehandlingerFraBehandlingskjeder(sak.getBehandlingskjede()));
    }

    private static final Function<WSSak, String> SAKSTEMA = wsSak -> wsSak.getSakstema().getValue();

    private static final Function<Behandling, Behandlingskjede> TIL_BEHANDLINGSKJEDE = behandling -> new Behandlingskjede()
            .withStatus(behandling.getBehandlingsStatus())
            .withSistOppdatert(LocalDateTime.from(behandling.getBehandlingDato().toGregorianCalendar().toZonedDateTime()));

    private List<Behandlingskjede> tilBehandligskjeder(WSSak wsSak) {
        return filtrerteBehandlinger(wsSak)
                .stream()
                .map(TIL_BEHANDLINGSKJEDE)
                .collect(toList());
    }
}
