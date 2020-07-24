package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KontrakterWrapperTestConfig {

    @Bean
    public DefaultYtelseskontraktService ytelseskontraktService() {
        return mock(DefaultYtelseskontraktService.class);
    }

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktMock() {
        return mock(YtelseskontraktServiceBi.class);
    }

    @Bean
    public DefaultOppfolgingskontraktService oppfolgingskontraktService() {
        return mock(DefaultOppfolgingskontraktService.class);
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktMock() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

}
