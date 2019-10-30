package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.kjerneinfomapper;

import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.KjerneinfoMapperConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.Mockito.verifyZeroInteractions;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        KjerneinfoMapperConfigResolver.class,
        KjerneinfoMapperWrapperTestConfig.class

})
public class KjerneinfoMapperConfigResolverTest {

    @Inject
    private Wrapper<KodeverkmanagerBi> kodeverkManagerService;

    @Inject
    private KjerneinfoMapperConfigResolver resolver;

    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() {
        resolver.kjerneinfoMapperBean().map(new Object(), new Object());
        verifyZeroInteractions(kodeverkManagerService.wrappedObject);
    }

}
