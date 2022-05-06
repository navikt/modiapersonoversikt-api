package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

internal object TilgangTilModiaPolicy : Kabac.Policy {
    private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()

        return if (modiaRoller.intersect(veilederRoller).isNotEmpty()) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Veileder har ikke tilgang til modia")
        }
    }
}
