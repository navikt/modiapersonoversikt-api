package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.createPersonResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlukkOppgaveServiceTest {

    @Mock
    private OppgaveBehandlingService oppgaveBehandlingService;

    @Mock
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;

    @Mock
    private EnforcementPoint pep;

    @InjectMocks
    private PlukkOppgaveService plukkOppgaveService;

    @Before
    public void setUp() {
        HentKjerneinformasjonResponse personResponse = createPersonResponse();
        personResponse.getPerson().getPersonfakta().setDiskresjonskode("7");
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(personResponse);
    }

    @Test
    public void girNoneHvisIngenOppgaveFraTjenesten() {
        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(Optional.<Oppgave>none());

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(Optional.<Oppgave>none())));
    }

    @Test
    public void girOppgaveHvisSaksbehandlerHarTilgang() {
        Optional<Oppgave> oppgave = optional(new Oppgave("oppgaveId", "fnr"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(oppgave);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(oppgave)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr"));
        Optional<Oppgave> oppgave2 = optional(new Oppgave("2", "fnr"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(oppgave1, oppgave2);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false, false, false, true);

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(oppgave2)));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangTilSamtligePep() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(oppgave1, Optional.<Oppgave>none());
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true, false);

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(Optional.<Oppgave>none())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangFraKjerneinfo() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(oppgave1, Optional.<Oppgave>none());
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenThrow(new AuthorizationException(""));

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(Optional.<Oppgave>none())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId));
    }
}