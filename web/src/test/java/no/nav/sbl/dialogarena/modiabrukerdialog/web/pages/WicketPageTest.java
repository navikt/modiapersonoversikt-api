package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.junit.Before;

import javax.inject.Inject;

public abstract class WicketPageTest {

    @Inject
    protected FluentWicketTester<?> wicket;

    @Before
    public void setup() {
        wicket.tester.getSession().replaceSession();
        additionalSetup();
    }

    /**
     * Override this method to provide @Before functionality
     */
    protected void additionalSetup() { }

}
