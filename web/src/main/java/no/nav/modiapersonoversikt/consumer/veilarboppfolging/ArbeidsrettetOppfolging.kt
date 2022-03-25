package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler

object ArbeidsrettetOppfolging {
    interface Service {
        fun hentOppfolgingsinfo(fodselsnummer: Fnr): Info
        fun ping()
    }

    data class Status(
        val underOppfolging: Boolean
    )

    data class Info(
        @JvmField val erUnderOppfolging: Boolean,
        val veileder: Saksbehandler?,
        val oppfolgingsenhet: Enhet?
    )

    data class EnhetOgVeileder(
        val oppfolgingsenhet: Enhet?,
        val veilederId: String?
    )

    data class Enhet(
        val enhetId: String,
        val navn: String
    )
}
