package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.behandlebrukerprofil.config.spring.BehandleBrukerprofilConsumerConfig;
import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.BehandleBrukerProfilPortTypeImpl;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BehandleBrukerprofilServiceBiMock.getBehandleBrukerprofilServiceBiMock;

@Configuration
@Import({
        BehandleBrukerprofilConsumerConfig.class
})
public class BehandleBrukerprofilConsumerConfigResolver {

    @Inject
    private BehandleBrukerprofilPortType behandleBrukerprofilPortType;
    @Inject
    private BehandleBrukerprofilPortType selfTestBehandleBrukerprofilPortType;

    private BehandleBrukerprofilServiceBi defaultBi = new BehandleBrukerProfilPortTypeImpl(behandleBrukerprofilPortType, selfTestBehandleBrukerprofilPortType).behandleBrukerprofilServiceBi();
    private BehandleBrukerprofilServiceBi alternateBi = getBehandleBrukerprofilServiceBiMock();

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        String key = "start.kjerneinfo.withintegration";
        return createSwitcher(defaultBi, alternateBi, key, BehandleBrukerprofilServiceBi.class);
    }

}
