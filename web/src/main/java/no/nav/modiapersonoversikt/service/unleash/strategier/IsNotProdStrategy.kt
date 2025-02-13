package no.nav.modiapersonoversikt.service.unleash.strategier

import io.getunleash.UnleashContext
import io.getunleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.getApplicationEnvironment

class IsNotProdStrategy : Strategy {
    override fun getName(): String = "isNotProd"

    override fun isEnabled(
        parameters: Map<String, String>,
        context: UnleashContext,
    ): Boolean = getApplicationEnvironment() != "p"
}
