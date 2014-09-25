package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Value("${kodeverkendpoint.v2.url}")
    private URL endpoint;

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = new KodeverkV2PortTypeImpl(endpoint).kodeverkPortType();
        KodeverkPortType mock = KodeverkV2PortTypeMock.kodeverkPortType();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient prod = new KodeverkV2PortTypeImpl(endpoint).kodeverkClient();
        KodeverkClient mock = KodeverkV2PortTypeMock.kodeverkClient();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkClient.class);
    }
}
