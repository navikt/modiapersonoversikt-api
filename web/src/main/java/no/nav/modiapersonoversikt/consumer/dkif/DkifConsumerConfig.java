package no.nav.modiapersonoversikt.consumer.dkif;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.DigitalKontaktinformasjonV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;
import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;


@Configuration
public class DkifConsumerConfig {
    private final String dkifEndpointUrl = getRequiredProperty("VIRKSOMHET_DIGITALKONTAKINFORMASJON_V1_ENDPOINTURL");

    @Bean
    public DigitalKontaktinformasjonV1 dkifV1(StsConfig stsConfig) {
        DigitalKontaktinformasjonV1 dkif = getDigitalKontaktinformasjonV1()
                .configureStsForSystemUser(stsConfig)
                .build();
        return createTimerProxyForWebService("DKIF", dkif, DigitalKontaktinformasjonV1.class);
    }

    @Bean
    public Pingable digitalKontaktinformasjonPingable(StsConfig stsConfig) {
        DigitalKontaktinformasjonV1 pingPorttype = getDigitalKontaktinformasjonV1()
                .configureStsForSystemUser(stsConfig)
                .build();
        return new PingableWebService("DigitalKontaktinformasjon DKIF", pingPorttype);
    }

    private CXFClient<DigitalKontaktinformasjonV1> getDigitalKontaktinformasjonV1() {
        return new CXFClient<>(DigitalKontaktinformasjonV1.class)
                .wsdl("classpath:wsdl/no/nav/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding", "DigitalKontaktinformasjon_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/digitalKontaktinformasjon/v1/Binding", "DigitalKontaktinformasjon_v1Port"))
                .address(dkifEndpointUrl)
                .withHandler(new MDCOutHandler());
    }
}
