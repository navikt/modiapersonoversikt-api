package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.artifacts.kjerneinfo;

import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;

import static org.mockito.Mockito.mock;

public class SykepengerWidgetServiceMock {

    public static SykepengerWidgetService getSykepengerWidgetServiceMock() {
        return mock(SykepengerWidgetService.class);
    }

    public static SykmeldingsperiodeLoader getSykmeldingsperiodeLoaderMock() {
        return mock(SykmeldingsperiodeLoader.class);
    }

    public static ForeldrepengerLoader getForeldrepengerLoaderMock() {
        return mock(ForeldrepengerLoader.class);
    }

    public static SykmeldingsperioderPing getSykmeldingsperioderPingMock() {
        return mock(SykmeldingsperioderPing.class);
    }
}
