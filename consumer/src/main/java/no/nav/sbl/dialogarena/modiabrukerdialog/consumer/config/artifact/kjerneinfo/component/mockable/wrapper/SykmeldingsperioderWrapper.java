package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getForeldrepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getPleiepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerServiceBiMock;

@Configuration
public class SykmeldingsperioderWrapper {

    @Inject
    private SykepengerServiceBi sykepengerServiceBi;

    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceBi;

    @Inject
    private PleiepengerServiceBi pleiepengerServiceBi;

    @Bean
    @Qualifier("sykepengerServiceDefault")
    public Wrapper<SykepengerServiceBi> sykepengerServiceDefault() {
        return new Wrapper<>(sykepengerServiceBi);
    }

    @Bean
    @Qualifier("sykepengerServiceMock")
    public Wrapper<SykepengerServiceBi> sykepengerServiceMock() {
        return new Wrapper<>(getSykepengerServiceBiMock());
    }

    @Bean
    @Qualifier("foreldrepengerServiceDefault")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault() {
        return new Wrapper<>(foreldrepengerServiceBi);
    }

    @Bean
    @Qualifier("foreldrepengerServiceMock")
    public Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceMock() {
        return new Wrapper<>(getForeldrepengerServiceBiMock());
    }

    @Bean
    @Qualifier("pleiepengerServiceDefault")
    public Wrapper<PleiepengerServiceBi> pleiepengerServiceDefault() {
        return new Wrapper<>(pleiepengerServiceBi);
    }

    @Bean
    @Qualifier("pleiepengerServiceMock")
    public Wrapper<PleiepengerServiceBi> pleiepengerServiceMock() {
        return new Wrapper<>(getPleiepengerServiceBiMock());
    }

}
