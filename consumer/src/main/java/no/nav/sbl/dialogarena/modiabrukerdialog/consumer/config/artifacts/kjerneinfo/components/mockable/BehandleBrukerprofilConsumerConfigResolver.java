package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.behandlebrukerprofil.config.spring.BehandleBrukerprofilConsumerConfig;
import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.behandlebrukerprofil.consumer.support.mapping.BehandleBrukerprofilMapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

@Configuration
@Import({
        BehandleBrukerprofilConsumerConfig.class
})
public class BehandleBrukerprofilConsumerConfigResolver {

    @Inject
    private BehandleBrukerprofilPortType behandleBrukerprofilPortType;

    @Inject
    private BehandleBrukerprofilPortType selfTestBehandleBrukerprofilPortType;

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return new DefaultBehandleBrukerprofilService(behandleBrukerprofilPortType, selfTestBehandleBrukerprofilPortType, new BehandleBrukerprofilMapper());
    }

}
