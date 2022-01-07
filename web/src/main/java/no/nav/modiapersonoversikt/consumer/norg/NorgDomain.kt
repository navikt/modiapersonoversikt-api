package no.nav.modiapersonoversikt.consumer.norg

object NorgDomain {
    enum class EnhetStatus {
        UNDER_ETABLERING, AKTIV, UNDER_AVVIKLING, NEDLAGT
    }
    enum class OppgaveBehandlerFilter {
        UFILTRERT, KUN_OPPGAVEBEHANDLERE, INGEN_OPPGAVEBEHANDLERE
    }
    enum class DiskresjonsKode {
        SPFO, SPSF, ANY
    }

    data class EnhetGeografiskTilknyttning(
        val alternativEnhetId: String? = null,
        val enhetId: String?,
        val geografiskOmraade: String?,
        val navKontorId: String? = null,
    )

    data class Enhet(
        val enhetId: String?,
        val enhetNavn: String?,
        val status: EnhetStatus?
    )

    data class EnhetKontaktinformasjon(
        val enhetId: String,
        val enhetNavn: String,
        val publikumsmottak: List<Publikumsmottak>
    )
    data class Publikumsmottak(
        val besoksadresse: Gateadresse?,
        val apningstider: List<Apningstid>
    )

    data class Gateadresse(
        val gatenavn: String? = null,
        val husnummer: String? = null,
        val husbokstav: String? = null,
        val postnummer: String? = null,
        val poststed: String? = null,
    )

    enum class Ukedag {
        MANDAG, TIRSDAG, ONSDAG, TORSDAG, FREDAG, LORDAG, SONDAG
    }

    class Apningstid(
        val ukedag: Ukedag,
        val apentFra: String?,
        val apentTil: String?
    )
}
