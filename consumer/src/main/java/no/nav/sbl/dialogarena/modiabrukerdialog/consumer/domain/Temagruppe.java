package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD,
    FMLI,
    HJLPM,
    BIL,
    ORT_HJE;

    public static final List<Temagruppe> UTGAAENDE_TEMAGRUPPER = asList(ARBD, FMLI, HJLPM);
}

