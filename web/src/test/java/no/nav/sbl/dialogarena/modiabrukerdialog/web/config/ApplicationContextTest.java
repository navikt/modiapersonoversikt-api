package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DirtiesContext(classMode = AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ModiaApplicationContext.class})
public class ApplicationContextTest {

    @BeforeClass
    public static void setupStatic() {
        setFrom("test.properties");
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() { }

}
