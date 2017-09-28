package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KontrakterConsumerConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
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
        KontrakterConsumerConfigResolver.class,
        KontrakterWrapperTestConfig.class
})
public class KontrakterConsumerConfigResolverTest {

    @Inject
    private Wrapper<DefaultYtelseskontraktService> ytelseskontraktService;

    @Inject
    private Wrapper<DefaultOppfolgingskontraktService> oppfolgingskontraktService;

    @Inject
    private KontrakterConsumerConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        resolver.oppfolgingskontraktServiceBi().hentOppfolgingskontrakter(new OppfolgingskontraktRequest());
        resolver.ytelseskontraktServiceBi().hentYtelseskontrakter(new YtelseskontraktRequest());
        verifyZeroInteractions(oppfolgingskontraktService.wrappedObject);
        verifyZeroInteractions(ytelseskontraktService.wrappedObject);
    }

    @Test
    public void perDefaultSkalProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "false");
        resolver.ytelseskontraktServiceBi().hentYtelseskontrakter(new YtelseskontraktRequest());
        resolver.oppfolgingskontraktServiceBi().hentOppfolgingskontrakter(new OppfolgingskontraktRequest());
        verify(ytelseskontraktService.wrappedObject, times(1)).hentYtelseskontrakter(any(YtelseskontraktRequest.class));
        verify(oppfolgingskontraktService.wrappedObject, times(1)).hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class));
    }

}
