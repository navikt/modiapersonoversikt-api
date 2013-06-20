package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.security.loginmodule.DummyRole;
import no.nav.modig.testcertificates.TestCertificates;

import org.apache.wicket.util.time.Duration;
import org.eclipse.jetty.plus.jaas.JAASLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;

public final class StartJetty {

	private StartJetty() {
	}

	public static final int PORT = 8080;

	public static void main(final String[] args) throws Exception { // NOPMD

	    SystemProperties.load("/jetty-environment.properties");
	    SystemProperties.load("/environment-t8.properties");

		final int timeout = (int) Duration.ONE_HOUR.getMilliseconds();
		TestCertificates.setupKeyAndTrustStore();

		final Server server = new Server();
		final SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(timeout);
		connector.setSoLingerTime(-1);
		connector.setPort(PORT);
		server.addConnector(connector);

		final WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/modiabrukerdialog");
		context.addOverrideDescriptor("jetty-web.xml");
		context.setResourceBase("src/main/webapp");
		context.setAttribute(WebInfConfiguration.CONTAINER_JAR_PATTERN, ".*");

		System.out.println(">>> Set up loginmodule");         // NOPMD
		SecurityHandler securityHandler = context.getSecurityHandler();
		JAASLoginService jaasLoginService = new JAASLoginService("Simple Login Realm");
		jaasLoginService.setLoginModuleName("simplelogin");
		jaasLoginService.setRoleClassNames(new String[]{DummyRole.class.getName()});
		securityHandler.setLoginService(jaasLoginService);
		securityHandler.setRealmName("Simple Login Realm");

		server.setHandler(context);

		try {
			System.out.println(">>> STARTING EMBEDDED JETTY SERVER on port " + PORT + " , PRESS ANY KEY TO STOP");// NOPMD
			server.start();
			System.in.read();
			System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");                                                     // NOPMD
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();// NOPMD
			System.exit(1);
		}
	}

}
