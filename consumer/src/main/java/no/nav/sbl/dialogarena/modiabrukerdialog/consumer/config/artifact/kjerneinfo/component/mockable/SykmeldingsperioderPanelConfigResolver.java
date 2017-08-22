package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.SykmeldingsperioderPanelConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.mapping.to.PleiepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.pleiepenger.loader.PleiepengerLoader;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

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

    @Inject
    @Qualifier("pleiepengerServiceImpl")
    private Wrapper<PleiepengerService> pleiepengerServiceImpl;

    @Inject
    @Qualifier("pleiepengerServiceMock")
    private Wrapper<PleiepengerService> pleiepengerServiceMock;



    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return new SykepengerWidgetServiceImpl(getSykepengerService(), getForeldrepengerService(), getPleiepengerService());
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

    @Bean
    public PleiepengerLoader pleiepengerLoader() {
        PleiepengerLoader pleiepengerLoader = new SykmeldingsperioderPanelConfigImpl().pleiepengerLoader();
        pleiepengerLoader.setPleiepengerService(getPleiepengerService());
        return pleiepengerLoader;
    }

    private ForeldrepengerServiceBi getForeldrepengerService() {
        return new ForeldrepengerServiceBi() {
            @Override
            public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return foreldrepengerServiceMock.wrappedObject.hentForeldrepengerListe(request);
                }
                return foreldrepengerServiceDefault.wrappedObject.hentForeldrepengerListe(request);
            }

        };
    }

    private SykepengerServiceBi getSykepengerService() {
        return new SykepengerServiceBi() {
            @Override
            public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
                if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return sykepengerServiceMock.wrappedObject.hentSykmeldingsperioder(request);
                }
                return sykepengerServiceDefault.wrappedObject.hentSykmeldingsperioder(request);
            }

        };
    }

    private PleiepengerService getPleiepengerService() {
        return new PleiepengerService() {
            @Override
            public PleiepengerListeResponse hentPleiepengerListe(PleiepengerListeRequest request) {
                if(mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return pleiepengerServiceMock.wrappedObject.hentPleiepengerListe(request);
                }
                return pleiepengerServiceImpl.wrappedObject.hentPleiepengerListe(request);
            }
        };
    }
}
