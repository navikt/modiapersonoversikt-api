package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersSkjermingPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

object TilgangTilBrukerMedSkjermingPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilBrukerMedSkjermingPolicy)
    private val skjermingRoller = setOf("0000-ga-gosys_utvidet", "0000-ga-pensjon_utvidet")

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        if (skjermingRoller.intersect(veilederRoller).isNotEmpty()) {
            return Decision.Permit()
        }
        val erSkjermet = ctx.getValue(BrukersSkjermingPip)

        return if (erSkjermet == true) {
            Decision.Deny("Veileder har ikke tilgang til skjermet person", DenyCauseCode.FP3_EGEN_ANSATT)
        } else {
            Decision.NotApplicable()
        }
    }
}
