package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.testcertificates.TestCertificates;
import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public class TestSecurityBaseClass {
    @BeforeClass
    public static void setupStatic() {
        TestCertificates.setupKeyAndTrustStore();
    }
}