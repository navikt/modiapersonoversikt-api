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
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getForeldrepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerWidgetServiceMock;

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    SykepengerServiceBi sykepengerServiceBi;
    @Inject
    ForeldrepengerServiceBi foreldrepengerServiceBi;
    private ForeldrepengerServiceBi foreldrepengerServiceBiMock;
    private SykepengerServiceBi sykepengerServiceBiMock;

    private SykmeldingsperioderPanelConfigImpl sykmeldingsperioderImpl = new SykmeldingsperioderPanelConfigImpl();

    private SykepengerWidgetService defaultWidgetService = sykmeldingsperioderImpl.sykepengerWidgetService();
    private SykepengerWidgetService mockWidgetService = getSykepengerWidgetServiceMock();
    private SykmeldingsperiodeLoader periodeLoader = sykmeldingsperioderImpl.sykmeldingsperiodeLoader();
    private ForeldrepengerLoader foreldrepengerLoader = sykmeldingsperioderImpl.foreldrepengerLoader();
    private SykmeldingsperioderPing ping;
    private String key = "start.kjerneinfo.withmock";

    public SykmeldingsperioderPanelConfigResolver() {

        foreldrepengerServiceBiMock = getForeldrepengerServiceBiMock();
        sykepengerServiceBiMock = getSykepengerServiceBiMock();

        ping = sykmeldingsperioderImpl.sykmeldingsperioderPing(foreldrepengerServiceBi(), sykepengerServiceBi());
    }

    @Bean
    SykmeldingsperioderPing sykmeldingsperioderPing() {
        return ping;
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return createSwitcher(defaultWidgetService, mockWidgetService, key, SykepengerWidgetService.class);
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        periodeLoader.setSykepengerService(sykepengerServiceBi());
        return periodeLoader;
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        foreldrepengerLoader.setForeldrepengerService(foreldrepengerServiceBi());
        return foreldrepengerLoader;
    }

    private ForeldrepengerServiceBi foreldrepengerServiceBi() {
        return createSwitcher(foreldrepengerServiceBi, foreldrepengerServiceBiMock, key, ForeldrepengerServiceBi.class);
    }

    private SykepengerServiceBi sykepengerServiceBi() {
        return createSwitcher(sykepengerServiceBi, sykepengerServiceBiMock, key, SykepengerServiceBi.class);
    }


}
