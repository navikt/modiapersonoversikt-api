package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.metrics.MetricsFactory.createTimerProxyForWebService;

@Configuration
public class BehandleHenvendelseEndpointConfig {

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        final BehandleHenvendelsePortType prod = createBehandleHenvendelsePortType().configureStsForSubject().build();

        return createTimerProxyForWebService("BehandleHenvendelse", prod, BehandleHenvendelsePortType.class);
    }

    @Bean
    public Pingable behandleHenvendelsePing() {
        final BehandleHenvendelsePortType ws = createBehandleHenvendelsePortType().configureStsForSystemUser().build();
        return new PingableWebService("Behandle henvendelse", ws);
    }

    private static CXFClient<BehandleHenvendelsePortType> createBehandleHenvendelsePortType() {
        return new CXFClient<>(BehandleHenvendelsePortType.class)
                .wsdl("classpath:wsdl/BehandleHenvendelse.wsdl")
                .address(EnvironmentUtils.getRequiredProperty("DOMENE_BRUKERDIALOG_BEHANDLEHENVENDELSE_V1_ENDPOINTURL"))
                .withProperty("jaxb.additionalContextClasses", new Class[]{XMLJournalfortInformasjon.class});
    }

}
