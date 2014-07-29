package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.util.SystemProperties.load;

@ContextConfiguration(classes = WicketTesterConfig.class)
public abstract class WicketPageTest {

    @Inject
    protected FluentWicketTester<?> wicket;

    @BeforeClass
    public static void staticSetup() {
        load("/jetty-environment.properties");
    }

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
