package no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.DefaultForeldrepengerService;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.PleiepengerService;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.PleiepengerServiceImpl;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.SykepengerMapper;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SykmeldingsperioderConsumerConfig {

    @Autowired
    private ForeldrepengerV2 foreldrepengerPortType;

    @Autowired
    private SykepengerV2 sykepengerPortType;

    @Autowired
    private PleiepengerV1 pleiepengerPortType;

    @Bean
    public SykepengerServiceBi sykepengerServiceBi() {
        DefaultSykepengerService sykepengerService = new DefaultSykepengerService();
        sykepengerService.setSykepengerService(sykepengerPortType);
        sykepengerService.setMapper(SykepengerMapper.getInstance());
        return sykepengerService;
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceBi() {
        DefaultForeldrepengerService foreldrepengerService = new DefaultForeldrepengerService();
        foreldrepengerService.setForeldrepengerService(foreldrepengerPortType);
        foreldrepengerService.setMapper(ForeldrepengerMapper.getInstance());
        return foreldrepengerService;
    }

    @Bean
    public PleiepengerService pleiepengerService() {
        return new PleiepengerServiceImpl(pleiepengerPortType);
    }
}
