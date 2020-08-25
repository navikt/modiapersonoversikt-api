package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.sbl.dialogarena.abac.AbacRequest;
import no.nav.sbl.dialogarena.abac.AbacResponse;
import no.nav.sbl.dialogarena.abac.Decision;
import no.nav.sbl.dialogarena.abac.Response;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
        List<Oppgave> oppgaver = singletonList(new Oppgave("oppgaveId", "fnr", "behandlingskjedeId"));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgaver);
        when(tilgangskontrollContext.checkAbac(any(AbacRequest.class))).thenReturn(
                new AbacResponse(singletonList(new Response(Decision.Permit, emptyList())))
        );
        List<Oppgave> oppgaverResponse = SubjectHandlerUtil.withIdent("Z9990322", () -> plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET));

        assertThat(oppgaverResponse, is(equalTo(oppgaver)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        List<Oppgave> oppgave1 = singletonList(new Oppgave("1", "fnr1", "1"));
        List<Oppgave> oppgave2 = singletonList(new Oppgave("2", "fnr2", "2"));

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
