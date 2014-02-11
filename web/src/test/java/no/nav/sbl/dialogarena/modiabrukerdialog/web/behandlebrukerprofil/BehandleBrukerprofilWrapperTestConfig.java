package no.nav.sbl.dialogarena.modiabrukerdialog.web.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class BehandleBrukerprofilWrapperTestConfig {

    @Bean
    @Qualifier("behandleBrukerprofilService")
    public BehandleBrukerprofilServiceBi defaultService() {
        return mock(BehandleBrukerprofilServiceBi.class);
    }

    @Bean
    @Qualifier("behandleBrukerprofilMock")
    public BehandleBrukerprofilServiceBi mockService() {
        return mock(BehandleBrukerprofilServiceBi.class);
    }

}
