package no.nav.modiapersonoversikt.infrastructure.kabac

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import org.junit.jupiter.api.Assertions.*

fun main() {
    val kabac = Kabac()
    val pdl = mockk<PdlOppslagService>()
    every { pdl.hentAktorId(any()) } returns "000aktorid000"

    with(kabac) {
        install(AuthContextHolderPip)
        install(NavIdentPip)
        install(AktorIdPip(pdl))
    }

    val erIkkeProd = Kabac.Policy {
        val env = requireValue(CommonAttributes.ENV)
        if (env == "p") {
            Kabac.Decision.Deny("Kan ikke brukes i prod")
        } else {
            Kabac.Decision.Permit()
        }
    }

    val harAktorIdPolicy = Kabac.Policy {
        val aktorId = getValue(AktorIdPip)
        if (aktorId == null) {
            Kabac.Decision.Deny("Fant ikke aktør id")
        } else {
            Kabac.Decision.Permit()
        }
    }

    val combinedPolicy = kabac.evaluate(
        combining = CombiningAlgorithm.denyOverride,
        policies = listOf(harAktorIdPolicy, erIkkeProd),
        evaluationProviders = listOf(
            AttributeProvider(CommonAttributes.FNR, "1231"),
            AttributeProvider(CommonAttributes.ENV, "q1")
        )
    )

    println(combinedPolicy)
}
