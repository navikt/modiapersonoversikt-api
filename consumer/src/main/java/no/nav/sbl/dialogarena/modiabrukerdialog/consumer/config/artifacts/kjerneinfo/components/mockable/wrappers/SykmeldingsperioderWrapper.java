package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getForeldrepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerWidgetServiceMock;

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
    @Qualifier("sykepengerWidgetDefault")
    public Wrapper<SykepengerWidgetService> sykepengerWidgetDefault() {
        return new Wrapper<SykepengerWidgetService>(new SykepengerWidgetServiceImpl(sykepengerServiceBi, foreldrepengerServiceBi));
    }

    @Bean
    @Qualifier("sykepengerWidgetMock")
    public Wrapper<SykepengerWidgetService> sykepengerWidgetMock() {
        return new Wrapper<>(getSykepengerWidgetServiceMock());
    }

}
