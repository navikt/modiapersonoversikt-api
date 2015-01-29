package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Underytelse {
    public Key<String> ytelsesType = new Key<>("YTELSE_TYPE");
    public Key<Double> satsBeloep = new Key<>("SATS_BELOEP");
    public Key<String> satsType = new Key<>("SATS_TYPE");
    public Key<Double> satsAntall = new Key<>("SATS_ANTALL");
    public Key<Double> ytelseBeloep = new Key<>("YTELSE_BELOEP");
}
