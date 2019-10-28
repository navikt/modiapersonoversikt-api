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

@Configuration
public class SykmeldingsperioderPanelConfigResolver {

    @Inject
    @Qualifier("sykepengerServiceDefault")
    private Wrapper<SykepengerServiceBi> sykepengerServiceDefault;

    @Inject
    @Qualifier("foreldrepengerServiceDefault")
    private Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault;

    @Inject
    private PleiepengerService pleiepengerServiceImpl;

    private ForeldrepengerServiceBi getForeldrepengerService() {
        return new ForeldrepengerServiceBi() {
            @Override
            public ForeldrepengerListeResponse hentForeldrepengerListe(ForeldrepengerListeRequest request) {
                return foreldrepengerServiceDefault.wrappedObject.hentForeldrepengerListe(request);
            }

        };
    }

    private SykepengerServiceBi getSykepengerService() {
        return new SykepengerServiceBi() {
            @Override
            public SykepengerResponse hentSykmeldingsperioder(SykepengerRequest request) {
                return sykepengerServiceDefault.wrappedObject.hentSykmeldingsperioder(request);
            }

        };
    }

}
