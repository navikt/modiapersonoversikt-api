package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktLerretTest extends AbstractWicketTest {

    private SaksoversiktLerret lerret;

    @Override
    protected void setup() {
        lerret = new SaksoversiktLerret("lerret", "");
        wicketTester.goToPageWith(lerret);
    }

    @Test
    public void skalÅpneLerretUtenFeil() {

    }
}
