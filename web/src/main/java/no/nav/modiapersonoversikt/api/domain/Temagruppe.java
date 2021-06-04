package no.nav.modiapersonoversikt.api.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD("ARBD_KNA"),
    HELSE("HELSE_KNA"),
    FMLI("FMLI_KNA"),
    FDAG("FDAG_KNA"),
    HJLPM("HJLPM_KNA"),
    BIL("BIL_KNA"),
    ORT_HJE("ORT_HJE_KNA"),
    OVRG("OVRG_KNA"),
    PENS("PENS_KNA"),
    PLEIEPENGERSY("PLEIEPENGERSY_KNA"),
    UFRT("UFRT_KNA"),
    UTLAND("UTLAND_KNA"),
    OKSOS("OKSOS_KNA"),
    ANSOS("ANSOS_KNA");

    public static final List<Temagruppe> SAMTALEREFERAT = asList(ARBD, HELSE, FMLI, HJLPM, PENS, OVRG, OKSOS, ANSOS);
    public static final List<Temagruppe> LEGG_TILBAKE = asList(ARBD, HELSE, FMLI, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OKSOS, ANSOS);
    public static final List<Temagruppe> PLUKKBARE = asList(ARBD, HELSE, FMLI, FDAG, HJLPM, BIL, ORT_HJE, PENS, PLEIEPENGERSY, UFRT, UTLAND, OVRG);
    public static final List<Temagruppe> KOMMUNALE_TJENESTER = asList(OKSOS, ANSOS);

    public final String underkategori;

    Temagruppe(String underkategori) {
        this.underkategori = underkategori;
    }
}

