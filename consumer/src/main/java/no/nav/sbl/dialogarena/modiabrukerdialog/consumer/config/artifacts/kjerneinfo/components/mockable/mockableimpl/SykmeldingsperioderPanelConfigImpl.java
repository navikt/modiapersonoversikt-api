package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;

public class SykmeldingsperioderPanelConfigImpl {

    SykepengerServiceBi sykepengerServiceBi;
    ForeldrepengerServiceBi foreldrepengerServiceBi;

    public SykmeldingsperioderPanelConfigImpl(SykepengerServiceBi sykepengerServiceBi, ForeldrepengerServiceBi foreldrepengerServiceBi) {
        this.sykepengerServiceBi = sykepengerServiceBi;
        this.foreldrepengerServiceBi = foreldrepengerServiceBi;
    }

    public SykmeldingsperioderPing sykmeldingsperioderPing() {
        return new SykmeldingsperioderPing(foreldrepengerServiceBi, sykepengerServiceBi);
    }

    public SykepengerWidgetService sykepengerWidgetService() {
        return new SykepengerWidgetServiceImpl();
    }

    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    public ForeldrepengerLoader foreldrepengerLoader() {
        return new ForeldrepengerLoader();
    }

}
