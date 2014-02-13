package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.sykmeldingsperioder;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.DefaultSykepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class SykmeldingsperioderWrapperTestConfig {

    private SykepengerServiceBi dummyBonne = mock(SykepengerServiceBi.class);
    private SykepengerServiceBi reellDummy = mock(SykepengerServiceBi.class);
    private SykepengerServiceBi mockDummy = mock(SykepengerServiceBi.class);

    @Bean
    public SykepengerServiceBi sykepengerService() {
        return new DefaultSykepengerService();
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerService() {
        return mock(ForeldrepengerServiceBi.class);
    }

    @Bean
    @Qualifier("sykepengerServiceDefault")
    public Wrapper<SykepengerServiceBi> sykepengerServiceDefault() {
        return new Wrapper<>(reellDummy);
    }

    @Bean
    @Qualifier("sykepengerServiceMock")
    public Wrapper<SykepengerServiceBi> sykepengerServiceMock() {
        return new Wrapper<>(mockDummy);
    }

    @Bean
    @Qualifier("foreldrepengerServiceDefault")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault() {
        return new Wrapper<>(foreldrepengerService());
    }

    @Bean
    @Qualifier("foreldrepengerServiceMock")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceMock() {
        return new Wrapper<>(mock(ForeldrepengerServiceBi.class));
    }

}
