package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.integrasjon.features.TimeoutFeature;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_CONNECTION_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.EndpointsConfig.MODIA_RECEIVE_TIMEOUT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.Utils.konfigurerMedHttps;

public class OppgavebehandlingPortTypeImpl {

    protected String oppgavebehandlingEndpoint;

    public OppgavebehandlingPortTypeImpl(String oppgavebehandlingEndpoint) {
        this.oppgavebehandlingEndpoint = oppgavebehandlingEndpoint;
    }

    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        return opprettOppgavebehandlingPortType(new UserSAMLOutInterceptor());
    }

    public Pingable oppgavebehandlingPing() {
        return new OppgavebehandlingPing(opprettOppgavebehandlingPortType(new SystemSAMLOutInterceptor()));
    }

    private OppgavebehandlingPortType opprettOppgavebehandlingPortType(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = commonJaxWsConfig(samlOutInterceptor);
        factoryBean.setServiceClass(OppgavebehandlingPortType.class);
        factoryBean.setAddress(oppgavebehandlingEndpoint);
        factoryBean.setWsdlURL("classpath:Oppgavebehandling.wsdl");
        OppgavebehandlingPortType oppgavebehandlingPortType = factoryBean.create(OppgavebehandlingPortType.class);
        konfigurerMedHttps(oppgavebehandlingPortType);

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
        features.add(new TimeoutFeature().withConnectionTimeout(MODIA_CONNECTION_TIMEOUT).withReceiveTimeout(MODIA_RECEIVE_TIMEOUT));
        factoryBean.getOutInterceptors().add(samlOutInterceptor);
        return factoryBean;
    }

    private static class OppgavebehandlingPing implements Pingable {

        OppgavebehandlingPortType oppgavebehandlingPortType;

        public OppgavebehandlingPing(OppgavebehandlingPortType oppgavebehandlingPortType) {
            this.oppgavebehandlingPortType = oppgavebehandlingPortType;
        }

        @Override
        public List<PingResult> ping() {
            long start = currentTimeMillis();
            try {
                boolean ping = oppgavebehandlingPortType.ping();
                long timeElapsed = currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", ping ? SERVICE_OK : SERVICE_FAIL, timeElapsed));
            } catch (Exception e) {
                long timeElapsed = currentTimeMillis() - start;
                return asList(new PingResult("Oppgavebehandling_v1", SERVICE_FAIL, timeElapsed));
            }
        }
    }
}
