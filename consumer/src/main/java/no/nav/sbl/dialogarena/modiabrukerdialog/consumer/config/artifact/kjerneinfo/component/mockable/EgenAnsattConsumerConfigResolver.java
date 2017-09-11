package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;


import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattServiceImpl;
import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class EgenAnsattConsumerConfigResolver {

    @Inject
    @Qualifier("egenAnsattService")
    private Wrapper<EgenAnsattServiceImpl> egenAnsattService;

    @Inject
    @Qualifier("egenAnsattMockService")
    private Wrapper<EgenAnsattService> egenAnsattMockService;

    @Bean
    public EgenAnsattService egenAnsattServiceBi() {
        if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
            return egenAnsattMockService.wrappedObject;
        } else {
            return egenAnsattService.wrappedObject;
        }

    }

}
