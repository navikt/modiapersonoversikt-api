package no.nav.modiapersonoversikt.service;

import no.nav.modiapersonoversikt.integration.abac.AbacRequest;
import no.nav.modiapersonoversikt.integration.abac.AbacResponse;
import no.nav.modiapersonoversikt.integration.abac.Decision;
import no.nav.modiapersonoversikt.integration.abac.Response;
import no.nav.modiapersonoversikt.api.domain.Oppgave;
import no.nav.modiapersonoversikt.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.api.service.OppgaveBehandlingService;
import no.nav.modiapersonoversikt.api.utils.http.SubjectHandlerUtil;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext;
import no.nav.modiapersonoversikt.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class PlukkOppgaveServiceImplTest {

    private static final String SAKSBEHANDLERS_VALGTE_ENHET = "4200";

    private final OppgaveBehandlingService oppgaveBehandlingService = mock(OppgaveBehandlingService.class);
    private final TilgangskontrollContext tilgangskontrollContext = mock(TilgangskontrollContext.class);
    private final Tilgangskontroll tilgangskontroll = new Tilgangskontroll(tilgangskontrollContext);
    private final PlukkOppgaveServiceImpl plukkOppgaveService = new PlukkOppgaveServiceImpl(
            oppgaveBehandlingService,
            tilgangskontroll
    );

    @Test
    public void girEmptyHvisIngenOppgaveFraTjenesten() {
        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(emptyList());

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(emptyList())));
    }

    @Test
    public void girOppgaveHvisSaksbehandlerHarTilgang() {
        List<Oppgave> oppgaver = singletonList(new Oppgave("oppgaveId", "fnr", "behandlingskjedeId", true));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgaver);
        when(tilgangskontrollContext.checkAbac(any(AbacRequest.class))).thenReturn(
                new AbacResponse(singletonList(new Response(Decision.Permit, emptyList())))
        );
        List<Oppgave> oppgaverResponse = SubjectHandlerUtil.withIdent("Z9990322", () -> plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET));

        assertThat(oppgaverResponse, is(equalTo(oppgaver)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        List<Oppgave> oppgave1 = singletonList(new Oppgave("1", "fnr1", "1", true));
        List<Oppgave> oppgave2 = singletonList(new Oppgave("2", "fnr2", "2", true));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgave1, oppgave2);
        when(tilgangskontrollContext.checkAbac(any(AbacRequest.class))).thenReturn(
                new AbacResponse(singletonList(new Response(Decision.Deny, emptyList()))),
                new AbacResponse(singletonList(new Response(Decision.Permit, emptyList())))
        );
        List<Oppgave> oppgaver = SubjectHandlerUtil.withIdent("Z9990322", () -> plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET));
        assertThat(oppgaver, is(equalTo(oppgave2)));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get(0).oppgaveId), eq(Temagruppe.FMLI), eq(SAKSBEHANDLERS_VALGTE_ENHET));
    }
}
