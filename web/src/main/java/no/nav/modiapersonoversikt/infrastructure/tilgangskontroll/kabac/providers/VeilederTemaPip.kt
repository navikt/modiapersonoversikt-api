package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService

class VeilederTemaPip(private val ansattService: AnsattService) : Kabac.AttributeProvider<Set<String>> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Set<String>> {
        override val key = Key<Set<String>>(VeilederTemaPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Set<String> {
        return ansattService.hentAnsattFagomrader(
            ident = ctx.requireValue(NavIdentPip).get(),
            enhet = ctx.requireValue(CommonAttributes.ENHET).get()
        )
    }
}
