package sporsmalogsvar;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import no.nav.modig.core.test.FilesAndDirs;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

public class StartJettySporsmalOgSvar {
	
	@SuppressWarnings({ "PMD.SystemPrintln" })
	public static void main(String[] args) {

		loadProperties("sporsmalogsvar.properties");
		TestCertificates.setupKeyAndTrustStore();

		Jetty jetty = Jetty.usingWar(FilesAndDirs.WEBAPP_SOURCE).at("sporsmalogsvar").port(8585)
				.overrideWebXml(new File(FilesAndDirs.TEST_RESOURCES, "override-web.xml"))
				.buildJetty();
		System.out.println("ADDRESS: " + jetty.getBaseUrl());
		jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

	private static void loadProperties(String resource) {
		try {
			Properties props = new Properties();
			props.load(StartJettySporsmalOgSvar.class.getClassLoader().getResourceAsStream(resource));
			for (Object key : props.keySet()) {
				System.setProperty((String) key, (String) props.get(key));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
