package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.sykmeldingsperioder;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.sykepenger.DefaultSykepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public PleiepengerService pleiepengerService() {
        PleiepengerService mock = mock(PleiepengerService.class);
        when(mock.hentPleiepengerListe(any(PleiepengerListeRequest.class)))
                .thenReturn(new PleiepengerListeResponse(new ArrayList<>()));
        return mock;
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

    @Bean
    @Qualifier("pleiepengerServiceImpl")
    public Wrapper<PleiepengerService> pleiepengerServiceImpl() {
        return new Wrapper<>(pleiepengerService());
    }

    @Bean
    @Qualifier("pleiepengerServiceMock")
    public Wrapper<PleiepengerService> pleiepengerServiceMock() {
        return new Wrapper<>(mock(PleiepengerService.class));
    }

}
