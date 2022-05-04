package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.service.unleash.UnleashService

class UnleashTogglePip(private val unleashService: UnleashService) : Kabac.AttributeProvider<Boolean> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Boolean> {
        override val key = Key<Boolean>(UnleashTogglePip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Boolean {
        val featureToggle = ctx.requireValue(CommonAttributes.FEATURE_TOGGLE)
        return unleashService.isEnabled(featureToggle)
    }
}
