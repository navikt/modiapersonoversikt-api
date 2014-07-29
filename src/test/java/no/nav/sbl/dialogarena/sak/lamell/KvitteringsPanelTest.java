package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class KvitteringsPanelTest extends AbstractWicketTest {

    private KvitteringsPanel kvitteringsPanel;

    @Override
    protected void setup() {
        kvitteringsPanel = new KvitteringsPanel("id", null);
        wicketTester.goToPageWith(kvitteringsPanel);
    }

    @Test
    public void shouldOpen_pageWithComponent() {

    }

}

