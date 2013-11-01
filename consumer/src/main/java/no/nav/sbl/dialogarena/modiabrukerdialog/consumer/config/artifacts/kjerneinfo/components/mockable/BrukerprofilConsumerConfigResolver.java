package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.brukerprofil.config.spring.brukerprofil.BrukerprofilConsumerConfig;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.support.DefaultBrukerprofilService;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({ BrukerprofilConsumerConfig.class })
public class BrukerprofilConsumerConfigResolver {

    @Inject
    private BrukerprofilPortType brukerprofilPortType;

    @Inject
    private BrukerprofilPortType selfTestBrukerprofilPortType;

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return new DefaultBrukerprofilService(brukerprofilPortType, selfTestBrukerprofilPortType, new BrukerprofilMapper());
    }

}
