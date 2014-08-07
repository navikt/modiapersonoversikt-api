package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.sbl.dialogarena.sak.mock.SakOgBehandlingMocks.TEMA;

@RunWith(SpringJUnit4ClassRunner.class)
public class SaksoversiktLerretTest extends AbstractWicketTest {

    private SaksoversiktLerret lerret;

    @Override
    protected void setup() {
        lerret = new SaksoversiktLerret("lerret", "123");
        wicketTester.goToPageWith(lerret);
    }

    @Test
    public void skalÅpneLerretUtenFeil() {
        lerret.hentNyeHendelser(TEMA);
    }
}
