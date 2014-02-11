package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KontrakterWrapperTestConfig {

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktService() {
        return mock(YtelseskontraktServiceBi.class);
    }

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktMock() {
        return mock(YtelseskontraktServiceBi.class);
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktService() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktMock() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

}
