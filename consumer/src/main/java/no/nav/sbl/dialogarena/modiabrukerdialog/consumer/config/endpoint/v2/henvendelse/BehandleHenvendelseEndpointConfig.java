package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.SystemSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_FAIL;
import static no.nav.modig.modia.ping.PingResult.ServiceResult.SERVICE_OK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.henvendelse.HenvendelseEndpointConfig.HENVENDELSE_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics.TimingMetricsProxy.createMetricsProxy;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics.TimingMetricsProxy.createMetricsProxyWithInstanceSwitcher;
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
    public Pingable behandleHenvendelsePing(final BehandleHenvendelsePortType ws) {
        return new Pingable() {
            @Override
            public List<PingResult> ping() {
                long start = System.currentTimeMillis();
                String name = "BEHANDLE_HENVENDELSE";
                try {
                    ws.ping();
                    return asList(new PingResult(name, SERVICE_OK, System.currentTimeMillis() - start));
                } catch (Exception e) {
                    return asList(new PingResult(name, SERVICE_FAIL, System.currentTimeMillis() - start));
                }
            }
        };
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
