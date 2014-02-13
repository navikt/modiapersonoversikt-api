package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifacts.kjerneinfo.components.mockable.mockableimpl;

import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi;
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi;
import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;
import no.nav.sykmeldingsperioder.ping.SykmeldingsperioderPing;

public class SykmeldingsperioderPanelConfigImpl {

    public SykmeldingsperioderPing sykmeldingsperioderPing(ForeldrepengerServiceBi foreldrepengerServiceBi, SykepengerServiceBi sykepengerServiceBi) {
        return new SykmeldingsperioderPing(foreldrepengerServiceBi, sykepengerServiceBi);
    }

    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    public ForeldrepengerLoader foreldrepengerLoader() {
        return new ForeldrepengerLoader();
    }

}
