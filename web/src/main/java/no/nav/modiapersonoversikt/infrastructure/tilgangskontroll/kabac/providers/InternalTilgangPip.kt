package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.Key

object InternalTilgangPip : Kabac.AttributeProvider<List<NavIdent>> {
    private val identer: List<NavIdent> = EnvironmentUtils.getRequiredProperty("INTERNAL_TILGANG", "")
        .split(",")
        .map(String::trim)
        .map(String::uppercase)
        .map(::NavIdent)

    override val key = Key<List<NavIdent>>(InternalTilgangPip)
    override fun provide(ctx: EvaluationContext): List<NavIdent> {
        return identer
    }
}
