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

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Value("${kodeverkendpoint.v2.url}")
    private URL kodeverkEndpoint;

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType portType = new KodeverkV2EndpointConfigImpl(kodeverkEndpoint).kodeverkPortType();
        KodeverkPortType portTypeMock = new KodeverkV2PortTypeMock().kodeverkPortType();
        return createSwitcher(portType, portTypeMock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient kodeverkKlient = new KodeverkV2EndpointConfigImpl(kodeverkEndpoint).kodeverkClient();
        KodeverkClient kodeverkKlientMock = new KodeverkV2PortTypeMock().kodeverkClient();
        return createSwitcher(kodeverkKlient, kodeverkKlientMock, KODEVERK_KEY, KodeverkClient.class);
    }
}
