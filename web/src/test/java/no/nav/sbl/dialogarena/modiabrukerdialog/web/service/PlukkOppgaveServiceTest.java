package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.OppgaveBehandlingService;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.PersonKjerneinfoServiceBiMock.createPersonResponse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.GsakOppgaveV3PortTypeMock.lagWSOppgave;
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
        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(Optional.<WSOppgave>none());

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(Optional.<WSOppgave>none())));
    }

    @Test
    public void girOppgaveHvisSaksbehandlerHarTilgang() {
        Optional<WSOppgave> wsOppgave = optional(lagWSOppgave());

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(wsOppgave);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(wsOppgave)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        Optional<WSOppgave> wsOppgave1 = optional(lagWSOppgave("1"));
        Optional<WSOppgave> wsOppgave2 = optional(lagWSOppgave("2"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(anyString())).thenReturn(wsOppgave1, wsOppgave2);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false, true);

        assertThat(plukkOppgaveService.plukkOppgave("temagruppe"), is(equalTo(wsOppgave2)));
        verify(oppgaveBehandlingService).leggTilbakeOppgaveIGsak(eq(optional(wsOppgave1.get().getOppgaveId())), anyString(), anyString());
    }

}