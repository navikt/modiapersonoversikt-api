package no.nav.modiapersonoversikt.service.enhetligkodeverk

object EnhetligKodeverk {
    interface Service {
        fun hentKodeverk(kodeverkNavn: KodeverkConfig): Kodeverk
    }

    class Kodeverk(private val kodeverk: Map<String, String>) {

        fun hentBeskrivelse(kodeRef: String): String? = kodeverk[kodeRef]
    }

    interface Kilde {
        fun hentKodeverk(providers: KodeverkProviders): Kodeverk
    }
}
