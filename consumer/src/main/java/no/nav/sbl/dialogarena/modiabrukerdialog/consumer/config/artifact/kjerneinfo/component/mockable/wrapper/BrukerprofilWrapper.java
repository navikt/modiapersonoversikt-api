package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.brukerprofil.config.spring.brukerprofil.BrukerprofilConsumerConfig;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl.BrukerprofilConsumerConfigImpl;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock;

@Configuration
@Import({BrukerprofilConsumerConfig.class})
public class BrukerprofilWrapper {

    @Inject
    private BrukerprofilV3 brukerprofilPortType;

    @Inject
    private CacheManager cacheManager;

    @Bean
    @Qualifier("brukerprofilService")
    public Wrapper<BrukerprofilServiceBi> brukerprofilService() {
        return new Wrapper<>(new BrukerprofilConsumerConfigImpl(brukerprofilPortType, cacheManager).brukerprofilServiceBi());
    }

    @Bean
    @Qualifier("brukerprofilMock")
    public Wrapper<BrukerprofilServiceBi> brukerprofilMock() {
        return new Wrapper<>(getBrukerprofilServiceBiMock());
    }
}
