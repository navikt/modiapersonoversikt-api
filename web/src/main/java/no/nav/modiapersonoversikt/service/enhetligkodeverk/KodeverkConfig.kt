package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk

object KodeverkConfig {
    @JvmField
    val ARKIVTEMA = FellesKodeverkKilde("Arkivtemaer")
    val BEHANDLINGSTEMA = FellesKodeverkKilde("Behandlingstema")
    val KJONN = FellesKodeverkKilde("Kjønnstyper")
    val LAND = FellesKodeverkKilde("Landkoder")
    val SPRAK = FellesKodeverkKilde("Språk")
    val RETNINGSNUMRE = FellesKodeverkKilde("Retningsnumre")
    val POSTNUMMER = FellesKodeverkKilde("Postnummer")
    val PERSONSTATUSER = FellesKodeverkKilde("Personstatuser")
    val SIVILSTAND = FellesKodeverkKilde("Sivilstander")
    val DISKRESJONSKODER = FellesKodeverkKilde("Diskresjonskoder")
    val VALUTA = FellesKodeverkKilde("Valutaer")
    val TEMA = FellesKodeverkKilde("Tema")
    val SF_TEMAGRUPPER = SfHenvendelseKodeverkKilde()
    val OPPGAVE = OppgaveKodeverkKilde()

    fun values(): List<EnhetligKodeverk.Kilde<*, *>> {
        return listOf(
            ARKIVTEMA,
            BEHANDLINGSTEMA,
            KJONN,
            LAND,
            SPRAK,
            RETNINGSNUMRE,
            POSTNUMMER,
            PERSONSTATUSER,
            SIVILSTAND,
            DISKRESJONSKODER,
            VALUTA,
            TEMA,
            SF_TEMAGRUPPER,
            OPPGAVE
        )
    }
}

class FellesKodeverkKilde(override val navn: String) : EnhetligKodeverk.Kilde<String, String> {
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, String> {
        return providers.fellesKodeverk.hentKodeverk(navn)
    }
}

class SfHenvendelseKodeverkKilde : EnhetligKodeverk.Kilde<String, String> {
    override val navn = "SF_TEMAGRUPPER"
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, String> {
        return providers.sfHenvendelseKodeverk.hentKodeverk(navn)
    }
}

class OppgaveKodeverkKilde : EnhetligKodeverk.Kilde<String, OppgaveKodeverk.Tema> {
    override val navn = "OPPGAVE"
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, OppgaveKodeverk.Tema> {
        return providers.oppgaveKodeverk.hentKodeverk(navn)
    }
}
