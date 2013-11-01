package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2EndpointConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.ConfigUtil.setUseMock;

@Configuration
public class KodeverkV2EndpointConfig {
    private static final Logger LOG = LoggerFactory.getLogger(KodeverkV2EndpointConfig.class);

    @Value("${kodeverkendpoint.v2.url}")
    private URL kodeverkEndpoint;

    private boolean useMock;
    private KodeverkV2EndpointConfigImpl portType = new KodeverkV2EndpointConfigImpl(kodeverkEndpoint);
    private KodeverkV2PortTypeMock portTypeMock = new KodeverkV2PortTypeMock();

    public KodeverkV2EndpointConfig() {
        useMock = setUseMock("start.kodeverk.withintegration", LOG);
    }

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        if (useMock) {
            return portTypeMock.kodeverkPortType();
        }
        return portType.kodeverkPortType();
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        if (useMock) {
            return portTypeMock.kodeverkClient();
        }
        return portType.kodeverkClient();
    }
}
