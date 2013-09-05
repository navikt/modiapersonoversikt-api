package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import java.util.HashMap;
import java.util.Map;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class HentOppgaveConfig {

    @Profile({"default", "oppgavebehandlingDefault"})
    @Configuration
    public static class OppgavebehandlingDefault {

        @Value("${oppgavebehandlingendpoint.url}")
        protected String oppgavebehandlingEndpoint;

        @Bean
        public OppgavebehandlingPortType oppgavebehandlingPortType() {
            JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
            Map<String, Object> properties = new HashMap<>();
            properties.put("schema-validation-enabled", true);
            properties.put(SecurityConstants.MUST_UNDERSTAND, false);
            factoryBean.setProperties(properties);
            factoryBean.getFeatures().add(new LoggingFeature());
            factoryBean.getOutInterceptors().add(new SystemSAMLOutInterceptor());
            factoryBean.setServiceClass(OppgavebehandlingPortType.class);
            factoryBean.setAddress(oppgavebehandlingEndpoint);
            OppgavebehandlingPortType oppgavebehandlingPortType = factoryBean.create(OppgavebehandlingPortType.class);
            Client client = ClientProxy.getClient(oppgavebehandlingPortType);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            TLSClientParameters params = new TLSClientParameters();
            params.setDisableCNCheck(true);
            conduit.setTlsClientParameters(params);

            return oppgavebehandlingPortType;
        }
    }

    @Profile({"test", "oppgavebehandlingTest"})
    @Configuration
    public static class OppgavebehandlingTest {

        public static final String FODESELSNR = "11111111111";
        public static final String OPPGAVEID = "1";

        @Bean
        public OppgavebehandlingPortType oppgavebehandlingPortType() {
            return new OppgavebehandlingPortType() {
                @Override
                public boolean ping() {
                    return true;
                }

                @Override
                public WSPlukkOppgaveResultat plukkOppgave(String tema) {
                    return new WSPlukkOppgaveResultat().withFodselsnummer(FODESELSNR).withOppgaveId(OPPGAVEID);
                }

            };
        }

    }
}
