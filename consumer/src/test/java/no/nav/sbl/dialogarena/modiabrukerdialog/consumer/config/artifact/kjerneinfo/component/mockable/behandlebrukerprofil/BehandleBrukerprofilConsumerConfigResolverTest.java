package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.BehandleBrukerprofilConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        BehandleBrukerprofilWrapperTestConfig.class,
        BehandleBrukerprofilConsumerConfigResolver.class})
public class BehandleBrukerprofilConsumerConfigResolverTest {

    @Inject
    private Wrapper<DefaultBehandleBrukerprofilService> defaultService;

    @Inject
    private BehandleBrukerprofilConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet, OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verifyZeroInteractions(defaultService.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet, OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt {
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verify(defaultService.wrappedObject, times(1)).oppdaterKontaktinformasjonOgPreferanser(any(BehandleBrukerprofilRequest.class));
    }

}
