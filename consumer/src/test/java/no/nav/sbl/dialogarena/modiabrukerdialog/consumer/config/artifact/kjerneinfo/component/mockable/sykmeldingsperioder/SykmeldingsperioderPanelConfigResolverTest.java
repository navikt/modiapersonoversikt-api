package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.sykmeldingsperioder;


import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.SykmeldingsperioderPanelConfigResolver;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.utbtalinger.UtbetalingerServiceTestConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper;
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.mapping.to.SykepengerRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static java.lang.System.setProperty;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.ALLOW_MOCK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.TILLATMOCKSETUP_PROPERTY;
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
    @Qualifier("sykepengerServiceDefault")
    private Wrapper<SykepengerServiceBi> sykepengerServiceDefault;

    @Inject
    @Qualifier("foreldrepengerServiceDefault")
    private Wrapper<ForeldrepengerServiceBi> foreldrepengerServiceDefault;

    @Inject
    @Qualifier("pleiepengerServiceImpl")
    private Wrapper<PleiepengerService> pleiepengerServiceImpl;

    @Inject
    private SykmeldingsperioderPanelConfigResolver resolver;

    @Disabled
    @Test
    public void medMockSlaattPaaSkalIkkeProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "true");
        setProperty(KJERNEINFO_KEY, ALLOW_MOCK);
        verifyZeroInteractions(sykepengerServiceDefault.wrappedObject);
    }

//    @Ignore //trenger endring på SykepengerWidgetServiceImpl som må fjerne @Inject
//    @Test
    public void perDefaultSkalProdkodeEksekveres() {
        setProperty(TILLATMOCKSETUP_PROPERTY, "false");
        verify(sykepengerServiceDefault.wrappedObject, times(1)).hentSykmeldingsperioder(any(SykepengerRequest.class));
    }

}
