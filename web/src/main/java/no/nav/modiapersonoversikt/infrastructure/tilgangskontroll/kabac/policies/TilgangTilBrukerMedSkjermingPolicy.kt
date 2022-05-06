package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersSkjermingPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedSkjermingPolicy : Kabac.Policy {
    private val skjermingRoller = setOf("0000-ga-gosys_utvidet", "0000-ga-pensjon_utvidet")

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()

        if (skjermingRoller.intersect(veilederRoller).isNotEmpty()) {
            return Kabac.Decision.Permit()
        }
        val erSkjermet = ctx.getValue(BrukersSkjermingPip)

        return if (erSkjermet == true) {
            Kabac.Decision.Deny("Veileder har ikke tilgang til skjermet person")
        } else {
            Kabac.Decision.NotApplicable()
        }
    }
}
