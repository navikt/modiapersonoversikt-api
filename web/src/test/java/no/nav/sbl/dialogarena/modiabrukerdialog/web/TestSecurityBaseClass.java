package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.test.SystemProperties.setFrom;

@DirtiesContext
public class TestSecurityBaseClass {

    @BeforeClass
    public static void setupStatic() {
        setFrom("environment-local.properties");
        setupKeyAndTrustStore();
    }

}