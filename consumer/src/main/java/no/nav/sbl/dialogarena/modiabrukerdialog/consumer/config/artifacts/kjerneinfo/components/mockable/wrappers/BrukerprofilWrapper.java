package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers;

import no.nav.brukerprofil.config.spring.brukerprofil.BrukerprofilConsumerConfig;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.BrukerprofilConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock;

@Configuration
@Import({BrukerprofilConsumerConfig.class})
public class BrukerprofilWrapper {

    @Inject
    private BrukerprofilPortType brukerprofilPortType;

    @Inject
    private BrukerprofilPortType selfTestBrukerprofilPortType;

    @Bean
    @Qualifier("brukerprofilService")
    public Wrapper<BrukerprofilServiceBi> brukerprofilService() {
        return new Wrapper<>(new BrukerprofilConsumerConfigImpl(brukerprofilPortType, selfTestBrukerprofilPortType).brukerprofilServiceBi());
    }

    @Bean
    @Qualifier("brukerprofilMock")
    public Wrapper<BrukerprofilServiceBi> brukerprofilMock() {
        return new Wrapper<>(getBrukerprofilServiceBiMock());
    }
}
