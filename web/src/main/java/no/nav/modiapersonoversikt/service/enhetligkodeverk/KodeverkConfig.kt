package no.nav.modiapersonoversikt.service.enhetligkodeverk

enum class KodeverkConfig(private val kilde: EnhetligKodeverk.Kilde) {
    KJONN(FellesKodeverkKilde("Kjønnstyper")),
    LAND(FellesKodeverkKilde("Landkoder")),
    SPRAK(FellesKodeverkKilde("Språk")),
    RETNINGSNUMRE(FellesKodeverkKilde("Retningsnumre")),
    POSTNUMMER(FellesKodeverkKilde("Postnummer")),
    PERSONSTATUSER(FellesKodeverkKilde("Personstatuser")),
    SIVILSTAND(FellesKodeverkKilde("Sivilstander")),
    DISKRESJONSKODER(FellesKodeverkKilde("Diskresjonskoder")),
    VALUTA(FellesKodeverkKilde("Valutaer")),
    TEMA(FellesKodeverkKilde("Tema")),
    SF_TEMAGRUPPER(SfHenvendelseKodeverkKilde());

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
