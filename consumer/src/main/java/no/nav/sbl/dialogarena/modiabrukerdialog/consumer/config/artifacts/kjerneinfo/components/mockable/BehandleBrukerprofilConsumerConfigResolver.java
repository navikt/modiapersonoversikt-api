package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.modig.modia.ping.PingResult;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockErSlaattPaaForKey;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.mockSetupErTillatt;

@Configuration
public class BehandleBrukerprofilConsumerConfigResolver {

    @Inject
    @Qualifier("behandleBrukerprofilService")
    private BehandleBrukerprofilServiceBi defaultService;

    @Inject
    @Qualifier("behandleBrukerprofilMock")
    private BehandleBrukerprofilServiceBi mockService;

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return new BehandleBrukerprofilServiceBi() {

            @Override
            public void oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest request)
                    throws OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet, OppdaterKontaktinformasjonOgPreferanserUgyldigInput {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    mockService.oppdaterKontaktinformasjonOgPreferanser(request);
                } else {
                    defaultService.oppdaterKontaktinformasjonOgPreferanser(request);
                }
            }

            @Override
            public PingResult ping() {
                if (mockSetupErTillatt() && mockErSlaattPaaForKey(KJERNEINFO_KEY)) {
                    return mockService.ping();
                }
                return defaultService.ping();
            }
        };
    }

}
