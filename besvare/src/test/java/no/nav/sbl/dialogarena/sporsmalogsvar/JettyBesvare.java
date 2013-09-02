package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.io.File;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;

public final class JettyBesvare {

    public static void main(String ... args) {
        SystemProperties.setFrom("jetty-innside.properties");
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());

        TestCertificates.setupKeyAndTrustStore();

        Jetty.usingWar(new File(TEST_RESOURCES, "webapp")).at("besvare").port(8383)
                .buildJetty().start();
    }
}
