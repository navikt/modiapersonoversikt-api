package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.WicketPageTest;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock.PlukkOppgavePanelMockContext;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel.ReferatPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.SvarPanel;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSBruker;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel.PlukkOppgavePanel.TEMAGRUPPE_ATTR;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PlukkOppgavePanelMockContext.class
})
public class PlukkOppgavePanelTest extends WicketPageTest {

    @Inject
    private SakService sakService;

    @Test
    public void skalPlukkeOppgaveOgSetteSessionAttribute() {
        when(sakService.plukkOppgaveFraGsak(anyString())).thenReturn(optional(
                new WSOppgave()
                        .withGjelder(new WSBruker().withBrukerId("fnr"))
                        .withOppgaveId("oppgave")));

        Sporsmal sporsmal = new Sporsmal("sporsmal", now());
        sporsmal.temagruppe = "HJELPEMIDLER";
        when(sakService.getSporsmalFromOppgaveId(anyString(), anyString())).thenReturn(sporsmal);

        wicket.goToPageWith(new TestPlukkOppgavePanel("plukkoppgave"))
                .inForm(withId("plukk-oppgave-form"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukk-oppgave"))
                .should().beOn(PersonPage.class)
                .should().containComponent(ofType(SvarPanel.class))
                .should().notContainComponent(ofType(ReferatPanel.class));

        Serializable temagruppeAttribute = wicket.get().component(ofType(PlukkOppgavePanel.class)).getSession().getAttribute(TEMAGRUPPE_ATTR);
        assertThat(temagruppeAttribute, is(notNullValue()));
    }

    @Test
    public void skalIkkePlukkeOppgaveHvisTemagruppeIkkeErValgt() {
        TestPlukkOppgavePanel plukkoppgave = new TestPlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukk-oppgave-form"))
                .submitWithAjaxButton(withId("plukk-oppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("temagruppe.Required")));
    }

    @Test
    public void skalGiFeilmeldingHvisIngenOppgaverPaaTema() {
        when(sakService.plukkOppgaveFraGsak(anyString())).thenReturn(Optional.<WSOppgave>none());

        TestPlukkOppgavePanel plukkoppgave = new TestPlukkOppgavePanel("plukkoppgave");
        wicket.goToPageWith(plukkoppgave)
                .inForm(withId("plukk-oppgave-form"))
                .select("temagruppe", 0)
                .submitWithAjaxButton(withId("plukk-oppgave"));

        List<String> errorMessages = wicket.get().errorMessages();
        assertThat(errorMessages, hasItem(plukkoppgave.getString("plukkoppgave.ingenoppgaverpaatemagruppe")));
    }
}
