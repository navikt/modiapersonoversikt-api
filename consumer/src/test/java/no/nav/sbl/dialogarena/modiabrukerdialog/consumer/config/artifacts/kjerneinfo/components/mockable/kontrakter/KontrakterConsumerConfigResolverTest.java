package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.KontrakterConsumerConfigResolver;
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
        KontrakterConsumerConfigResolver.class,
        KontrakterWrapperTestConfig.class
})
public class KontrakterConsumerConfigResolverTest {

    @Inject
    private Wrapper<YtelseskontraktServiceBi> ytelseskontraktService;

    @Inject
    private Wrapper<OppfolgingskontraktServiceBi> oppfolgingskontraktService;

    @Inject
    private KontrakterConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "http://ja.nav.no");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.oppfolgingskontraktServiceBi().hentOppfolgingskontrakter(new OppfolgingskontraktRequest());
        verifyZeroInteractions(oppfolgingskontraktService.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() throws HentKontaktinformasjonOgPreferanserSikkerhetsbegrensning, HentKontaktinformasjonOgPreferanserPersonIkkeFunnet {
        setProperty(TILLATMOCKSETUP_PROPERTY, "nei");
        resolver.ytelseskontraktServiceBi().hentYtelseskontrakter(new YtelseskontraktRequest());
        verify(ytelseskontraktService.wrappedObject, times(1)).hentYtelseskontrakter(any(YtelseskontraktRequest.class));
    }

}
