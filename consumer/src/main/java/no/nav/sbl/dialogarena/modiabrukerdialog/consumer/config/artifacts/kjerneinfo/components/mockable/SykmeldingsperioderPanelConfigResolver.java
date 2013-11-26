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

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
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

    public SykmeldingsperioderPanelConfigResolver() {
        foreldrepengerServiceBiMock = getForeldrepengerServiceBiMock();
        sykepengerServiceBiMock = getSykepengerServiceBiMock();
    }

    @Bean
    SykmeldingsperioderPing sykmeldingsperioderPing() {
        return new SykmeldingsperioderPanelConfigImpl().sykmeldingsperioderPing(getForeldrepengerServiceBi(), getSykepengerServiceBi());
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        SykepengerWidgetService defaultWidgetService = new SykmeldingsperioderPanelConfigImpl().sykepengerWidgetService();
        SykepengerWidgetService mockWidgetService = getSykepengerWidgetServiceMock();
        return createSwitcher(defaultWidgetService, mockWidgetService, KJERNEINFO_KEY, SykepengerWidgetService.class);
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        SykmeldingsperiodeLoader periodeLoader = new SykmeldingsperioderPanelConfigImpl().sykmeldingsperiodeLoader();
        periodeLoader.setSykepengerService(getSykepengerServiceBi());
        return periodeLoader;
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        ForeldrepengerLoader foreldrepengerLoader = new SykmeldingsperioderPanelConfigImpl().foreldrepengerLoader();
        foreldrepengerLoader.setForeldrepengerService(getForeldrepengerServiceBi());
        return foreldrepengerLoader;
    }

    private ForeldrepengerServiceBi getForeldrepengerServiceBi() {
        return createSwitcher(foreldrepengerServiceBi, foreldrepengerServiceBiMock, KJERNEINFO_KEY, ForeldrepengerServiceBi.class);
    }

    private SykepengerServiceBi getSykepengerServiceBi() {
        return createSwitcher(sykepengerServiceBi, sykepengerServiceBiMock, KJERNEINFO_KEY, SykepengerServiceBi.class);
    }

}
