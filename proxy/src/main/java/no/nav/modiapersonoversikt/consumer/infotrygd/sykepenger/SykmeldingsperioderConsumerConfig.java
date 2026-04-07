package no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger;

import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SykmeldingsperioderConsumerConfig {

    @Autowired
    private SykepengerV2 sykepengerPortType;

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        DefaultSykepengerService sykepengerService = new DefaultSykepengerService();
        sykepengerService.setSykepengerService(sykepengerPortType);
        sykepengerService.setMapper(SykepengerMapper.getInstance());
        return sykepengerService;
    }
}
