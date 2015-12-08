package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PersonPageMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.FortsettDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.nydialogpanel.NyDialogPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ARBD;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel.TEMAGRUPPE_ATTR;
import static org.apache.wicket.authorization.IAuthorizationStrategy.ALLOW_ALL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersonPageMockContext.class})
public class PlukkOppgavePanelTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private PlukkOppgaveService plukkOppgaveService;

    @Before
    public void setUp() {
        wicket.tester.getApplication().getSecuritySettings().setAuthorizationStrategy(ALLOW_ALL);

        Melding sporsmal = new Melding().withId("sporsmal").withType(SPORSMAL_SKRIFTLIG).withTemagruppe(ARBD.toString()).withOpprettetDato(now());
        when(henvendelseUtsendingService.hentTraad(anyString(), anyString())).thenReturn(asList(sporsmal));
    }

    @Test
    public void plukkerOppgaveOgSetterSessionAttributes() {
        when(plukkOppgaveService.plukkOppgave(any(Temagruppe.class))).thenReturn(optional(new Oppgave("oppgaveId", "fnr", "henvendelseId")));

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"))
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        Serializable temagruppeAttribute = wicket.tester.getSession().getAttribute(TEMAGRUPPE_ATTR);
        Serializable fnrAttribute = wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR);
        Serializable oppgaveidAttribute = wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR);
        Serializable henvendelseidAttribute = wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR);
        assertThat(temagruppeAttribute, is(notNullValue()));
        assertThat(fnrAttribute, is(notNullValue()));
        assertThat(oppgaveidAttribute, is(notNullValue()));
        assertThat(henvendelseidAttribute, is(notNullValue()));
    }

    @Test
    public void plukkerIkkeOppgaveHvisTemagruppeIkkeErValgt() {
        PlukkOppgavePanel plukkoppgave = new PlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .submitWithAjaxButton(withId("plukkOppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("temagruppe.Required")));
    }

    @Test
    public void plukkeIkkeOppgaveHvisEnAlleredeErPlukket() {
        reset(plukkOppgaveService);

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"));
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, "fnr");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, "oppgaveid");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, "henvendelseid");

        wicket.inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        verify(plukkOppgaveService, never()).plukkOppgave(any(Temagruppe.class));
    }

    @Test
    public void girFeilmeldingHvisIngenOppgaverPaaTema() {
        when(plukkOppgaveService.plukkOppgave(any(Temagruppe.class))).thenReturn(Optional.<Oppgave>none());

        PlukkOppgavePanel plukkoppgave = new PlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("plukkoppgave.ingenoppgaverpaatemagruppe")));
    }

    @Test
    public void brukerIkkeOppgavePaaSesjonHvisDenErFerdigstilt() {
        reset(plukkOppgaveService);

        when(plukkOppgaveService.oppgaveErFerdigstilt(anyString())).thenReturn(true);
        when(plukkOppgaveService.plukkOppgave(any(Temagruppe.class))).thenReturn(optional(new Oppgave("oppgave2", "fnr2", "henvendelse2")));

        wicket.goToPageWith(new PlukkOppgavePanel("plukkoppgave"));
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, "fnr");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, "oppgaveid");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR, "henvendelseid");

        wicket.inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(FortsettDialogPanel.class))
                .should().notContainComponent(ofType(NyDialogPanel.class));

        verify(plukkOppgaveService, times(1)).plukkOppgave(any(Temagruppe.class));
        assertThat(wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR), is((Serializable) "oppgave2"));
        assertThat(wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR), is((Serializable) "fnr2"));
        assertThat(wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_HENVENDELSEID_ATTR), is((Serializable) "henvendelse2"));
    }
}
