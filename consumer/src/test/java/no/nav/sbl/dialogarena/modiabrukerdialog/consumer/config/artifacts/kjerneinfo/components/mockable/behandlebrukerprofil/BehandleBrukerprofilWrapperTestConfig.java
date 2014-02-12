package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class BehandleBrukerprofilWrapperTestConfig {

    @Bean
    @Qualifier("behandleBrukerprofilService")
    public Wrapper<DefaultBehandleBrukerprofilService> defaultService() {
        return new Wrapper<>(mock(DefaultBehandleBrukerprofilService.class));
    }

    @Bean
    @Qualifier("behandleBrukerprofilMock")
    public Wrapper<BehandleBrukerprofilServiceBi> mockService() {
        return new Wrapper<>(mock(BehandleBrukerprofilServiceBi.class));
    }

}
