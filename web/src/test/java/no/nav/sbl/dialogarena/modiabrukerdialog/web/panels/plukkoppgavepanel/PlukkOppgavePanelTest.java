package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PlukkOppgavePanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.PlukkOppgaveService;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSBruker;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_FNR_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.VALGT_OPPGAVE_ID_ATTR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel.TEMAGRUPPE_ATTR;
import static org.apache.wicket.authorization.IAuthorizationStrategy.ALLOW_ALL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PlukkOppgavePanelMockContext.class)
public class PlukkOppgavePanelTest extends WicketPageTest {

    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;
    @Inject
    private PlukkOppgaveService plukkOppgaveService;

    @Before
    public void setUp() {
        wicket.tester.getApplication().getSecuritySettings().setAuthorizationStrategy(ALLOW_ALL);

        Sporsmal sporsmal = new Sporsmal("sporsmal", now());
        sporsmal.temagruppe = Temagruppe.ARBD.toString();
        when(henvendelseUtsendingService.getSporsmalFromOppgaveId(anyString(), anyString())).thenReturn(sporsmal);
    }

    @Test
    public void skalPlukkeOppgaveOgSetteSessionAttributes() {
        when(plukkOppgaveService.plukkOppgave(anyString())).thenReturn(optional(
                new WSOppgave()
                        .withGjelder(new WSBruker().withBrukerId("fnr"))
                        .withOppgaveId("oppgave")
        ));

        wicket.goToPageWith(new TestPlukkOppgavePanel("plukkoppgave"))
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(SvarPanel.class))
                .should().notContainComponent(ofType(ReferatPanel.class));

        Serializable temagruppeAttribute = wicket.tester.getSession().getAttribute(TEMAGRUPPE_ATTR);
        Serializable fnrAttribute = wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_FNR_ATTR);
        Serializable oppgaveidAttribute = wicket.tester.getSession().getAttribute(VALGT_OPPGAVE_ID_ATTR);
        assertThat(temagruppeAttribute, is(notNullValue()));
        assertThat(fnrAttribute, is(notNullValue()));
        assertThat(oppgaveidAttribute, is(notNullValue()));
    }

    @Test
    public void skalIkkePlukkeOppgaveHvisTemagruppeIkkeErValgt() {
        TestPlukkOppgavePanel plukkoppgave = new TestPlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .submitWithAjaxButton(withId("plukkOppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("temagruppe.Required")));
    }

    @Test
    public void skalIkkePlukkeOppgaveHvisEnAlleredeErPlukket() {
        wicket.goToPageWith(new TestPlukkOppgavePanel("plukkoppgave"));
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_FNR_ATTR, "fnr");
        wicket.tester.getSession().setAttribute(VALGT_OPPGAVE_ID_ATTR, "oppgaveid");
        wicket
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(SvarPanel.class))
                .should().notContainComponent(ofType(ReferatPanel.class));

        verify(plukkOppgaveService, never()).plukkOppgave(anyString());
    }

    @Test
    public void skalGiFeilmeldingHvisIngenOppgaverPaaTema() {
        when(plukkOppgaveService.plukkOppgave(anyString())).thenReturn(Optional.<WSOppgave>none());

        TestPlukkOppgavePanel plukkoppgave = new TestPlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukkOppgaveForm"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukkOppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("plukkoppgave.ingenoppgaverpaatemagruppe")));
    }
}
