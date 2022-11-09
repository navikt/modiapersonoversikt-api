package no.nav.modiapersonoversikt.service.unleash.strategier

import no.finn.unleash.UnleashContext
import no.finn.unleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENHETER_PROPERTY
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.splitIntoSet

class ByEnhetStrategy : Strategy {
    override fun getName(): String = "byEnhet"

    override fun isEnabled(parameters: Map<String, String>): Boolean {
        // Missing context, cannot verify "enhet"
        return false
    }

    override fun isEnabled(parameters: Map<String, String>?, unleashContext: UnleashContext): Boolean {
        val strategiEnheter = parameters?.get("valgtEnhet").splitIntoSet()
        val ansattesEnheter = unleashContext.properties[ENHETER_PROPERTY].splitIntoSet()

        return strategiEnheter.intersect(ansattesEnheter).isNotEmpty()
    }
}
