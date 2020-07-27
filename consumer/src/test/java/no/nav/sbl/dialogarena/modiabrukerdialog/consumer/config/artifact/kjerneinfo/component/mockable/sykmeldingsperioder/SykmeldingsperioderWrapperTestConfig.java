package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.sykmeldingsperioder;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.sykepenger.DefaultSykepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
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
    public SykepengerServiceBi sykepengerServiceDefault() {
        return reellDummy;
    }

    @Bean
    public SykepengerServiceBi sykepengerServiceMock() {
        return mockDummy;
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceDefault() {
        return foreldrepengerService();
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceMock() {
        return mock(ForeldrepengerServiceBi.class);
    }

    @Bean
    public PleiepengerService pleiepengerServiceImpl() {
        return pleiepengerService();
    }

    @Bean
    public PleiepengerService pleiepengerServiceMock() {
        return mock(PleiepengerService.class);
    }

}
