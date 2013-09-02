package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@DirtiesContext
@ActiveProfiles({"test"})
public class TestSecurityBaseClass {

    @BeforeClass
    public static void setupStatic() {
        SystemProperties.setFrom("environment-test.properties");
        TestCertificates.setupKeyAndTrustStore();
    }

}