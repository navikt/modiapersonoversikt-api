package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.behandlebrukerprofil.consumer.support.DefaultBehandleBrukerprofilService;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.BehandleBrukerprofilConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        BehandleBrukerprofilWrapperTestConfig.class,
        BehandleBrukerprofilConsumerConfigResolver.class})
public class BehandleBrukerprofilConsumerConfigResolverTest {

    @Inject
    private Wrapper<DefaultBehandleBrukerprofilService> defaultService;

    @Inject
    private BehandleBrukerprofilConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verifyZeroInteractions(defaultService.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "false");
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verify(defaultService.wrappedObject, times(1)).oppdaterKontaktinformasjonOgPreferanser(any(BehandleBrukerprofilRequest.class));
    }

}
