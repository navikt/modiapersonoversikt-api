package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeiledersTemaPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

internal object TilgangTilTemaPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(TilgangTilTemaPolicy)

    override fun evaluate(ctx: EvaluationContext): Decision {
        val tema = requireNotNull(ctx.getValue(CommonAttributes.TEMA)) {
            "Kan ikke evaluere tilgang til tema uten at tema er gitt"
        }
        val veilederTema = ctx.getValue(VeiledersTemaPip)

        return if (veilederTema.contains(tema)) {
            Decision.Permit()
        } else {
            Decision.Deny("Veileder har ikke tilgang til $tema", DenyCauseCode.UNKNOWN)
        }
    }
}
