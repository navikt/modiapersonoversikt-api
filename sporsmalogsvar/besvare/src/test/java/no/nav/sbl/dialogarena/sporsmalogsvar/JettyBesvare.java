package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.sbl.dialogarena.time.Datoformat;

import java.io.File;
import java.util.Locale;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.sbl.dialogarena.test.path.FilesAndDirs.TEST_RESOURCES;
import static org.apache.commons.collections15.FactoryUtils.constantFactory;

public final class JettyBesvare {

    public static void main(String ... args) {
        Datoformat.brukLocaleFra(constantFactory(new Locale("no", "nb")));
        SystemProperties.setFrom("jetty-innside.properties");
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());

        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = Jetty.usingWar(new File(TEST_RESOURCES, "webapp")).at("besvare").port(8383).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
