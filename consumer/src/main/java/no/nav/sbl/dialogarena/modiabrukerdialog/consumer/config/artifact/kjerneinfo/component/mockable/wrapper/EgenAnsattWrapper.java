package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;

@Import(EgenAnsattV1EndpointConfig.class)
public class EgenAnsattWrapper {

    @Inject
    private EgenAnsattV1 egenAnsattV1;

    @Bean
    @Named("egenAnsattService")
    public Wrapper<EgenAnsattServiceImpl> egenAnsattService() {
        return new Wrapper<>(new EgenAnsattServiceImpl(egenAnsattV1));
    }
}
