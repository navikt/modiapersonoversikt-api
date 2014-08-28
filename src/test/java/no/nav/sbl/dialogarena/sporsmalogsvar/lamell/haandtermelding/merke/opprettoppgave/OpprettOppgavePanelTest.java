package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.opprettoppgave;

import no.nav.sbl.dialogarena.sporsmalogsvar.config.WicketPageTest;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.mock.ServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@ContextConfiguration(classes = {ServiceTestContext.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class OpprettOppgavePanelTest extends WicketPageTest {

    @Test
    public void opprettOppgavePanelInnholderAlleKomponenter() throws Exception {
        wicket.goToPageWith(lagOpprettOppgavePanel())
                .should().containComponent(withId("visNyOppgaveWrapper"));
    }

    @Test
    public void kanMerkesSomKontorSperretNaarManIkkeSkalOppretteOppgave() throws Exception {
        OpprettOppgavePanel oppgavePanel = lagOpprettOppgavePanel();
        oppgavePanel.skalOppretteOppgave.setObject(false);
        assertThat(oppgavePanel.kanMerkeSomKontorsperret(), is(true));
    }

    @Test
    public void kanIkkeMerkesSomKontorSperretNaarManSkalOppretteOppgaveOgDenIkkeErOpprettet() throws Exception {
        OpprettOppgavePanel oppgavePanel = lagOpprettOppgavePanel();
        oppgavePanel.skalOppretteOppgave.setObject(true);
        oppgavePanel.erOppgaveOpprettet.setObject(false);
        assertThat(oppgavePanel.kanMerkeSomKontorsperret(), is(false));
    }

    @Test
    public void kanMerkesSomKontorSperretNaarManHarOppretteOppgave() throws Exception {
        OpprettOppgavePanel oppgavePanel = lagOpprettOppgavePanel();
        oppgavePanel.skalOppretteOppgave.setObject(true);
        oppgavePanel.erOppgaveOpprettet.setObject(true);
        assertThat(oppgavePanel.kanMerkeSomKontorsperret(), is(true));
    }


    private OpprettOppgavePanel lagOpprettOppgavePanel() {
        return new OpprettOppgavePanel("id", mock(InnboksVM.class));
    }

}
