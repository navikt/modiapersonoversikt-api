package no.nav.modiapersonoversikt.service.unleash.strategier

import io.getunleash.UnleashContext
import io.getunleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.getApplicationEnvironment
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.splitIntoSet

class ByEnvironmentStrategy : Strategy {
    override fun getName(): String = "byEnvironment"

    override fun isEnabled(
        parameters: Map<String, String>,
        context: UnleashContext,
    ): Boolean {
        val strategiMiljo = parameters[ENABLED_ENVIRONMENT_PROPERTY].splitIntoSet()
        return strategiMiljo.contains(getApplicationEnvironment())
    }

    companion object {
        const val ENABLED_ENVIRONMENT_PROPERTY = "miljø"
    }
}
