package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class BehandleBrukerprofilWrapperTestConfig {

    @Bean
    public Wrapper<DefaultBehandleBrukerprofilService> defaultService() {
        return new Wrapper<>(mock(DefaultBehandleBrukerprofilService.class));
    }

    @Bean
    public Wrapper<BehandleBrukerprofilServiceBi> mockService() {
        return new Wrapper<>(mock(BehandleBrukerprofilServiceBi.class));
    }

}
