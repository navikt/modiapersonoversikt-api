package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;


import no.nav.dkif.consumer.DkifServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattService;
import no.nav.kjerneinfo.consumer.fim.person.support.EgenAnsattServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattRequest;
import no.nav.tjeneste.pip.egen.ansatt.v1.WSHentErEgenAnsattEllerIFamilieMedEgenAnsattResponse;
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
    private Wrapper<EgenAnsattService> egenAnsattService;

    @Inject
    @Qualifier("egenAnsattMockService")
    private Wrapper<EgenAnsattServiceBi> egenAnsattMockService;

    @Bean
    public EgenAnsattServiceBi egenAnsattServiceBi() {
        if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
            return egenAnsattMockService.wrappedObject;
        } else {
            return egenAnsattService.wrappedObject;
        }

    }

}
