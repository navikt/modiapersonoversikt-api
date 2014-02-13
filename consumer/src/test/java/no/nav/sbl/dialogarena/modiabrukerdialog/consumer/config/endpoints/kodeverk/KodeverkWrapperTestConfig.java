package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.kodeverk.v2.KodeverkPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class KodeverkWrapperTestConfig {

    @Bean
    @Qualifier("kodeverkPort")
    public Wrapper<KodeverkV2PortTypeImpl> kodeverkPort() {
        KodeverkV2PortTypeImpl mock = mock(KodeverkV2PortTypeImpl.class);
        KodeverkPortType portTypeMock = mock(KodeverkPortType.class);
        when(mock.kodeverkPortType()).thenReturn(portTypeMock);
        return new Wrapper<>(mock);
    }

    @Bean
    @Qualifier("kodeverkMock")
    public Wrapper<KodeverkV2PortTypeImpl> kodeverkMock() {
        KodeverkV2PortTypeImpl mock = mock(KodeverkV2PortTypeImpl.class);
        KodeverkPortType portTypeMock = mock(KodeverkPortType.class);
        when(mock.kodeverkPortType()).thenReturn(portTypeMock);
        return new Wrapper<>(mock);
    }

}
