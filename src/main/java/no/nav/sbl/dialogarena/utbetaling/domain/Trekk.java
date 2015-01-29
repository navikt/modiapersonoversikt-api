package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Trekk {
    public Key<String> trekksType = new Key<>("TREKK_TYPE");
    public Key<Double> trekkBeloep = new Key<>("TREKK_BELOEP");
    public Key<String> kreditor = new Key<>("KREDITOR");
}
