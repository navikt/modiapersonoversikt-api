package no.nav.modiapersonoversikt.service.unleash.strategier

import no.finn.unleash.strategy.Strategy
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.getApplicationEnvironment

class IsNotProdStrategy : Strategy {
    override fun getName(): String = "isNotProd"

    override fun isEnabled(parameters: Map<String, String>?): Boolean {
        return getApplicationEnvironment() != "p"
    }
}
