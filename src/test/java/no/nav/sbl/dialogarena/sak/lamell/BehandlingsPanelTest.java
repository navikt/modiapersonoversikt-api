package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BehandlingsPanelTest extends AbstractWicketTest {

    private BehandlingsPanel behandlingsPanel;

    @Override
    protected void setup() {
        behandlingsPanel = new BehandlingsPanel("id", null);
        wicketTester.goToPageWith(behandlingsPanel);
    }

    @Test
    public void shouldOpen_pageWithComponent() {

    }

}
