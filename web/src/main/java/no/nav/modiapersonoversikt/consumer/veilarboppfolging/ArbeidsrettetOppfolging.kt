package no.nav.modiapersonoversikt.consumer.veilarboppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler
import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet

object ArbeidsrettetOppfolging {
    interface Service {
        fun hentOppfolgingsinfo(ident: Fnr): Info
        fun ping()
    }

    data class Status(
        val underOppfolging: Boolean
    )

    data class Info(
        @JvmField val erUnderOppfolging: Boolean,
        val veileder: Saksbehandler?,
        val oppfolgingsenhet: AnsattEnhet?
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
