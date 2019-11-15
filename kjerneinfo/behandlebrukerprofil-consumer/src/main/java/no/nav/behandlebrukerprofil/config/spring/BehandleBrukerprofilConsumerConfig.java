package no.nav.behandlebrukerprofil.config.spring;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.BehandleBrukerprofilV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Configuration
public class BehandleBrukerprofilConsumerConfig {

    @Value("${VIRKSOMHET_BEHANDLEBRUKERPROFIL_V2_ENDPOINTURL:}")
    private String behandleBrukerprofilEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;

    @Bean
    public BehandleBrukerprofilV2 behandleBrukerprofilPortType() {
        BehandleBrukerprofilV2 endpoint = getBehandleBrukerprofilPortType()
                .configureStsForSubject()
                .build();
        return createTimerProxyForWebService("behandleBrukerprofilV2", endpoint, BehandleBrukerprofilV2.class);
    }

    @Bean
    public Pingable behandleBrukerprofilPingable() {
        BehandleBrukerprofilV2 pingPorttype = getBehandleBrukerprofilPortType()
                .configureStsForSystemUser()
                .build();
        return new PingableWebService("BehandleBrukerprofil", pingPorttype);
    }

    private CXFClient<BehandleBrukerprofilV2> getBehandleBrukerprofilPortType() {
        return new CXFClient<>(BehandleBrukerprofilV2.class)
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/behandleBrukerprofil/v2/", "BehandleBrukerprofil_v2"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/behandleBrukerprofil/v2/", "BehandleBrukerprofil_v2"))
                .address(getAdress())
                .withHandler(new MDCOutHandler());
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, behandleBrukerprofilEndpointUrl);
    }
}
