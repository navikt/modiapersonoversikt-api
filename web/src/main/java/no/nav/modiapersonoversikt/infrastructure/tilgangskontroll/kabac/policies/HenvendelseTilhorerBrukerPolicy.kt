package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.HenvendelseEierPip

internal object HenvendelseTilhorerBrukerPolicy : Kabac.Policy {
    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val fnr = ctx.requireValue(CommonAttributes.FNR)
        val eier = ctx.requireValue(HenvendelseEierPip)

        return if (fnr == eier) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Bruker eier ikke henvendelsen")
        }
    }
}
