package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.sbl.dialogarena.sak.AbstractWicketTest;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SaksoversiktLerretTest extends AbstractWicketTest {

    @Mock
    private SaksoversiktService service;
    private SaksoversiktLerret lerret;

    @Override
    protected void setup() {
        applicationContext.putBean(service);

        lerret = new SaksoversiktLerret("lerret", "");
        wicketTester.goToPageWith(lerret);
    }

    @Test
    public void skalÅpneLerretUtenFeil() {

    }
}
