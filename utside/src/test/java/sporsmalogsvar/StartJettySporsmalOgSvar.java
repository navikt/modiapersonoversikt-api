package sporsmalogsvar;

import no.nav.modig.core.test.FilesAndDirs;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import java.io.File;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;

public class StartJettySporsmalOgSvar {

	public static void main(String[] args) {
	    SystemProperties.setFrom("sporsmalogsvar.properties");
		TestCertificates.setupKeyAndTrustStore();

		Jetty jetty = Jetty.usingWar(FilesAndDirs.WEBAPP_SOURCE).at("sporsmalogsvar").port(8585)
				.overrideWebXml(new File(FilesAndDirs.TEST_RESOURCES, "override-web.xml"))
				.buildJetty();
		jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

}
