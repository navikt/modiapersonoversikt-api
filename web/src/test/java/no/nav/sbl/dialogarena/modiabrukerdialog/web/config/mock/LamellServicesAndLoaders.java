package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;


import no.nav.kjerneinfo.kontrakter.oppfolging.loader.OppfolgingsLoader;
import no.nav.kjerneinfo.kontrakter.ytelser.YtelseskontrakterLoader;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class LamellServicesAndLoaders {

    @Bean
    public OppfolgingsLoader modelLoader() {
        return mock(OppfolgingsLoader.class);
    }

    @Bean
    public OppfolgingskontraktServiceBi serviceBi() {
        return mock(OppfolgingskontraktServiceBi.class);
    }

    @Bean
    public YtelseskontraktServiceBi serviceBi2() {
        return mock(YtelseskontraktServiceBi.class);
    }

    @Bean
    public YtelseskontrakterLoader loader2() {
        return mock(YtelseskontrakterLoader.class);
    }

}
