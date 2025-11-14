package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.enumkodeverk.EnumKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.enumkodeverk.Fagsystem
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk

abstract class ObjectEnum<T> {
    private val values = mutableListOf<T>()

    fun values(): List<T> = values

    protected fun <V : T> add(value: V): V {
        values.add(value)
        return value
    }
}

object KodeverkConfig : ObjectEnum<EnhetligKodeverk.Kilde<*, *>>() {
    @JvmField
    val ARKIVTEMA = add(FellesKodeverkKilde("Arkivtemaer"))
    val BEHANDLINGSTEMA = add(FellesKodeverkKilde("Behandlingstema"))
    val KJONN = add(FellesKodeverkKilde("Kjønnstyper"))
    val LAND = add(FellesKodeverkKilde("Landkoder"))
    val SPRAK = add(FellesKodeverkKilde("Språk"))
    val RETNINGSNUMRE = add(FellesKodeverkKilde("Retningsnumre"))
    val POSTNUMMER = add(FellesKodeverkKilde("Postnummer"))
    val PERSONSTATUSER = add(FellesKodeverkKilde("Personstatuser"))
    val SIVILSTAND = add(FellesKodeverkKilde("Sivilstander"))
    val DISKRESJONSKODER = add(FellesKodeverkKilde("Diskresjonskoder"))
    val VALUTA = add(FellesKodeverkKilde("Valutaer"))
    val TEMA = add(FellesKodeverkKilde("Tema"))
    val SF_TEMAGRUPPER = add(SfHenvendelseKodeverkKilde())
    val OPPGAVE = add(OppgaveKodeverkKilde())
    val FAGSYSTEM = add(EnumKodeverk.Kilde(Fagsystem::class))
}

class FellesKodeverkKilde(
    override val navn: String,
) : EnhetligKodeverk.Kilde<String, String> {
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, String> =
        providers.fellesKodeverk.hentKodeverk(navn)
}

class SfHenvendelseKodeverkKilde : EnhetligKodeverk.Kilde<String, String> {
    override val navn = "SF_TEMAGRUPPER"

    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, String> =
        providers.sfHenvendelseKodeverk.hentKodeverk(navn)
}

class OppgaveKodeverkKilde : EnhetligKodeverk.Kilde<String, OppgaveKodeverk.Tema> {
    override val navn = "OPPGAVE"

    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, OppgaveKodeverk.Tema> =
        providers.oppgaveKodeverk.hentKodeverk(navn)
}
