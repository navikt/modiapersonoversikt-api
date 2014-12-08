package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.kontorsperre;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.nyoppgaveformwrapper.NyOppgaveFormWrapper;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class KontorsperrePanelTest extends WicketPageTest {

    @Test
    public void opprettOppgavePanelInnholderAlleKomponenter() throws Exception {
        wicket.goToPageWith(lagOpprettOppgavePanel())
                .should().containComponent(withId("opprettOppgaveCheckboxWrapper").and(ofType(WebMarkupContainer.class)))
                .should().containComponent(withId("opprettOppgaveCheckbox").and(ofType(CheckBox.class)))
                .should().containComponent(withId("nyoppgaveForm").and(ofType(NyOppgaveFormWrapper.class)));
    }

    @Test
    public void kanMerkesSomKontorSperretNaarManIkkeSkalOppretteOppgave() throws Exception {
        KontorsperrePanel oppgavePanel = lagOpprettOppgavePanel();
        oppgavePanel.skalOppretteOppgave.setObject(false);
        assertThat(oppgavePanel.kanMerkeSomKontorsperret(), is(true));
    }

    private KontorsperrePanel lagOpprettOppgavePanel() {
        return new KontorsperrePanel("id", mock(InnboksVM.class));
    }

}
