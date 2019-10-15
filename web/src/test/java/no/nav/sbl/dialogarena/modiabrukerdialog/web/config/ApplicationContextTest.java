package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static java.lang.System.getProperties;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.DEFAULT_MOCK_TILLATT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ModiaApplicationContext.class})
public class ApplicationContextTest {

    @BeforeClass
    public static void setupStatic() {
        setFrom("jetty-environment.properties");
        getProperties().setProperty(TILLATMOCKSETUP_PROPERTY, DEFAULT_MOCK_TILLATT);
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() {
    }

}
