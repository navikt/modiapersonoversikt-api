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

    public static final List<Temagruppe> SAMTALEREFERAT = asList(ARBD, FMLI, HJLPM, PENS, OKSOS, ANSOS, OVRG);
    public static final List<Temagruppe> LEGG_TILBAKE = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE);
}

