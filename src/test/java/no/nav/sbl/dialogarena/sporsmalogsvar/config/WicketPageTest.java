package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.junit.Before;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ContextConfiguration(classes = WicketTesterConfig.class)
@DirtiesContext(classMode = AFTER_CLASS)
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
