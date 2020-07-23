package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


@Configuration
public class SykmeldingsperioderWrapper {

    @Inject
    private SykepengerServiceBi sykepengerServiceBi;

    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceBi;

    @Bean
    public SykepengerServiceBi sykepengerServiceDefault() {
        return sykepengerServiceBi;
    }

    @Bean
    public ForeldrepengerServiceBi foreldrepengerServiceDefault() {
        return foreldrepengerServiceBi;
    }
}
