package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    SykepengerServiceBi sykepengerServiceBi;

    @Inject
    ForeldrepengerServiceBi foreldrepengerServiceBi;

    @Bean
    SykmeldingsperioderPing sykmeldingsperioderPing() {
        return new SykmeldingsperioderPing(foreldrepengerServiceBi, sykepengerServiceBi);
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return new SykepengerWidgetServiceImpl();
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        return new ForeldrepengerLoader();
    }

}
