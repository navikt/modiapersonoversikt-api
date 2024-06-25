package no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.modiapersonoversiktproxy.infrastructure.handlers.MDCOutHandler;
import no.nav.modiapersonoversiktproxy.infrastructure.metrics.MetricsFactory;
import no.nav.modiapersonoversiktproxy.infrastructure.ping.Pingable;
import no.nav.modiapersonoversiktproxy.infrastructure.ping.PingableWebService;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

@Configuration
public class ForeldrepengerConsumerConfig {
    private final String foreldrepengerEndpointUrl = getRequiredProperty("VIRKSOMHET_FORELDREPENGER_V2_ENDPOINTURL");

    @Bean
    public ForeldrepengerV2 foreldrepengerPortType(StsConfig stsConfig) {
        return MetricsFactory.createTimerProxyForWebService("Foreldrepenger_v2", getForeldrepengerPortType(false, stsConfig), ForeldrepengerV2.class);
    }

    @Bean
    public Pingable foreldrepenger(StsConfig stsConfig) {
        return new PingableWebService("Foreldrepenger", getForeldrepengerPortType(true, stsConfig));
    }

    private ForeldrepengerV2 getForeldrepengerPortType(boolean isPingPorttype, StsConfig stsConfig) {
        CXFClient<ForeldrepengerV2> cxfClient = new CXFClient<>(ForeldrepengerV2.class)
                .wsdl("classpath:wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/foreldrepenger/v2/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/foreldrepenger/v2/Binding", "Foreldrepenger_v2"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/foreldrepenger/v2/Binding", "Foreldrepenger_v2Port"))
                .withHandler(new MDCOutHandler())
                .address(foreldrepengerEndpointUrl);

        if (isPingPorttype) {
            cxfClient.configureStsForSystemUser(stsConfig);
        } else {
            cxfClient.configureStsForSubject(stsConfig);
        }

        return cxfClient.build();
    }
}
