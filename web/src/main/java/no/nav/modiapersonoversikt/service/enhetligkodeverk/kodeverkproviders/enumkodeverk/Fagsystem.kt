package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.enumkodeverk

enum class Fagsystem(private val systemnavn: String) : EnumKodeverk.WithValue<String> {
    AO01("Arena"),
    AO11("Grisen"),
    FS22("Gosys"),
    IT01("Infotrygd"),
    OEBS("OeBS"),
    PP01("Pesys"),
    V2("V2"),
    UFM("Unntak fra medlemskap"),
    FS36("Vedtaksløsning Foreldrepenger"),
    BISYS("Kopiert inn i Bisys"),
    K9("Sykdom i familien"),
    FS38("Melosys"),
    OB36("UR"),
    BA("Barnetrygd"),
    EF("Enslig forsørger)"),
    HJELPEMIDLER("Hjelpemidler"),
    OMSORGSPENGER("Omsorgspenger"),
    SUPSTONAD("Supplerende Stønad"),
    KONT("Kontantstøtte"),
    ;

    override fun getValue(): String = this.systemnavn
}
