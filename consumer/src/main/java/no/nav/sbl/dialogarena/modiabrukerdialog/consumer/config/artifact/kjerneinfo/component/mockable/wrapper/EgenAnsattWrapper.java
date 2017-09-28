package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import javax.inject.Named;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.EgenAnsattServiceBiMock.getEgenAnsattServiceBiMock;

@Import(EgenAnsattV1EndpointConfig.class)
public class EgenAnsattWrapper {

    @Inject
    private EgenAnsattV1 egenAnsattV1;

    @Bean
    @Named("egenAnsattService")
    public Wrapper<EgenAnsattService> egenAnsattService() {
        return new Wrapper<>(new EgenAnsattService(egenAnsattV1));
    }

    @Bean
    @Named("egenAnsattMockService")
    public Wrapper<EgenAnsattServiceBi> egenAnsattMockService() {
        return new Wrapper<>(getEgenAnsattServiceBiMock());
    }


}
