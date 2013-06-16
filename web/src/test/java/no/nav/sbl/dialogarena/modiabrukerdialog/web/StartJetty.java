package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.IOException;
import java.net.URISyntaxException;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJetty {

	public static final int PORT = 8080;

	private StartJetty() {
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		SystemProperties.load("/environment-test.properties");
		System.setProperty("no.nav.modig.core.context.subjectHandlerImplementationClass", "no.nav.modig.core.context.JettySubjectHandler");
		System.setProperty("servicegateway.url", "https://service-gw-t8.test.local:443/");
		System.setProperty("personsokendpoint.url", "https://d26apvl173.test.local:9451/tpsws/Personsok_v1");
		System.setProperty("ytelseskontraktendpoint.url", "https://service-gw-t8.test.local:443/");
		System.setProperty("kodeverkendpoint.url", "http://d26jbsl00068.test.local:8080/kodeverk-ws/Kodeverk");
		System.setProperty("brukerprofilendpoint.url", "http://10.33.44.182:9080/tpsws/Brukerprofil_v1");
		System.setProperty("behandleBrukerprofilendpoint.url", "https://d26apvl173.test.local:9451/tpsws/BehandleBrukerprofil_v1");
		System.setProperty("kjerneinfoendpoint.url", "http://e26apvl091.test.local:9081/tpsws/Person_v1");
		System.setProperty("spring.profiles.active","test");
		TestCertificates.setupKeyAndTrustStore();


		Jetty jetty = usingWar(WEBAPP_SOURCE).at("modiabrukerdialog").port(PORT).buildJetty();
		jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

}
