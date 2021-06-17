package no.nav.modiapersonoversikt.config.endpoint.v2.henvendelse;

import no.nav.common.cxf.CXFClient;
import no.nav.common.cxf.StsConfig;
import no.nav.common.utils.EnvironmentUtils;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modiapersonoversikt.infrastructure.ping.PingableWebService;
import no.nav.modiapersonoversikt.infrastructure.types.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Autowired;

import static no.nav.modiapersonoversikt.infrastructure.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class BehandleHenvendelseEndpointConfig {
    @Autowired
    private StsConfig stsConfig;

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        final BehandleHenvendelsePortType prod = createBehandleHenvendelsePortType().configureStsForSubject(stsConfig).build();

        return createTimerProxyForWebService("BehandleHenvendelse", prod, BehandleHenvendelsePortType.class);
    }

    @Bean
    public Pingable behandleHenvendelsePing() {
        final BehandleHenvendelsePortType ws = createBehandleHenvendelsePortType().configureStsForSystemUser(stsConfig).build();
        return new PingableWebService("Behandle henvendelse", ws);
    }

    private static CXFClient<BehandleHenvendelsePortType> createBehandleHenvendelsePortType() {
        return new CXFClient<>(BehandleHenvendelsePortType.class)
                .wsdl("classpath:wsdl/BehandleHenvendelse.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_BEHANDLEHENVENDELSE_V1_ENDPOINTURL"))
                .withProperty("jaxb.additionalContextClasses", new Class[]{XMLJournalfortInformasjon.class});
    }

}
