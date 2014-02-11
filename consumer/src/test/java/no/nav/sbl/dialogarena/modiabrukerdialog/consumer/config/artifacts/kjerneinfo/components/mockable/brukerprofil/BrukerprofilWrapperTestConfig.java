package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.brukerprofil;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class BrukerprofilWrapperTestConfig {

    @Bean
    public BrukerprofilServiceBi brukerprofilService() {
        return mock(BrukerprofilServiceBi.class);
    }

    @Bean
    public BrukerprofilServiceBi brukerprofilMock() {
        return mock(BrukerprofilServiceBi.class);
    }

}
