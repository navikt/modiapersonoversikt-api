package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = WicketTesterConfig.class)
public abstract class WicketPageTest {

    @Inject
    protected FluentWicketTester<?> wicket;

    protected WicketPageTest() {
        System.setProperty("modiabrukerdialog.datadir", System.getProperty("java.io.tmpdir"));
    }

    @BeforeClass
    public static void staticSetup() {
        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
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
