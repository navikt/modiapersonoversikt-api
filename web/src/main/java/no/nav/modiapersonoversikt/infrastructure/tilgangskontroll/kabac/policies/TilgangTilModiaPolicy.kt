package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersRollerPip

internal object TilgangTilModiaPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilModiaPolicy)
    private val modiaRoller =
        setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

    override fun evaluate(ctx: EvaluationContext): Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip)

        return if (modiaRoller.intersect(veilederRoller).isNotEmpty()) {
            Decision.Permit()
        } else {
            Decision.Deny("Veileder har ikke tilgang til modia", DenyCauseCode.AD_ROLLE)
        }
    }
}
