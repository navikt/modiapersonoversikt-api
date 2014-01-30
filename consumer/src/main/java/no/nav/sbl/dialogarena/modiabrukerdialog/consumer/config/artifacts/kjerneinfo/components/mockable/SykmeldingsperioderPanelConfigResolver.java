package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.modig.modia.ping.PingResult;
import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.SykmeldingsperioderPanelConfigImpl;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getForeldrepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerServiceBiMock;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.SykepengerWidgetServiceMock.getSykepengerWidgetServiceMock;

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    private SykepengerServiceBi sykepengerServiceBi;
    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceBi;

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
        final SykepengerWidgetService defaultWidgetService = new SykmeldingsperioderPanelConfigImpl().sykepengerWidgetService();
        final SykepengerWidgetService mockWidgetService = getSykepengerWidgetServiceMock();
        return new SykepengerWidgetService() {
            @Override
            public List<InfoPanelVM> getWidgetContent(String fnr) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockWidgetService.getWidgetContent(fnr);
                }
                return defaultWidgetService.getWidgetContent(fnr);
            }
        };
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
        return new ForeldrepengerServiceBi() {
            @Override
            public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return foreldrepengerServiceBiMock.hentForeldrepengerListe(request);
                }
                return foreldrepengerServiceBi.hentForeldrepengerListe(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return foreldrepengerServiceBiMock.ping();
                }
                return foreldrepengerServiceBi.ping();
            }
        };
    }

    private SykepengerServiceBi getSykepengerServiceBi() {
        return new SykepengerServiceBi() {
            @Override
            public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return sykepengerServiceBiMock.hentSykmeldingsperioder(request);
                }
                return sykepengerServiceBi.hentSykmeldingsperioder(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return sykepengerServiceBiMock.ping();
                }
                return sykepengerServiceBi.ping();
            }
        };
    }

}
