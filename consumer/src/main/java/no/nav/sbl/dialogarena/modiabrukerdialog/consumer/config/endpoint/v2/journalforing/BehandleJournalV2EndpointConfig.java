package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.journalforing;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleJournalV2PortTypeMock.createBehandleJournalPortTypeMock;

@Configuration
public class BehandleJournalV2EndpointConfig {

    public static final String BEHANDLE_JOURNAL_V2_KEY = "start.behandlejournal.withmock";

    @Bean
    public BehandleJournalV2 behandleJournalPortType() {
        return createSwitcher(
                createBehandleJournalPortType(new UserSAMLOutInterceptor()),
                createBehandleJournalPortTypeMock(),
                BEHANDLE_JOURNAL_V2_KEY,
                BehandleJournalV2.class);
    }

    @Bean
    public Pingable ping() {
        final BehandleJournalV2 behandleJournalPortType = createBehandleJournalPortType(new SystemSAMLOutInterceptor());
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "BEHANDLE_JOURNAL_V2";
                try {
                    behandleJournalPortType.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
    }

    private static BehandleJournalV2 createBehandleJournalPortType(AbstractSAMLOutInterceptor interceptor) {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
//        proxyFactoryBean.setWsdlLocation("classpath:wsdl/no/nav/tjeneste/virksomhet/behandleJournal/v2/behandleJournal.wsdl");
        proxyFactoryBean.setAddress(System.getProperty("behandlejournal.v2.url"));
        proxyFactoryBean.getOutInterceptors().add(interceptor);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        BehandleJournalV2 portType = proxyFactoryBean.create(BehandleJournalV2.class);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters clientParameters = new TLSClientParameters();
        clientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(clientParameters);
        return portType;
    }
}
