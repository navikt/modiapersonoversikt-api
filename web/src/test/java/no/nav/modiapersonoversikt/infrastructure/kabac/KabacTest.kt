package no.nav.modiapersonoversikt.infrastructure.kabac

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.kabac.providers.AktorIdPip
import no.nav.modiapersonoversikt.infrastructure.kabac.providers.AttributeProvider
import no.nav.modiapersonoversikt.infrastructure.kabac.providers.AuthContextHolderPip
import no.nav.modiapersonoversikt.infrastructure.kabac.providers.NavIdentPip
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService

fun main() {
    val pdl = mockk<PdlOppslagService>()
    every { pdl.hentAktorId(any()) } throws IllegalStateException("Feil mot PDL")

    val kabac = Kabac()
    with(kabac) {
        install(AuthContextHolderPip)
        install(NavIdentPip)
        install(AktorIdPip(pdl))
    }

    val erIkkeProd = Kabac.Policy { ctx ->
        val env = ctx.requireValue(CommonAttributes.ENV)
        if (env == "p") {
            Kabac.Decision.Deny("Kan ikke brukes i prod")
        } else {
            Kabac.Decision.Permit()
        }
    }

    val harAktorIdPolicy = Kabac.Policy {ctx ->
        val aktorId = ctx.getValue(AktorIdPip)
        if (aktorId == null) {
            Kabac.Decision.Deny("Fant ikke aktør id")
        } else {
            Kabac.Decision.Permit()
        }
    }

    val combinedPolicy = kabac.evaluate(
        combining = CombiningAlgorithm.denyOverride,
        policies = listOf(harAktorIdPolicy, erIkkeProd),
        attributes = listOf(
            AttributeProvider(NavIdentPip, NavIdent("short-circuit")),
            AttributeProvider(CommonAttributes.FNR, "1231"),
            AttributeProvider(CommonAttributes.ENV, "q1")
        )
    )

    println(combinedPolicy)
}
