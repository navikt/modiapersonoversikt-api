package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.SykmeldingsperioderPanelConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
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
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockSetupErTillatt;

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    @Qualifier("sykepengerServiceDefault")
    private Wrapper<SykepengerServiceBi> sykepengerServiceDefault;

    @Inject
    @Qualifier("sykepengerServiceMock")
    private Wrapper<SykepengerServiceBi> sykepengerServiceMock;

    @Inject
    @Qualifier("foreldrepengerServiceDefault")
    private Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault;

    @Inject
    @Qualifier("foreldrepengerServiceMock")
    private Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceMock;

    @Bean
    public SykmeldingsperioderPing sykmeldingsperioderPing() {
        return new SykmeldingsperioderPanelConfigImpl().sykmeldingsperioderPing(getForeldrepengerService(), getSykepengerService());
    }

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return new SykepengerWidgetServiceImpl(getSykepengerService(), getForeldrepengerService());
    }

    @Bean
    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        SykmeldingsperiodeLoader periodeLoader = new SykmeldingsperioderPanelConfigImpl().sykmeldingsperiodeLoader();
        periodeLoader.setSykepengerService(getSykepengerService());
        return periodeLoader;
    }

    @Bean
    public ForeldrepengerLoader foreldrepengerLoader() {
        ForeldrepengerLoader foreldrepengerLoader = new SykmeldingsperioderPanelConfigImpl().foreldrepengerLoader();
        foreldrepengerLoader.setForeldrepengerService(getForeldrepengerService());
        return foreldrepengerLoader;
    }

    private ForeldrepengerServiceBi getForeldrepengerService() {
        return new ForeldrepengerServiceBi() {
            @Override
            public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return foreldrepengerServiceMock.wrappedObject.hentForeldrepengerListe(request);
                }
                return foreldrepengerServiceDefault.wrappedObject.hentForeldrepengerListe(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return foreldrepengerServiceMock.wrappedObject.ping();
                }
                return foreldrepengerServiceDefault.wrappedObject.ping();
            }
        };
    }

    private SykepengerServiceBi getSykepengerService() {
        return new SykepengerServiceBi() {
            @Override
            public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return sykepengerServiceMock.wrappedObject.hentSykmeldingsperioder(request);
                }
                return sykepengerServiceDefault.wrappedObject.hentSykmeldingsperioder(request);
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return sykepengerServiceMock.wrappedObject.ping();
                }
                return sykepengerServiceDefault.wrappedObject.ping();
            }
        };
    }

}
