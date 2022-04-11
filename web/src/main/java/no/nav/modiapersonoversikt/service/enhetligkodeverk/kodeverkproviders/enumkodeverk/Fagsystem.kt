package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.enumkodeverk

enum class Fagsystem(name: String) : EnumKodeverk.WithValue<String> {
    AO01("Arena"),
    AO11("Grisen"),
    FS22("Gosys"),
    IT01("Infotrygd"),
    OEBS("OeBS"),
    PP01("Pesys"),
    V2("V2"),
    UFM("Unntak"),
    FS36("Vedtaksløsning Foreldrepenger"),
    BISYS("Kopiert inn i Bisys");

    override fun getValue(): String = this.name
}
