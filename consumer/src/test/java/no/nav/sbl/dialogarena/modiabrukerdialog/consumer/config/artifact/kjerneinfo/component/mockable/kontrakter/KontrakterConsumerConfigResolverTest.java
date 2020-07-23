package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.kontrakter;

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.support.DefaultOppfolgingskontraktService;
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.support.DefaultYtelseskontraktService;
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KontrakterConsumerConfigResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        KontrakterConsumerConfigResolver.class,
        KontrakterWrapperTestConfig.class
})
public class KontrakterConsumerConfigResolverTest {

    @Inject
    private DefaultYtelseskontraktService ytelseskontraktService;

    @Inject
    private DefaultOppfolgingskontraktService oppfolgingskontraktService;

    @Inject
    private KontrakterConsumerConfigResolver resolver;

    @Test
    public void perDefaultSkalProdkodeEksekveres() {
        resolver.ytelseskontraktServiceBi().hentYtelseskontrakter(new YtelseskontraktRequest());
        resolver.oppfolgingskontraktServiceBi().hentOppfolgingskontrakter(new OppfolgingskontraktRequest());
        verify(ytelseskontraktService, times(1)).hentYtelseskontrakter(any(YtelseskontraktRequest.class));
        verify(oppfolgingskontraktService, times(1)).hentOppfolgingskontrakter(any(OppfolgingskontraktRequest.class));
    }

}
