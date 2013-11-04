package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2EndpointConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;

@Configuration
public class KodeverkV2EndpointConfig {

    private final String key = "start.kodeverk.withmock";
    @Value("${kodeverkendpoint.v2.url}")
    private URL kodeverkEndpoint;
    private KodeverkPortType portType = new KodeverkV2EndpointConfigImpl(kodeverkEndpoint).kodeverkPortType();
    private KodeverkPortType portTypeMock = new KodeverkV2PortTypeMock().kodeverkPortType();
    private KodeverkClient kodeverkKlient = new KodeverkV2EndpointConfigImpl(kodeverkEndpoint).kodeverkClient();
    private KodeverkClient kodeverkKlientMock = new KodeverkV2PortTypeMock().kodeverkClient();

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        return createSwitcher(portType, portTypeMock, key, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        return createSwitcher(kodeverkKlient, kodeverkKlientMock, key, KodeverkClient.class);
    }
}
