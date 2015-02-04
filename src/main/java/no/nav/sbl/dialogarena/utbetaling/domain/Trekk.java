package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

@SuppressWarnings("all")
public interface Trekk {
    Key<String> trekksType = new Key<>("TREKK_TYPE");
    Key<Double> trekkBeloep = new Key<>("TREKK_BELOEP");
    Key<String> kreditor = new Key<>("KREDITOR");
}
