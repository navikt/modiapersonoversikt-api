package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import no.nav.sbl.dialogarena.mottaksbehandling.tjeneste.TjenesteSikkerhet;
import no.nav.virksomhet.tjenester.oppgave.v2.Oppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.mottaksbehandling.tjeneste.TjenesteSikkerhet.leggPaaAutentisering;
import static no.nav.sbl.dialogarena.mottaksbehandling.tjeneste.TjenesteSikkerhet.standardBrukernavn;
import static no.nav.sbl.dialogarena.mottaksbehandling.tjeneste.TjenesteSikkerhet.standardPassord;

public class OppgavesystemIntegrasjon {
	
	public static Oppgavebehandling oppgaveBehandlingWSKlient() {
		JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
		Map<String, Object> properties = new HashMap<>();
		properties.put("schema-validation-enabled", true);
		jaxwsClient.setProperties(properties);

		jaxwsClient.getFeatures().add(new LoggingFeature());
		jaxwsClient.setServiceClass(Oppgavebehandling.class);
		jaxwsClient.setAddress("https://tjenestebuss-t11.adeo.no/nav-tjeneste-oppgavebehandling_v2Web/sca/OppgavebehandlingWSEXP");
		jaxwsClient.setWsdlURL("classpath:oppgavebehandling/no/nav/virksomhet/tjenester/oppgavebehandling/oppgavebehandling.wsdl");

//        leggPaaAutentisering(jaxwsClient, standardBrukernavn, standardPassord);
        TjenesteSikkerhet.leggPaaAutentisering(jaxwsClient, "Z999172", "***REMOVED***");

		return jaxwsClient.create(Oppgavebehandling.class);
	}

	public static Oppgave oppgaveWSKlient() {
		JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
		// Map<String, Object> properties = new HashMap<>();
		// properties.put("schema-validation-enabled", true);
		// jaxwsClient.setProperties(properties);

		jaxwsClient.getFeatures().add(new LoggingFeature());
		jaxwsClient.setServiceClass(Oppgave.class);
		jaxwsClient.setAddress("https://tjenestebuss-t11.adeo.no/nav-tjeneste-oppgave_v2Web/sca/OppgaveWSEXP");
		jaxwsClient.setWsdlURL("classpath:oppgave/no/nav/virksomhet/tjenester/oppgave/oppgave.wsdl");

//		leggPaaAutentisering(jaxwsClient, standardBrukernavn, standardPassord);

        TjenesteSikkerhet.leggPaaAutentisering(jaxwsClient, "Z999172", "***REMOVED***");
		return jaxwsClient.create(Oppgave.class);
	}

}
