package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import java.io.File;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;

public final class JettyInnside {

    public static void main(String ... args) {
        SystemProperties.setFrom("jetty.properties");
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());

        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(new File(TEST_RESOURCES, "webapp")).port(8383).at("innside").buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));

    }
}
