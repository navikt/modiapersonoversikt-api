package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v2.kodeverk;

import no.nav.modig.modia.ping.Pingable;
import no.nav.modig.modia.ping.PingableWebService;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.common.kodeverk.CachingKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.DefaultKodeverkClient;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Optional.empty;
import static no.nav.sbl.dialogarena.common.cxf.InstanceSwitcher.createMetricsProxyWithInstanceSwitcher;
import static org.apache.cxf.ws.security.SecurityConstants.MUST_UNDERSTAND;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = lagKodeverkPortType();
        KodeverkPortType mock = KodeverkV2PortTypeMock.kodeverkPortType();

        return createMetricsProxyWithInstanceSwitcher("KodeverkPortTypeV2", prod, mock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient prod = lagKodeverkClient();
        KodeverkClient mock = KodeverkV2PortTypeMock.kodeverkClient();
        return createMetricsProxyWithInstanceSwitcher("KodeverkClient", prod, mock, KODEVERK_KEY, KodeverkClient.class);
    }

    @Bean
    public Pingable pingKodeverk() {
        return new PingableWebService("Kodeverk", lagKodeverkPortType());
    }

    private KodeverkPortType lagKodeverkPortType() {
        return new CXFClient<>(KodeverkPortType.class)
                .timeout(15000, 15000)
                .wsdl("classpath:kodeverk/no/nav/tjeneste/virksomhet/kodeverk/v2/Kodeverk.wsdl")
                .address(System.getProperty("kodeverkendpoint.v2.url"))
                .withProperty(MUST_UNDERSTAND, false)
                .build();
    }

    private KodeverkClient lagKodeverkClient() {
        return new CachingKodeverkClient(new DefaultKodeverkClient(kodeverkPortType()), empty());
    }

}
