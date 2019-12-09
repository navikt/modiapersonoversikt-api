package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;
import static no.nav.sbl.dialogarena.test.ssl.SSLTestUtils.setupKeyAndTrustStore;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = AFTER_CLASS)
@ContextConfiguration(classes = {ModiaApplicationContext.class})
public class ApplicationContextTest {

    @BeforeClass
    public static void setupStatic() {
        setFrom("configurations/q0.properties");
        setupKeyAndTrustStore();
    }

    @Test
    public void shouldSetupAppContext() {
    }

}
