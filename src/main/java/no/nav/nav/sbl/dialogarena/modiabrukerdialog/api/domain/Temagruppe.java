package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD,
    FMLI,
    HJLPM,
    BIL,
    ORT_HJE,
    OVRG,
    PENS,
    OKSOS,
    ANSOS;

    public static final List<Temagruppe> INNGAAENDE = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE);
    public static final List<Temagruppe> UTGAAENDE = asList(ARBD, FMLI, HJLPM, OKSOS, ANSOS, OVRG);
}

