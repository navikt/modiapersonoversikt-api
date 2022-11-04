package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.DenyCauseCode
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.HenvendelseEierPip
import no.nav.personoversikt.common.kabac.Decision
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

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
            Decision.Deny("Bruker eier ikke henvendelsen", DenyCauseCode.UNKNOWN)
        }
    }
}
