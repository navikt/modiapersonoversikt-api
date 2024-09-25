package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.personoversikt.common.kabac.Kabac
import no.nav.personoversikt.common.kabac.Kabac.EvaluationContext
import no.nav.personoversikt.common.kabac.utils.Key

class InternalTilgangPip : Kabac.PolicyInformationPoint<List<NavIdent>> {
    private val identer: List<NavIdent> =
        EnvironmentUtils
            .getRequiredProperty("INTERNAL_TILGANG", "")
            .split(",")
            .map(String::trim)
            .map(String::uppercase)
            .map(::NavIdent)

    override val key = Companion.key

    companion object : Kabac.AttributeKey<List<NavIdent>> {
        override val key = Key<List<NavIdent>>(InternalTilgangPip)
    }

    override fun provide(ctx: EvaluationContext): List<NavIdent> = identer
}
