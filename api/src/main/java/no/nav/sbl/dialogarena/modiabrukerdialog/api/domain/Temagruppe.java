package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

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
    PLEIEPENGERSY,
    UFRT,
    UTLAND,
    OKSOS,
    ANSOS;

    public static final List<Temagruppe> SAMTALEREFERAT = asList(ARBD, FMLI, HJLPM, PENS, OVRG, OKSOS, ANSOS);
    public static final List<Temagruppe> LEGG_TILBAKE = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND);
    public static final List<Temagruppe> KOMMUNALE_TJENESTER = asList(OKSOS, ANSOS);
}
