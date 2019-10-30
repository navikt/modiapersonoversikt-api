package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class BehandleBrukerprofilConsumerConfigResolver {

    @Inject
    private Wrapper<DefaultBehandleBrukerprofilService> defaultService;

    @Inject
    private Wrapper<BehandleBrukerprofilServiceBi> mockService;

    @Bean
    public BehandleBrukerprofilServiceBi behandleBrukerprofilServiceBi() {
        return new BehandleBrukerprofilServiceBi() {

            @Override
            public void oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest request)
                    throws OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet, OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
                defaultService.wrappedObject.oppdaterKontaktinformasjonOgPreferanser(request);
            }

        };
    }

}
