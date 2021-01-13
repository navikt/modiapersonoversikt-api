package no.nav.sykmeldingsperioder.config.spring;

import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonConsumerConfig;
import no.nav.sykmeldingsperioder.config.spring.foreldrepenger.ForeldrepengerConsumerConfig;
import no.nav.sykmeldingsperioder.config.spring.sykepenger.SykepengerConsumerConfig;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.DefaultForeldrepengerService;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerConsumerConfig;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceImpl;
import no.nav.sykmeldingsperioder.consumer.sykepenger.DefaultSykepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.SykepengerMapper;
import no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerService;
import no.nav.sykmeldingsperioder.consumer.utbetalinger.UtbetalingerServiceImpl;
import no.nav.tjeneste.virksomhet.foreldrepenger.v2.ForeldrepengerV2;
import no.nav.tjeneste.virksomhet.pleiepenger.v1.PleiepengerV1;
import no.nav.tjeneste.virksomhet.sykepenger.v2.SykepengerV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SykepengerConsumerConfig.class, ForeldrepengerConsumerConfig.class,
        PleiepengerConsumerConfig.class, OrganisasjonConsumerConfig.class})
public class ConsumerConfig {

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

    @Bean
    public UtbetalingerService utbetalingerService() {
        return new UtbetalingerServiceImpl();
    }
}
