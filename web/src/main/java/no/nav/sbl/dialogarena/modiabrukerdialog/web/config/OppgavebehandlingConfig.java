package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;

@Configuration
public class OppgavebehandlingConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OppgavebehandlingConfig.class);

    @Profile({"default", "oppgavebehandlingDefault"})
    @Configuration
    public static class Default {

        @Value("${oppgavebehandlingendpoint.url}")
        protected String oppgavebehandlingEndpoint;

        @Bean
        public OppgavebehandlingPortType oppgavebehandlingPortType() {
            return opprettOppgavebehandlingPortType(new UserSAMLOutInterceptor());
        }

        @Bean
        public Pingable oppgavebehandlingPing() {
            return new OppgavebehandlingPing(opprettOppgavebehandlingPortType(new SystemSAMLOutInterceptor()));
        }

        private OppgavebehandlingPortType opprettOppgavebehandlingPortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
            JaxWsProxyFactoryBean factoryBean = commonJaxWsConfig(samlOutInterceptor);
            factoryBean.setServiceClass(OppgavebehandlingPortType.class);
            factoryBean.setAddress(oppgavebehandlingEndpoint);
            factoryBean.setWsdlURL("classpath:Oppgavebehandling.wsdl");
            OppgavebehandlingPortType oppgavebehandlingPortType = factoryBean.create(OppgavebehandlingPortType.class);

            Client client = ClientProxy.getClient(oppgavebehandlingPortType);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            TLSClientParameters params = new TLSClientParameters();
            params.setDisableCNCheck(true);
            conduit.setTlsClientParameters(params);

            return oppgavebehandlingPortType;
        }

        private JaxWsProxyFactoryBean commonJaxWsConfig(AbstractSAMLOutInterceptor samlOutInterceptor) {
            JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
            Map<String, Object> properties = new HashMap<>();
            properties.put("schema-validation-enabled", true);
            properties.put(SecurityConstants.MUST_UNDERSTAND, false);
            factoryBean.setProperties(properties);
            List<Feature> features = factoryBean.getFeatures();
            features.add(new LoggingFeature());
            features.add(new WSAddressingFeature());
            factoryBean.getOutInterceptors().add(samlOutInterceptor);
            return factoryBean;
        }
    }

    @Profile({"test", "oppgavebehandlingTest"})
    @Configuration
    public static class Test {

        public static final String FODESELSNR = "12345678901";
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

                @Override
                public void leggTilbakeOppgave(String oppgaveId, String aarsak) {
                    LOG.debug("Legge tilbake oppgave med id " + oppgaveId + " fordi " + aarsak);
                }

            };
        }
    }

    private static class OppgavebehandlingPing implements Pingable {

        OppgavebehandlingPortType oppgavebehandlingPortType;

        public OppgavebehandlingPing(OppgavebehandlingPortType oppgavebehandlingPortType) {
            this.oppgavebehandlingPortType = oppgavebehandlingPortType;
        }

        @Override
        public List<PingResult> ping() {
            long start = System.currentTimeMillis();
            try {
                boolean ping = oppgavebehandlingPortType.ping();
                long timeElapsed = System.currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", ping ? SERVICE_OK : SERVICE_FAIL, timeElapsed));
            } catch (Exception e) {
                long timeElapsed = System.currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", SERVICE_FAIL, timeElapsed));
            }
        }
    }
}
