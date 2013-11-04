package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.SykmeldingsperioderPanelConfigImpl;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getForeldrepengerLoaderMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerWidgetServiceMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykmeldingsperioderPingMock;

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    SykepengerServiceBi sykepengerServiceBi;
    @Inject
    ForeldrepengerServiceBi foreldrepengerServiceBi;

    private SykepengerWidgetService defaultWidgetService = new SykmeldingsperioderPanelConfigImpl(sykepengerServiceBi, foreldrepengerServiceBi).sykepengerWidgetService();
    private SykepengerWidgetService mockWidgetService = getSykepengerWidgetServiceMock();
    private SykmeldingsperiodeLoader periodeLoader = new SykmeldingsperioderPanelConfigImpl(sykepengerServiceBi, foreldrepengerServiceBi).sykmeldingsperiodeLoader();
    private SykmeldingsperiodeLoader periodeLoaderMock = new SykmeldingsperioderPanelConfigImpl(sykepengerServiceBi, foreldrepengerServiceBi).sykmeldingsperiodeLoader();
    private ForeldrepengerLoader foreldrepengerLoader = new SykmeldingsperioderPanelConfigImpl(sykepengerServiceBi, foreldrepengerServiceBi).foreldrepengerLoader();
    private ForeldrepengerLoader foreldrepengerLoaderMock = getForeldrepengerLoaderMock();
    private SykmeldingsperioderPing ping = new SykmeldingsperioderPanelConfigImpl(sykepengerServiceBi, foreldrepengerServiceBi).sykmeldingsperioderPing();
    private SykmeldingsperioderPing pingMock = getSykmeldingsperioderPingMock();
    private String key = "start.kjerneinfo.withintegration";

    @Bean
    SykmeldingsperioderPing sykmeldingsperioderPing() {
        return createSwitcher(ping, pingMock, key, SykmeldingsperioderPing.class);
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return createSwitcher(defaultWidgetService, mockWidgetService, key, SykepengerWidgetService.class);
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return createSwitcher(periodeLoader, periodeLoaderMock, key, SykmeldingsperiodeLoader.class);
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        return createSwitcher(foreldrepengerLoader, foreldrepengerLoaderMock, key, ForeldrepengerLoader.class);
    }

}
