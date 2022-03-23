package no.nav.modiapersonoversikt.consumer.infotrygd.config.spring.sykepenger;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversikt.infrastructure.jaxws.handlers.MDCOutHandler;
import no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class SykepengerConsumerConfig {
    private final String sykepengerEndpointUrl = getRequiredProperty("VIRKSOMHET_SYKEPENGER_V2_ENDPOINTURL");

    @Bean
    public SykepengerV2 sykepengerPortType(StsConfig stsConfig) {
        return MetricsFactory.createTimerProxyForWebService("Sykepenger_v2", getSykepengerPortType(false, stsConfig), SykepengerV2.class);
    }

    @Bean
    public Pingable sykepengerPingable(StsConfig stsConfig) {
        return new PingableWebService("Sykepenger", getSykepengerPortType(true, stsConfig));
    }

    private SykepengerV2 getSykepengerPortType(boolean isPingPorttype, StsConfig stsConfig) {
        CXFClient<SykepengerV2> cxfClient = new CXFClient<>(SykepengerV2.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/sykepenger/v2/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/sykepenger/v2/Binding", "Sykepenger_v2"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/sykepenger/v2/Binding", "Sykepenger_v2Port"))
                .withHandler(new MDCOutHandler())
                .address(sykepengerEndpointUrl);

        if (isPingPorttype) {
            cxfClient.configureStsForSystemUser(stsConfig);
        } else {
            cxfClient.configureStsForSubject(stsConfig);
        }

        return cxfClient.build();
    }
}
