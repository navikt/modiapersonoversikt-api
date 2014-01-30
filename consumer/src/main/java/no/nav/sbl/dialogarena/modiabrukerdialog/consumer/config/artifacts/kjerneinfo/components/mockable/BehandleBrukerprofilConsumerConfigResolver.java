package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.behandlebrukerprofil.config.spring.BehandleBrukerprofilConsumerConfig;
import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.modig.modia.ping.PingResult;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl.BehandleBrukerProfilPortTypeImpl;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;
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

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        final BehandleBrukerprofilServiceBi defaultBi = new BehandleBrukerProfilPortTypeImpl(behandleBrukerprofilPortType, selfTestBehandleBrukerprofilPortType).behandleBrukerprofilServiceBi();
        final BehandleBrukerprofilServiceBi alternateBi = getBehandleBrukerprofilServiceBiMock();

        return new BehandleBrukerprofilServiceBi() {

            @Override
            public void oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest request)
                    throws OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet, OppdaterKontaktinformasjonOgPreferanserUgyldigInput {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    alternateBi.oppdaterKontaktinformasjonOgPreferanser(request);
                } else {
                    defaultBi.oppdaterKontaktinformasjonOgPreferanser(request);
                }
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return alternateBi.ping();
                }
                return defaultBi.ping();
            }
        };
    }

}
