package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static no.nav.modig.lang.option.Optional.optional;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlukkOppgaveServiceImplTest {

    @Mock
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Mock
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi;
    @Mock
    private EnforcementPoint pep;

    @InjectMocks
    private PlukkOppgaveServiceImpl plukkOppgaveService;

    private static HentKjerneinformasjonResponse personResponse = new HentKjerneinformasjonResponse();

    static {
        personResponse.setPerson(new Person.With()
                .personfakta(new Personfakta.With()
                        .harAnsvarligEnhet(new AnsvarligEnhet.With()
                                .organisasjonsenhet(new Organisasjonsenhet.With()
                                        .organisasjonselementId("1").done()).done()).done()).done());
        personResponse.getPerson().getPersonfakta().setDiskresjonskode("7");
    }

    @Before
    public void setUp() {
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(personResponse);
    }

    @Test
    public void girNoneHvisIngenOppgaveFraTjenesten() {
        when(oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.FMLI)).thenReturn(Optional.<Oppgave>none());

        assertThat(plukkOppgaveService.plukkOppgave(Temagruppe.FMLI), is(equalTo(Optional.<Oppgave>none())));
    }

    @Test
    public void girOppgaveHvisSaksbehandlerHarTilgang() {
        Optional<Oppgave> oppgave = optional(new Oppgave("oppgaveId", "fnr", "henvendelseId"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.FMLI)).thenReturn(oppgave);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        assertThat(plukkOppgaveService.plukkOppgave(Temagruppe.FMLI), is(equalTo(oppgave)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr", "1"));
        Optional<Oppgave> oppgave2 = optional(new Oppgave("2", "fnr", "2"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.FMLI)).thenReturn(oppgave1, oppgave2);
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(false, false, false, true);

        assertThat(plukkOppgaveService.plukkOppgave(Temagruppe.FMLI), is(equalTo(oppgave2)));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId), eq(Temagruppe.FMLI));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangTilSamtligePep() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr", "henvendelseId"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.FMLI)).thenReturn(oppgave1, Optional.<Oppgave>none());
        when(pep.hasAccess(any(PolicyRequest.class))).thenReturn(true, false);

        assertThat(plukkOppgaveService.plukkOppgave(Temagruppe.FMLI), is(equalTo(Optional.<Oppgave>none())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId), eq(Temagruppe.FMLI));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangFraKjerneinfo() {
        Optional<Oppgave> oppgave1 = optional(new Oppgave("1", "fnr", "henvendelseId"));

        when(oppgaveBehandlingService.plukkOppgaveFraGsak(Temagruppe.FMLI)).thenReturn(oppgave1, Optional.<Oppgave>none());
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenThrow(new AuthorizationException(""));

        assertThat(plukkOppgaveService.plukkOppgave(Temagruppe.FMLI), is(equalTo(Optional.<Oppgave>none())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get().oppgaveId), eq(Temagruppe.FMLI));
    }
}