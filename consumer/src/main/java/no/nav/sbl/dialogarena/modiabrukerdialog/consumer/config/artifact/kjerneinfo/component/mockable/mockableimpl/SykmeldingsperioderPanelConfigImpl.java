package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.sykmeldingsperioder.foreldrepenger.loader.ForeldrepengerLoader;
import no.nav.sykmeldingsperioder.loader.SykmeldingsperiodeLoader;

public class SykmeldingsperioderPanelConfigImpl {

    public SykmeldingsperiodeLoader sykmeldingsperiodeLoader() {
        return new SykmeldingsperiodeLoader();
    }

    public ForeldrepengerLoader foreldrepengerLoader() {
        return new ForeldrepengerLoader();
    }

}
