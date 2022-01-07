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
}
