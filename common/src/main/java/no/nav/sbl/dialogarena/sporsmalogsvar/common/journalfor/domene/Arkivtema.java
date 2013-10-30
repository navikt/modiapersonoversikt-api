package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene;

public enum Arkivtema {

    AAP("Arbeidsavklaringspenger"),
    AGR("Ajourhold - Grunnopplysninger"),
    BAR("Barnetrygd"),
    BID("Bidrag"),
    BIL("Bil"),
    DAG("Dagpenger"),
    ENF("Enslig forsørger"),
    FOR("Foreldre- og svangerskapspenger"),
    FOS("Forsikring"),
    FUL("Fullmakt"),
    GRA("Gravferdsstønad"),
    GRU("Grunn- og hjelpestønad"),
    HEL("Helsetjenester og ort. Hjelpemidler"),
    HJE("Hjelpemidler"),
    IND("Individstønad"),
    KON("Kontantstøtte"),
    MED("Medlemskap"),
    MOB("Mobilitetsfremmende stønad"),
    OMS("Omsorgspenger, Pleiepenger og opplæringspenger"),
    OPP("Oppfølging"),
    PEN("Pensjon"),
    SER("Serviceklager"),
    SYK("Sykepenger"),
    SYM("Sykmelding"),
    TRK("Trekkhåndtering"),
    UFO("Uførepensjon"),
    VEN("Ventelønn"),
    YRK("Yrkesskade / Menerstatning");

    public final String navn;

    Arkivtema(String navn) {
        this.navn = navn;
    }

    @Override
    public String toString() {
        return navn;
    }
}
