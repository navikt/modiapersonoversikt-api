package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes

class BrukersSkjermingPip(private val skjermedePersonerApi: SkjermedePersonerApi) : Kabac.PolicyInformationPoint<Boolean> {
    override val key = Companion.key

    companion object : Kabac.AttributeKey<Boolean> {
        override val key = Key<Boolean>(BrukersSkjermingPip::class.java.simpleName)
    }

    override fun provide(ctx: EvaluationContext): Boolean {
        val fnr = ctx.getValue(CommonAttributes.FNR)
        return skjermedePersonerApi.erSkjermetPerson(fnr)
    }
}
