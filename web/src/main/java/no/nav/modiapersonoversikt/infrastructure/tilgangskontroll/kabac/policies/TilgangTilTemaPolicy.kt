package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersTemaPip

internal object TilgangTilTemaPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilTemaPolicy)

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val tema = requireNotNull(ctx.getValue(CommonAttributes.TEMA)) {
            "Kan ikke evaluere tilgang til tema uten at tema er gitt"
        }
        val veilederTema = ctx.getValue(VeiledersTemaPip) ?: emptySet()

        return if (veilederTema.contains(tema)) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Veileder har ikke tilgang til $tema")
        }
    }
}
