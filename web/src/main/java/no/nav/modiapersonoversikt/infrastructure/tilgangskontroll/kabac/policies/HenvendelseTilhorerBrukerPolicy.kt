package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.HenvendelseEierPip

internal object HenvendelseTilhorerBrukerPolicy : Kabac.Policy {
    override val key = Key<Kabac.Policy>(HenvendelseTilhorerBrukerPolicy)
    override fun evaluate(ctx: EvaluationContext): Decision {
        val fnr = requireNotNull(ctx.getValue(CommonAttributes.FNR)) {
            "Kan ikke evaluere eierskap av henvendelse uten FNR"
        }
        val eier = ctx.getValue(HenvendelseEierPip)

        return if (fnr == eier) {
            Decision.Permit()
        } else {
            Decision.Deny("Bruker eier ikke henvendelsen")
        }
    }
}
