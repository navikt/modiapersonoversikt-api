package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.sykmeldingsperioder;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.SykmeldingsperioderPanelConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.utbtalinger.UtbetalingerServiceTestConfig;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        SykmeldingsperioderPanelConfigResolver.class,
        SykmeldingsperioderWrapperTestConfig.class,
        UtbetalingerServiceTestConfig.class
})
public class SykmeldingsperioderPanelConfigResolverTest {

    @Inject
    private SykepengerServiceBi sykepengerServiceDefault;

    @Inject
    private ForeldrepengerServiceBi foreldrepengerServiceDefault;

    @Inject
    private PleiepengerService pleiepengerServiceImpl;

    @Inject
    private SykmeldingsperioderPanelConfigResolver resolver;

//    @Ignore //trenger endring på SykepengerWidgetServiceImpl som må fjerne @Inject
//    @Test
    public void perDefaultSkalProdkodeEksekveres() {
        verify(sykepengerServiceDefault, times(1)).hentSykmeldingsperioder(any(SykepengerRequest.class));
    }

}
