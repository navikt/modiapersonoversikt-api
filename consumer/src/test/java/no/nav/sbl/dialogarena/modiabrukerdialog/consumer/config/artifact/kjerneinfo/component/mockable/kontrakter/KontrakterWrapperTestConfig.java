package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class KontrakterWrapperTestConfig {

    @Bean
    public Wrapper<DefaultYtelseskontraktService> ytelseskontraktService() {
        return new Wrapper<>(mock(DefaultYtelseskontraktService.class));
    }

    @Bean
    public Wrapper<YtelseskontraktServiceBi> ytelseskontraktMock() {
        return new Wrapper<>(mock(YtelseskontraktServiceBi.class));
    }

    @Bean
    public Wrapper<DefaultOppfolgingskontraktService> oppfolgingskontraktService() {
        return new Wrapper<>(mock(DefaultOppfolgingskontraktService.class));
    }

    @Bean
    public Wrapper<OppfolgingskontraktServiceBi> oppfolgingskontraktMock() {
        return new Wrapper<>(mock(OppfolgingskontraktServiceBi.class));
    }

}
