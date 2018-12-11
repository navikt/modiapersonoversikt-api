package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.plukkoppgave.PlukkOppgaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static org.apache.wicket.authorization.IAuthorizationStrategy.ALLOW_ALL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PersonPageMockContext.class})
public class PlukkOppgavePanelTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private PlukkOppgaveService plukkOppgaveService;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private OppfolgingsinfoApiService oppfolgingsinfoApiService;

    @BeforeEach
    public void setUp() {
        wicket.tester.getApplication().getSecuritySettings().setAuthorizationStrategy(ALLOW_ALL);

        Melding sporsmal = new Melding().withId("sporsmal").withType(SPORSMAL_SKRIFTLIG).withTemagruppe(ARBD.toString()).withFerdigstiltDato(now());
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString(), anyString())).thenReturn(asList(sporsmal));
        when(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet()).thenReturn("4444");
    }

    @Test
    public void plukkerOppgaveOgSetterSessionAttributes() {
        when(plukkOppgaveService.plukkOppgaver(any(Temagruppe.class), anyString())).thenReturn(singletonList(new Oppgave("oppgaveId", "fnr", "henvendelseId")));

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"))
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgaver"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        DialogSession session = DialogSession.read(wicket.tester.getSession());
        assertThat(session.getTemagruppe(), is(notNullValue()));
        assertThat(session.getPlukkedeOppgaver().isEmpty(), is(false));
        assertThat(session.getOppgaveSomBesvares().isPresent(), is(true));
    }

    @Test
    public void plukkerIkkeOppgaveHvisTemagruppeIkkeErValgt() {
        PlukkOppgavePanel plukkoppgave = new PlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .submitWithAjaxButton(withId("plukkOppgaver"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("temagruppe.Required")));
    }

    @Test
    public void plukkeIkkeOppgaveHvisEnAlleredeErPlukket() {
        reset(plukkOppgaveService);

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"));

        Oppgave oppgave1 = new Oppgave("oppgave1", "fnr1", "henvendelse1");
        DialogSession session = DialogSession.read(wicket.tester.getSession())
                .withPlukkedeOppgaver(singletonList(oppgave1));

        wicket.inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgaver"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        verify(plukkOppgaveService, never()).plukkOppgaver(any(Temagruppe.class), anyString());
    }

    @Test
    public void girFeilmeldingHvisIngenOppgaverPaaTema() {
        when(plukkOppgaveService.plukkOppgaver(any(Temagruppe.class), anyString())).thenReturn(emptyList());

        PlukkOppgavePanel plukkoppgave = new PlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgaver"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("plukkoppgave.ingenoppgaverpaatemagruppe")));
    }

    @Test
    public void brukerIkkeOppgavePaaSesjonHvisDenErFerdigstilt() {
        reset(plukkOppgaveService);

        Oppgave oppgave1 = new Oppgave("oppgave1", "fnr1", "henvendelse1");
        Oppgave oppgave2 = new Oppgave("oppgave2", "fnr2", "henvendelse2");

        when(plukkOppgaveService.oppgaveErFerdigstilt(anyString())).thenReturn(true);
        when(plukkOppgaveService.plukkOppgaver(any(Temagruppe.class), anyString())).thenReturn(singletonList(oppgave1));

        DialogSession session = DialogSession.read(wicket.tester.getSession())
                .withPlukkedeOppgaver(singletonList(oppgave2));

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"));

        wicket.inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgaver"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        verify(plukkOppgaveService, times(1)).plukkOppgaver(any(Temagruppe.class), anyString());
        assertThat(session.getOppgaveSomBesvares().orElse(null), is(oppgave1));
    }
}
