package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.RolleListe
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.BrukersSkjermingPip
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

object TilgangTilBrukerMedSkjermingPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilBrukerMedSkjermingPolicy)
    private val skjermingRoller = RolleListe("0000-ga-egne_ansatte")

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        if (skjermingRoller.hasIntersection(veilederRoller)) {
            return Decision.Permit()
        }
        val erSkjermet = ctx.getValue(BrukersSkjermingPip)

        return if (erSkjermet) {
            Decision.Deny("Veileder har ikke tilgang til skjermet person", DenyCauseCode.FP3_EGEN_ANSATT)
        } else {
            Decision.NotApplicable()
        }
    }
}
