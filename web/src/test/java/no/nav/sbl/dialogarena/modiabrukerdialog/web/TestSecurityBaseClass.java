package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import org.junit.BeforeClass;
import org.springframework.test.annotation.DirtiesContext;

import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;

@DirtiesContext
public class TestSecurityBaseClass {

    @BeforeClass
    public static void setupStatic() {
        setupKeyAndTrustStore();
    }

}