package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.brukerprofil;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.messages.BrukerprofilRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.BrukerprofilConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.wrappers.Wrapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        BrukerprofilConsumerConfigResolver.class,
        BrukerprofilWrapperTestConfig.class
})
public class BrukerprofilConsumerConfigResolverTest {

    @Inject
    private Wrapper<BrukerprofilServiceBi> brukerprofilService;

    @Inject
    private BrukerprofilConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "http://ja.nav.no");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.brukerprofilServiceBi().hentKontaktinformasjonOgPreferanser(new BrukerprofilRequest("ident"));
        verifyZeroInteractions(brukerprofilService.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "nei");
        resolver.brukerprofilServiceBi().hentKontaktinformasjonOgPreferanser(new BrukerprofilRequest("ident"));
        verify(brukerprofilService.wrappedObject, times(1)).hentKontaktinformasjonOgPreferanser(any(BrukerprofilRequest.class));
    }


}
