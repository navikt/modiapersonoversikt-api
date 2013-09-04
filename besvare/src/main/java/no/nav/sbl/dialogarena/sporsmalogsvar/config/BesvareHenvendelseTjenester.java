package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;

@Configuration
public class BesvareHenvendelseTjenester {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${besvarehenvendelseendpoint.url}")
    protected String besvareHenvendelseEndpoint;

    protected String oppgavebehandlingEndpoint = "https://localhost:30103/besvarehenvendelse/services/domene.Brukerdialog/GsakOppgavebehandling_v1";

    @Bean
    public Pingable besvareHenvendelsePing() {
        return new BesvareHenvendelsePingImpl(besvareSystemUser());
    }

    @Bean
    public Pingable oppgavebehandlingPing() {
        return new OppgavebehandlingPingImpl();
    }

    @Bean
    public BesvareHenvendelsePortType besvareSso() {
        return opprettBesvareHenvendelsePortType(new UserSAMLOutInterceptor());
    }

    @Bean
    public BesvareHenvendelsePortType besvareSystemUser() {
        return opprettBesvareHenvendelsePortType(new SystemSAMLOutInterceptor());
    }

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingSystemUser() {
        return opprettOppgavebehandlingPortType(new SystemSAMLOutInterceptor());
    }

    private BesvareHenvendelsePortType opprettBesvareHenvendelsePortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig(samlOutInterceptor);
        jaxwsClient.setServiceClass(BesvareHenvendelsePortType.class);
        jaxwsClient.setAddress(besvareHenvendelseEndpoint);
        jaxwsClient.setWsdlURL("classpath:v1/BesvareHenvendelse.wsdl");
        BesvareHenvendelsePortType besvareHenvendelsePortType = jaxwsClient.create(BesvareHenvendelsePortType.class);
        return konfigurerMedHttps(besvareHenvendelsePortType);
    }

    private OppgavebehandlingPortType opprettOppgavebehandlingPortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig(samlOutInterceptor);
        jaxwsClient.setServiceClass(OppgavebehandlingPortType.class);
        jaxwsClient.setAddress(oppgavebehandlingEndpoint);
//        jaxwsClient.setWsdlURL("classpath:Oppgavebehandling.wsdl");
        OppgavebehandlingPortType oppgavebehandlingPortType = jaxwsClient.create(OppgavebehandlingPortType.class);
        return konfigurerMedHttps(oppgavebehandlingPortType);
    }

    private <T> T konfigurerMedHttps(T portType) {
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        return portType;
	}

    private JaxWsProxyFactoryBean commonJaxWsConfig(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        properties.put(SecurityConstants.MUSTUNDERSTAND, false);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        factoryBean.getOutInterceptors().add(samlOutInterceptor);
        return factoryBean;
    }

    private class OppgavebehandlingPingImpl implements Pingable {
        @Override
        public List<PingResult> ping() {
            List<PingResult> pingResultList = new LinkedList<>();
            pingResultList.add(performPing());
            return pingResultList;
        }

        private PingResult performPing() {
            boolean pingResponseSuccessful;
            long timeElapsed;
            long start = System.currentTimeMillis();
            try {
                oppgavebehandlingSystemUser().ping();
                timeElapsed = System.currentTimeMillis() - start;
//                logger.info("BesvareHenvendelse_v1.ping(): SUCCESS");
                pingResponseSuccessful = true;
            } catch (Exception e) {
                timeElapsed = System.currentTimeMillis() - start;
//                logger.info("BesvareHenvendelse_v1.ping(): ERROR" + e.getMessage());
                pingResponseSuccessful = false;
            }

            PingResult.ServiceResult serviceResult = pingResponseSuccessful ? SERVICE_OK : SERVICE_FAIL;

            return new PingResult("GsakOppgavebehandling_v1", serviceResult, timeElapsed);
        }
    }
}