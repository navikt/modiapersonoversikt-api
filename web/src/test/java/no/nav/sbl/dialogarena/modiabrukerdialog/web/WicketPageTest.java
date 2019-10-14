package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.WicketTesterConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeAll
    public static void staticSetup() {
//        System.setProperty(StaticSubjectHandler.SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
//        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
//        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
//        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder("Z999999", IdentType.EksternBruker).withAuthLevel(4).getSubject());
    }

    @BeforeEach
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
