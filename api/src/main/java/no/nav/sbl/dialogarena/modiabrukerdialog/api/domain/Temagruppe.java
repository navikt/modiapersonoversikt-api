package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD,
    HELSE,
    FMLI,
    FDAG,
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

    public static final List<Temagruppe> SAMTALEREFERAT = asList(ARBD, HELSE, FMLI, HJLPM, PENS, OVRG, OKSOS, ANSOS);
    public static final List<Temagruppe> LEGG_TILBAKE = asList(ARBD, HELSE, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = asList(ARBD, HELSE, FMLI, FDAG, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OVRG);
    public static final List<Temagruppe> KOMMUNALE_TJENESTER = asList(OKSOS, ANSOS);
}
