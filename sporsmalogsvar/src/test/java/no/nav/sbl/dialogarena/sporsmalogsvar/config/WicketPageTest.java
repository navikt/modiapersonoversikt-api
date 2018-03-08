package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

@ContextConfiguration(classes = WicketTesterConfig.class)
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
    protected void additionalSetup() {
    }

}
