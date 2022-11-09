package no.nav.modiapersonoversikt.service.unleash.strategier

import no.finn.unleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.getApplicationEnvironment
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.splitIntoSet

class ByEnvironmentStrategy : Strategy {
    override fun getName(): String = "byEnvironment"

    override fun isEnabled(parameters: Map<String, String>?): Boolean {
        val strategiMiljo = parameters?.get("miljø").splitIntoSet()
        return strategiMiljo.contains(getApplicationEnvironment())
    }
}
