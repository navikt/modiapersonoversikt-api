package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.fagsystem

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
object FagsystemKodeverkKilde : EnhetligKodeverk.Kilde<String, String> {
    private val kodeverk: EnhetligKodeverk.Kodeverk<String, String> = EnhetligKodeverk.Kodeverk(
        "fagsystem",
        mapOf(
            "AO01" to "Arena",
            "AO11" to "Grisen",
            "FS22" to "Gosys",
            "IT01" to "Infotrygd",
            "OEBS" to "OeBS",
            "PP01" to "Pesys",
            "V2" to "V2",
            "UFM" to "Unntak",
            "FS36" to "Vedtaksløsning Foreldrepenger"
        )
    )

    override val navn: String = "fagsystem"
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, String> {
        return kodeverk
    }
}
