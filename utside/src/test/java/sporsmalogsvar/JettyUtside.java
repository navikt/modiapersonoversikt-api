package sporsmalogsvar;

import no.nav.modig.core.test.FilesAndDirs;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import java.io.File;

public class JettyUtside {

	public static void main(String[] args) {
	    SystemProperties.setFrom("sporsmalogsvar.properties");
		TestCertificates.setupKeyAndTrustStore();

		Jetty.usingWar(FilesAndDirs.WEBAPP_SOURCE).at("sporsmalogsvar").port(8585)
				.overrideWebXml(new File(FilesAndDirs.TEST_RESOURCES, "override-web.xml"))
				.buildJetty().start();
	}

}
