package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.kodeverk;

import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.InstanceSwitcher.createSwitcher;

@Configuration
public class KodeverkV2EndpointConfig {

    public static final String KODEVERK_KEY = "start.kodeverk.withmock";

    @Inject
    @Qualifier("kodeverkPort")
    private Wrapper<KodeverkV2PortTypeImpl> kodeverkPort;

    @Inject
    @Qualifier("kodeverkMock")
    private Wrapper<KodeverkV2PortTypeMock> kodeverkMock;

    @Bean(name = "kodeverkPortTypeV2")
    public KodeverkPortType kodeverkPortType() {
        KodeverkPortType prod = kodeverkPort.wrappedObject.kodeverkPortType();
        KodeverkPortType mock = kodeverkMock.wrappedObject.kodeverkPortType();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkPortType.class);
    }

    @Bean
    public KodeverkClient kodeverkClient() {
        KodeverkClient prod = kodeverkPort.wrappedObject.kodeverkClient();
        KodeverkClient mock = kodeverkMock.wrappedObject.kodeverkClient();
        return createSwitcher(prod, mock, KODEVERK_KEY, KodeverkClient.class);
    }
}
