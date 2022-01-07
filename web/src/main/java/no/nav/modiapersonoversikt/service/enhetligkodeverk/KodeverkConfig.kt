package no.nav.modiapersonoversikt.service.enhetligkodeverk

enum class KodeverkConfig(private val kilde: EnhetligKodeverk.Kilde) {
    ARKIVTEMA(FellesKodeverkKilde("Arkivtemaer")),
    BEHANDLINGSTEMA(FellesKodeverkKilde("Behandlingstema")),
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
    SF_TEMAGRUPPER(SfHenvendelseKodeverkKilde()),
    NAVSKJEMAOGVEDLEGGSKODER(
        FellesKodeverkFletter(
            "NAVSkjemaOgVedleggskoder",
            listOf(
                FellesKodeverkKilde("NAVSkjema"),
                FellesKodeverkKilde("Vedleggskoder")
            )
        )
    );

    fun hentKodeverk(providers: KodeverkProviders) = kilde.hentKodeverk(providers)
}

class FellesKodeverkFletter(val nyttKodeverkNavn: String, val kodeverkKilder: List<FellesKodeverkKilde>) :
    EnhetligKodeverk.Kilde {
    override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk {
        val kodeverk = kodeverkKilder
            .map { providers.fraFellesKodeverk(it.kodeverkNavn).kodeverk }
            .reduce { a, b -> a + b }

        return EnhetligKodeverk.Kodeverk(nyttKodeverkNavn, kodeverk)
    }
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
