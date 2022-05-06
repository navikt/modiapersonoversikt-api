package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersTemaPip

internal object TilgangTilTemaPolicy : Kabac.Policy {
    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val tema = ctx.requireValue(CommonAttributes.TEMA)
        val veilederTema = ctx.requireValue(VeiledersTemaPip)

        return if (veilederTema.contains(tema)) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Veileder har ikke tilgang til $tema")
        }
    }
}
