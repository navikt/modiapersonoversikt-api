package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD,
    FMLI,
    PLEIE,
    HJLPM,
    BIL,
    INTER,
    ORT_HJE,
    OVRG,
    PENS,
    UFRT,
    OKSOS,
    ANSOS;

    public static final List<Temagruppe> SAMTALEREFERAT = asList(ARBD, FMLI, HJLPM, PENS, OVRG, OKSOS, ANSOS);
    public static final List<Temagruppe> LEGG_TILBAKE = asList(ARBD, FMLI, PLEIE, HJLPM, BIL, INTER, ORT_HJE, PENS, UFRT, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = asList(ARBD, FMLI, PLEIE, HJLPM, BIL, INTER, ORT_HJE, PENS, UFRT);
    public static final List<Temagruppe> KOMMUNALE_TJENESTER = asList(OKSOS, ANSOS);
}
