package no.nav.modiapersonoversikt.consumer.norg

import no.nav.common.types.identer.EnhetId

object NorgDomain {
    enum class EnhetStatus {
        UNDER_ETABLERING, AKTIV, UNDER_AVVIKLING, NEDLAGT;
        companion object {
            fun safeValueOf(status: String?) = valueOf(safeEnumValue(status))
        }
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
        val enhetId: String,
        val enhetNavn: String,
        val status: EnhetStatus,
        val oppgavebehandler: Boolean
    )
    data class EnhetKontaktinformasjon(
        val enhet: Enhet,
        val publikumsmottak: List<Publikumsmottak>,
        val overordnetEnhet: EnhetId?
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
        MANDAG, TIRSDAG, ONSDAG, TORSDAG, FREDAG, LORDAG, SONDAG;
        companion object {
            fun safeValueOf(status: String?) = valueOf(safeEnumValue(status))
        }
    }

    class Apningstid(
        val ukedag: Ukedag,
        val stengt: Boolean,
        val apentFra: String?,
        val apentTil: String?
    )

    private fun safeEnumValue(value: String?) = requireNotNull(value).uppercase().replace(' ', '_')
}
