package no.nav.sbl.dialogarena.modiabrukerdialog.web.service;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.kjerneinfo.domain.person.Personfakta;
import no.nav.kjerneinfo.domain.person.fakta.AnsvarligEnhet;
import no.nav.kjerneinfo.domain.person.fakta.Organisasjonsenhet;
import no.nav.modig.core.exception.AuthorizationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlukkOppgaveServiceImplTest {

    private static final String SAKSBEHANDLERS_VALGTE_ENHET = "4200";

    private OppgaveBehandlingService oppgaveBehandlingService = mock(OppgaveBehandlingService.class);
    private PersonKjerneinfoServiceBi personKjerneinfoServiceBi = mock(PersonKjerneinfoServiceBi.class);
    private TilgangskontrollContext tilgangskontrollContext = mock(TilgangskontrollContext.class);
    private Tilgangskontroll tilgangskontroll = new Tilgangskontroll(tilgangskontrollContext);

    private PlukkOppgaveServiceImpl plukkOppgaveService = new PlukkOppgaveServiceImpl(
            oppgaveBehandlingService,
            personKjerneinfoServiceBi,
            tilgangskontroll
    );

    private static HentKjerneinformasjonResponse personResponse = new HentKjerneinformasjonResponse();

    static {
        Personfakta personfakta = new Personfakta();
        personfakta.setAnsvarligEnhet(new AnsvarligEnhet.With()
                .organisasjonsenhet(new Organisasjonsenhet.With().organisasjonselementId("1").done()).done());
        personfakta.setDiskresjonskode(new Kodeverdi("SPFO", null));
        personResponse.setPerson(new Person.With()
                .personfakta(personfakta).done());
    }

    @BeforeEach
    public void setUp() {
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenReturn(personResponse);
    }

    @Test
    public void girEmptyHvisIngenOppgaveFraTjenesten() {
        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(emptyList());

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(emptyList())));
    }

    @Test
    public void girOppgaveHvisSaksbehandlerHarTilgang() {
        List<Oppgave> oppgaver = singletonList(new Oppgave("oppgaveId", "fnr", "behandlingskjedeId"));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgaver);
        when(tilgangskontrollContext.harSaksbehandlerRolle("0000-ga-bd06_modiagenerelltilgang")).thenReturn(true);
//        when(tilgangskontroll.hasAccess(any(PolicyRequest.class))).thenReturn(true);

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(oppgaver)));
    }

    @Test
    public void leggerTilbakeOppgaveOgPlukkerNyHvisSaksbehandlerIkkeHarTilgang() {
        List<Oppgave> oppgave1 = singletonList(new Oppgave("1", "fnr1", "1"));
        List<Oppgave> oppgave2 = singletonList(new Oppgave("2", "fnr2", "2"));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET )).thenReturn(oppgave1, oppgave2);
        when(tilgangskontrollContext.harSaksbehandlerRolle("0000-ga-bd06_modiagenerelltilgang")).thenReturn(true);

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(oppgave2)));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get(0).oppgaveId), eq(Temagruppe.FMLI), eq(SAKSBEHANDLERS_VALGTE_ENHET));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangTilSamtligePep() {
        List<Oppgave> oppgave1 = singletonList(new Oppgave("1", "fnr", "behandlingskjedeId"));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgave1, emptyList());

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(emptyList())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get(0).oppgaveId), eq(Temagruppe.FMLI), eq(SAKSBEHANDLERS_VALGTE_ENHET));
    }

    @Test
    public void leggerTilbakeHvisIkkeTilgangFraKjerneinfo() {
        List<Oppgave> oppgave1 = singletonList(new Oppgave("1", "fnr", "behandlingskjedeId"));

        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(oppgave1, emptyList());
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any(HentKjerneinformasjonRequest.class))).thenThrow(new AuthorizationException(""));

        assertThat(plukkOppgaveService.plukkOppgaver(Temagruppe.FMLI, SAKSBEHANDLERS_VALGTE_ENHET), is(equalTo(emptyList())));
        verify(oppgaveBehandlingService).systemLeggTilbakeOppgaveIGsak(eq(oppgave1.get(0).oppgaveId), eq(Temagruppe.FMLI), eq(SAKSBEHANDLERS_VALGTE_ENHET));
    }

    @Test
    public void brukerUtenAnsvarligEnhetTilgangssjekkesPaaTomStreng() {
        when(personKjerneinfoServiceBi.hentKjerneinformasjon(any())).thenReturn(mockPersonUtenAnsvarligEnhet());
        when(oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET)).thenReturn(singletonList(new Oppgave("1", "fnr", "1")));
        when(tilgangskontrollContext.harSaksbehandlerRolle("0000-ga-bd06_modiagenerelltilgang")).thenReturn(true);

        List<Oppgave> oppgave = plukkOppgaveService.plukkOppgaver(Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET);

        assertThat(oppgave.isEmpty(), is(false));
    }

    private HentKjerneinformasjonResponse mockPersonUtenAnsvarligEnhet() {
        HentKjerneinformasjonResponse hentKjerneinformasjonResponse = new HentKjerneinformasjonResponse();
        Person person = new Person();
        Personfakta personfakta = new Personfakta();
        person.setPersonfakta(personfakta);
        hentKjerneinformasjonResponse.setPerson(person);
        return hentKjerneinformasjonResponse;
    }

}
