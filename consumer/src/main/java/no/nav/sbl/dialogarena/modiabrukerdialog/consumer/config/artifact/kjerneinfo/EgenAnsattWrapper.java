package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo;

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService;
import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattServiceImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.v1.egenansatt.EgenAnsattV1EndpointConfig;
import no.nav.tjeneste.pip.egen.ansatt.v1.EgenAnsattV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(EgenAnsattV1EndpointConfig.class)
public class EgenAnsattWrapper {

    @Autowired
    private EgenAnsattV1 egenAnsattV1;

    @Bean
    public EgenAnsattService egenAnsattService() {
        return new EgenAnsattServiceImpl(egenAnsattV1);
    }
}
