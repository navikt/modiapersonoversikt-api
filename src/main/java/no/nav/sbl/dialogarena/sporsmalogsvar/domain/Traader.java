package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils;

import java.util.List;
import java.util.Map;

public class Traader {

    private final Map<String, List<Melding>> traader;

    public Traader(List<Melding> meldinger) {
        this.traader = MeldingUtils.skillUtTraader(meldinger);
        sammenslaFullforteDelsvar();
    }

    private void sammenslaFullforteDelsvar() {

    }

    public Map<String, List<Melding>> getTraader() {
        return traader;
    }

    public boolean erUtenMeldinger() {
        return traader.size() == 0;
    }
}
