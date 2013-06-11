package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.testcertificates.TestCertificates;
import org.junit.BeforeClass;

public class TestSecurityBaseClass {
    @BeforeClass
    public static void setup() {
        TestCertificates.setupKeyAndTrustStore();
    }
}