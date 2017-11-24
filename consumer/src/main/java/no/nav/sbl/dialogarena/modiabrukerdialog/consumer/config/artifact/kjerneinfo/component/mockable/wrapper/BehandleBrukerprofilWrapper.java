package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.wrapper;

import no.nav.behandlebrukerprofil.config.spring.BehandleBrukerprofilConsumerConfig;
import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.BehandleBrukerprofilV2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BehandleBrukerprofilServiceBiMock.getBehandleBrukerprofilServiceBiMock;

@Configuration
@Import({BehandleBrukerprofilConsumerConfig.class})
public class BehandleBrukerprofilWrapper {

    @Inject
    private BehandleBrukerprofilV2 behandleBrukerprofilPortType;

    @Inject
    private BehandleBrukerprofilV2 selfTestBehandleBrukerprofilPortType;

    @Inject
    private CacheManager cacheManager;

    @Bean
    @Qualifier("behandleBrukerprofilService")
    public Wrapper<DefaultBehandleBrukerprofilService> defaultService() {
        return new Wrapper<>(new DefaultBehandleBrukerprofilService(
                behandleBrukerprofilPortType,
                selfTestBehandleBrukerprofilPortType,
                BehandleBrukerprofilMapper.getInstance(),
                cacheManager
        ));
    }

    @Bean
    @Qualifier("behandleBrukerprofilMock")
    public Wrapper<BehandleBrukerprofilServiceBi> mockService() {
        return new Wrapper<>(getBehandleBrukerprofilServiceBiMock());
    }

}
