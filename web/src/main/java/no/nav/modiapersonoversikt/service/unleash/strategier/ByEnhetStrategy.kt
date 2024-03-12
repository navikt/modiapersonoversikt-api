package no.nav.modiapersonoversikt.service.unleash.strategier

import io.getunleash.UnleashContext
import io.getunleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENHETER_PROPERTY
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.splitIntoSet

class ByEnhetStrategy : Strategy {
    override fun getName(): String = "byEnhet"

    override fun isEnabled(parameters: Map<String, String>): Boolean {
        // Missing context, cannot verify "enhet"
        return false
    }

    override fun isEnabled(
        parameters: Map<String, String?>?,
        unleashContext: UnleashContext,
    ): Boolean {
        val strategiEnheter = parameters?.get(ENABLED_ENHETER_PROPERTY).splitIntoSet()
        val ansattesEnheter = unleashContext.properties[ENHETER_PROPERTY].splitIntoSet()

        return strategiEnheter.intersect(ansattesEnheter).isNotEmpty()
    }

    companion object {
        const val ENABLED_ENHETER_PROPERTY = "valgtEnhet"
    }
}
