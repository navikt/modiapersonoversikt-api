package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandling_v1PortType;
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
import static no.nav.sbl.dialogarena.saksoversikt.service.service.DataFletter.hentBehandlingerFraBehandlingskjeder;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BEHANDLING_STATUS;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.OPPRETTET;
import static org.slf4j.LoggerFactory.getLogger;

public class SakOgBehandlingService {

    private static final Logger logger = getLogger(SakOgBehandlingService.class);

    @Inject
    private SakOgBehandling_v1PortType sakOgBehandlingPortType;

    @Inject
    private Filter filter;

    @Inject
    private FodselnummerAktorService fnrAktor;

    public List<WSSak> hentSakerForAktor(String aktorId) {
        try {
            return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
        } catch (RuntimeException ex) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex);
            throw new SystemException("Feil ved kall til sakogbehandling", ex);
        }
    }

    public List<WSSak> hentAlleSaker(String fnr) {
        try {
            String aktorId = fnrAktor.hentAktorIdForFnr(fnr);
            List<WSSak> sobSaker = sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
            return filter.filtrerSaker(sobSaker);
        } catch (RuntimeException ex) {
            logger.error("Det skjedde en uventet feil mot Sak og Behandling", ex);
            throw new SystemException("Feil ved kall til sakogbehandling", ex);
        }
    }

    public Map<String, List<Behandlingskjede>> hentBehandlingskjederGruppertPaaTema(String fnr) {
        return hentAlleSaker(fnr)
                .stream()
                .collect(toMap(SAKSTEMA, TIL_BEHANDLINGSKJEDER));
    }

    private List<Record<GenerellBehandling>> filtrerteBehandlinger(WSSak sak) {
        return filter.filtrerBehandlinger(hentBehandlingerFraBehandlingskjeder(sak.getBehandlingskjede()));
    }

    private static final Function<WSSak, String> SAKSTEMA = wsSak -> wsSak.getSakstema().getValue();

    private static final Function<Record<GenerellBehandling>, Behandlingskjede> TIL_BEHANDLINGSKJEDE = generellBehandling -> new Behandlingskjede()
            .withStatus(finnBehandlingsstatus(generellBehandling))
            .withSistOppdatert(LocalDateTime.from(generellBehandling.get(GenerellBehandling.BEHANDLING_DATO).toGregorianCalendar().toZonedDateTime()));

    private final Function<WSSak, List<Behandlingskjede>> TIL_BEHANDLINGSKJEDER = sak -> behandlingskjederForSak(sak);

    private List<Behandlingskjede> behandlingskjederForSak(WSSak wsSak) {
        return filtrerteBehandlinger(wsSak)
                .stream()
                .map(TIL_BEHANDLINGSKJEDE)
                .collect(toList());
    }

    private static BehandlingsStatus finnBehandlingsstatus(Record<GenerellBehandling> generellBehandling) {
        return generellBehandling.get(BEHANDLING_STATUS).equals(OPPRETTET) ? BehandlingsStatus.UNDER_BEHANDLING : BehandlingsStatus.FERDIG_BEHANDLET;
    }
}
