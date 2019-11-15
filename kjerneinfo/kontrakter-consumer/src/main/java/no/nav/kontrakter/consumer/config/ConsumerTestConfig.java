package no.nav.kontrakter.consumer.config;

import no.nav.kontrakter.consumer.fim.mapping.YtelseskontraktMapper;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.mock.OppfolgingkontraktMockService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.mock.YtelseskontraktMockService;
import no.nav.kontrakter.consumer.utils.OppfolgingskontraktMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConsumerTestConfig {

    @Bean
    public YtelseskontraktServiceBi ytelseskontraktServiceBi() {
        YtelseskontraktMockService ytelseskontraktService = new YtelseskontraktMockService();
        ytelseskontraktService.setMapper(YtelseskontraktMapper.getInstance());
        return ytelseskontraktService;
    }

    @Bean
    public OppfolgingskontraktServiceBi oppfolgingskontraktServiceBi() {
        OppfolgingkontraktMockService oppfolgingskontraktService = new OppfolgingkontraktMockService();
        oppfolgingskontraktService.setMapper(OppfolgingskontraktMapper.getInstance());
        return oppfolgingskontraktService;
    }
}
