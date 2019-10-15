package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeRequest;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.mapping.to.ForeldrepengerListeResponse;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerResponse;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private PleiepengerService pleiepengerServiceImpl;

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

}
