package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.kodeverk;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.porttypeimpl.KodeverkV2PortTypeImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.KodeverkV2PortTypeMock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

@Configuration
public class KodeverkV2WrapperConfig {

    @Value("${kodeverkendpoint.v2.url}")
    private URL endpoint;

    @Bean
    @Qualifier("kodeverkPort")
    public Wrapper<KodeverkV2PortTypeImpl> kodeverkPort() {
        return new Wrapper<>(new KodeverkV2PortTypeImpl(endpoint));
    }

    @Bean
    @Qualifier("kodeverkMock")
    public Wrapper<KodeverkV2PortTypeMock> kodeverkMock() {
        return new Wrapper<>(new KodeverkV2PortTypeMock());
    }


}
