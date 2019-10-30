package no.nav.dkif.config.spring;

import no.nav.modig.jaxws.handlers.MDCOutHandler;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;


@Configuration
public class DkifConsumerConfig {

    @Value("${dkifendpoint.url:}")
    private String dkifEndpointUrl;
    @Value("${servicegateway.url:}")
    private String servicegatewayUrl;


    @Bean
    public DigitalKontaktinformasjonV1 dkifV1() {
        DigitalKontaktinformasjonV1 dkif = getDigitalKontaktinformasjonV1()
                .configureStsForSystemUser()
                .build();
        return createTimerProxyForWebService("DKIF", dkif, DigitalKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable digitalKontaktinformasjonPingable() {
        DigitalKontaktinformasjonV1 pingPorttype = getDigitalKontaktinformasjonV1()
                .configureStsForSystemUser()
                .build();
        return new PingableWebService("DigitalKontaktinformasjon DKIF", pingPorttype);
    }

    private CXFClient<DigitalKontaktinformasjonV1> getDigitalKontaktinformasjonV1() {
        return new CXFClient<>(DigitalKontaktinformasjonV1.class)
                .wsdl("classpath:dkif/no/nav/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding", "DigitalKontaktinformasjon_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding", "DigitalKontaktinformasjon_v1Port"))
                .address(getAdress())
                .withHandler(new MDCOutHandler());
    }

    private String getAdress() {
        return defaultIfBlank(servicegatewayUrl, dkifEndpointUrl);
    }
}
