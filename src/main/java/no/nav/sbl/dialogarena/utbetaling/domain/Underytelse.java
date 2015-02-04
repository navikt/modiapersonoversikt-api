package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Underytelse {
    Key<String> ytelsesType = new Key<>("YTELSE_TYPE");
    Key<Double> satsBeloep = new Key<>("SATS_BELOEP");
    Key<String> satsType = new Key<>("SATS_TYPE");
    Key<Double> satsAntall = new Key<>("SATS_ANTALL");
    Key<Double> ytelseBeloep = new Key<>("YTELSE_BELOEP");
}
