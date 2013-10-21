package no.nav.sbl.dialogarena.mottaksbehandling.sak;

import no.nav.sbl.dialogarena.mottaksbehandling.tjeneste.TjenesteSikkerhet;
import no.nav.virksomhet.tjenester.sak.pensjon.v1.PensjonSak;
import no.nav.virksomhet.tjenester.sak.v1.Sak;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import java.util.HashMap;
import java.util.Map;

public class SakIntegrasjon {

    public static void main(String[] args) {
        sakWSKlient().finnGenerellSakListe(null);

    }

    public static Sak sakWSKlient() {
        JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        jaxwsClient.setProperties(properties);

        jaxwsClient.getFeatures().add(new LoggingFeature());
        jaxwsClient.setServiceClass(Sak.class);
        jaxwsClient.setAddress("https://tjenestebuss-t11.adeo.no/nav-tjeneste-sak_v1Web/sca/SakWSEXP");
        jaxwsClient.setWsdlURL("classpath:no/nav/virksomhet/tjenester/sak/sak.wsdl");

        TjenesteSikkerhet.leggPaaAutentisering(jaxwsClient, "Z999172", "***REMOVED***");

        return jaxwsClient.create(Sak.class);
    }

    public static PensjonSak pensjonSakWSKlient() {
        JaxWsProxyFactoryBean jaxwsClient = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        jaxwsClient.setProperties(properties);

        jaxwsClient.getFeatures().add(new LoggingFeature());
        jaxwsClient.setServiceClass(PensjonSak.class);
        jaxwsClient.setAddress("https://tjenestebuss-t11.adeo.no/nav-tjeneste-pensjonSak_v1Web/sca/PensjonSakWSEXP");
        jaxwsClient.setWsdlURL("classpath:no/nav/virksomhet/tjenester/sak/pensjon/pensjon.wsdl");

        TjenesteSikkerhet.leggPaaAutentisering(jaxwsClient, "Z900001", "Ruting001");

        return jaxwsClient.create(PensjonSak.class);
    }
}
