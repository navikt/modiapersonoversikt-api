package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.brukerprofil.config.spring.brukerprofil.BrukerprofilConsumerConfig;
import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.BrukerprofilConsumerConfigImpl;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.InstanceSwitcher.createSwitcher;
import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo.BrukerprofilServiceBiMock.getBrukerprofilServiceBiMock;

@Configuration
@Import({BrukerprofilConsumerConfig.class})
public class BrukerprofilConsumerConfigResolver {
    private static final String KEY = "start.kjerneinfo.withmock";

    @Inject
    private BrukerprofilPortType brukerprofilPortType;
    @Inject
    private BrukerprofilPortType selfTestBrukerprofilPortType;

    @Bean
    public BrukerprofilServiceBi brukerprofilServiceBi() {
        BrukerprofilServiceBi defaultBi = new BrukerprofilConsumerConfigImpl(brukerprofilPortType, selfTestBrukerprofilPortType).brukerprofilServiceBi();
        BrukerprofilServiceBi alternateBi = getBrukerprofilServiceBiMock();
        return createSwitcher(defaultBi, alternateBi, KEY, BrukerprofilServiceBi.class);
    }

}
