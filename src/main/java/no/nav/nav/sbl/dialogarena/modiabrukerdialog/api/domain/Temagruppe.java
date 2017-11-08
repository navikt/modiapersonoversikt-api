package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain;

import java.util.List;

import static java.lang.System.getProperty;
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
    public static final List<Temagruppe> LEGG_TILBAKE = getProperty("visNyeKoer", "false").equals("true")
            ? asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OKSOS, ANSOS)
            : asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, UFRT, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = getProperty("visNyeKoer", "false").equals("true")
            ? asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND)
            : asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, UFRT);
    public static final List<Temagruppe> KOMMUNALE_TJENESTER = asList(OKSOS, ANSOS);
}
