package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.behandlebrukerprofil;

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi;
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest;
import no.nav.brukerprofil.domain.Bruker;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.BehandleBrukerprofilConsumerConfigResolver;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.OppdaterKontaktinformasjonOgPreferanserUgyldigInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Properties;

import static java.lang.System.setProperties;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        BehandleBrukerprofilWrapperTestConfig.class,
        BehandleBrukerprofilConsumerConfigResolver.class})
public class BehandleBrukerprofilConsumerConfigResolverTest {

    @Inject
    @Qualifier("behandleBrukerprofilService")
    private BehandleBrukerprofilServiceBi defaultService;

    @Inject
    private BehandleBrukerprofilConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        Properties props = new Properties();
        props.setProperty(TILLATMOCKSETUP_PROPERTY, "http://ja.nav.no");
        props.setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        setProperties(props);
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verifyZeroInteractions(defaultService);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws OppdaterKontaktinformasjonOgPreferanserUgyldigInput, OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning, OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        Properties props = new Properties();
        props.setProperty(TILLATMOCKSETUP_PROPERTY, "nei");
        setProperties(props);
        resolver.behandleBrukerprofilServiceBi().oppdaterKontaktinformasjonOgPreferanser(new BehandleBrukerprofilRequest(new Bruker()));
        verify(defaultService, times(1)).oppdaterKontaktinformasjonOgPreferanser(any(BehandleBrukerprofilRequest.class));
    }

}
