package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerMockService;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.ForeldrepengerMapper;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerMockService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.SykepengerMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


@Configuration
public class SykmeldingsperioderWrapper {

    @Inject
    private SykepengerServiceBi sykepengerServiceBi;

    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceBi;

    @Bean
    @Qualifier("sykepengerServiceDefault")
    public Wrapper<SykepengerServiceBi> sykepengerServiceDefault() {
        return new Wrapper<>(sykepengerServiceBi);
    }

    @Bean
    @Qualifier("sykepengerServiceMock")
    public Wrapper<SykepengerServiceBi> sykepengerServiceMock() {
        return new Wrapper<>(new SykepengerMockService(new SykepengerMapper()));
    }

    @Bean
    @Qualifier("foreldrepengerServiceDefault")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault() {
        return new Wrapper<>(foreldrepengerServiceBi);
    }

    @Bean
    @Qualifier("foreldrepengerServiceMock")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceMock() {
        return new Wrapper<>(new ForeldrepengerMockService(new ForeldrepengerMapper()));
    }

}
