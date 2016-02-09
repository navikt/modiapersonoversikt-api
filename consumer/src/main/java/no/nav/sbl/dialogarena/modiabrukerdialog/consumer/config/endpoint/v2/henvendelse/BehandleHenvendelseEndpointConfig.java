package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.BehandleHenvendelsePortTypeMock.createBehandleHenvendelsePortTypeMock;

@Configuration
public class BehandleHenvendelseEndpointConfig {

    @Bean
    public BehandleHenvendelsePortType behandleHenvendelsePortType() {
        final BehandleHenvendelsePortType prod = createBehandleHenvendelsePortType(new UserSAMLOutInterceptor());
        final BehandleHenvendelsePortType mock = createBehandleHenvendelsePortTypeMock();

        return createMetricsProxyWithInstanceSwitcher(prod, mock, HENVENDELSE_KEY, BehandleHenvendelsePortType.class);
    }

    @Bean
    public Pingable behandleHenvendelsePing() {
        final BehandleHenvendelsePortType ws = createBehandleHenvendelsePortType(new SystemSAMLOutInterceptor());
        return new PingableWebService("Behandle henvendelse", ws);
    }

    private static BehandleHenvendelsePortType createBehandleHenvendelsePortType(AbstractSAMLOutInterceptor interceptor) {
        return new CXFClient<>(BehandleHenvendelsePortType.class)
                .wsdl("classpath:BehandleHenvendelse.wsdl")
                .address(System.getProperty("behandle.henvendelse.url"))
                .withOutInterceptor(interceptor)
                .setProperty("jaxb.additionalContextClasses", new Class[]{XMLJournalfortInformasjon.class})
                .build();
    }

}
