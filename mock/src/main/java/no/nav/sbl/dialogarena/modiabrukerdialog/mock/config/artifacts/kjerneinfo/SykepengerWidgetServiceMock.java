package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;

import static org.mockito.Mockito.mock;

public class SykepengerWidgetServiceMock {

    public static SykepengerWidgetService getSykepengerWidgetServiceMock() {
        return mock(SykepengerWidgetService.class);
    }

    public static ForeldrepengerServiceBi getForeldrepengerServiceBiMock() {
        return mock(ForeldrepengerServiceBi.class);
    }

    public static SykepengerServiceBi getSykepengerServiceBiMock() {
        return mock(SykepengerServiceBi.class);
    }
}
