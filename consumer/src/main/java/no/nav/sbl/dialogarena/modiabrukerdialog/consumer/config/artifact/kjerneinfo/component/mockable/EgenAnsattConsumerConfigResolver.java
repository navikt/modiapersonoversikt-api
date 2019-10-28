package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;


import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class EgenAnsattConsumerConfigResolver {

    @Inject
    @Qualifier("egenAnsattService")
    private Wrapper<EgenAnsattServiceImpl> egenAnsattService;

    @Bean
    public EgenAnsattService egenAnsattServiceBi() {
        return egenAnsattService.wrappedObject;

    }

}
