package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.brukerprofil;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class BrukerprofilWrapperTestConfig {

    @Bean
    public Wrapper<BrukerprofilServiceBi> brukerprofilService() {
        return new Wrapper<>(mock(BrukerprofilServiceBi.class));
    }

    @Bean
    public Wrapper<BrukerprofilServiceBi> brukerprofilMock() {
        return new Wrapper<>(mock(BrukerprofilServiceBi.class));
    }

}
