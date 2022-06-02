package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService

class VeiledersTemaPip(private val ansattService: AnsattService) : Kabac.PolicyInformationPoint<Set<String>> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Set<String>> {
        override val key = Key<Set<String>>(VeiledersTemaPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Set<String> {
        return ansattService.hentAnsattFagomrader(
            ident = ctx.getValue(NavIdentPip).get(),
            enhet = ctx.getValue(CommonAttributes.ENHET).get()
        )
    }
}
