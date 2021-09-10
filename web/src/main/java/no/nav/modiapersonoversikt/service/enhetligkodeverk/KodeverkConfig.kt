package no.nav.modiapersonoversikt.service.enhetligkodeverk

enum class KodeverkConfig(private val kilde: EnhetligKodeverk.Kilde) {
    LAND(FellesKodeverkKilde("Landkoder")),
    SFTEMAGRUPPER(SfHenvendelseKodeverkKilde());

    fun hentKodeverk(providers: KodeverkProviders) = kilde.hentKodeverk(providers)
}

class FellesKodeverkKilde(val kodeverkNavn: String) : EnhetligKodeverk.Kilde {
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk {
        return providers.fraFellesKodeverk(kodeverkNavn)
    }
}

class SfHenvendelseKodeverkKilde() : EnhetligKodeverk.Kilde {
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk {
        return providers.fraSfHenvendelseKodeverk()
    }
}
