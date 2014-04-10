package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.lang.System.getProperties;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.DEFAULT_MOCK_TILLATT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ModiaApplicationContext.class})
public class ApplicationContextTest {

    @BeforeClass
    public static void setupStatic() {
        setFrom("jetty-environment.properties");
        getProperties().setProperty(TILLATMOCKSETUP_PROPERTY, DEFAULT_MOCK_TILLATT);
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() { }

}
