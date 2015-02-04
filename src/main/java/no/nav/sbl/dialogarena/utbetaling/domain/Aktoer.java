package no.nav.sbl.dialogarena.utbetaling.domain;

import no.nav.sbl.dialogarena.common.records.Key;

public interface Aktoer {
    Key<String> aktoerId = new Key<>("AKTOER_ID");
    Key<String> navn = new Key<>("NAVN");
}
