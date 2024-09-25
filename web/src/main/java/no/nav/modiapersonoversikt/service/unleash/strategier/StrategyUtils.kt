package no.nav.modiapersonoversikt.service.unleash.strategier

import no.nav.personoversikt.common.utils.EnvUtils

object StrategyUtils {
    const val ENHETER_PROPERTY = "enheter"
    const val ENVIRONMENT_PROPERTY = "APP_ENVIRONMENT_NAME"

    fun String?.splitIntoSet(): Set<String> =
        this
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map { it.trim() }
            ?.toSet()
            ?: emptySet()

    fun getApplicationEnvironment(): String = EnvUtils.getConfig(ENVIRONMENT_PROPERTY) ?: "local"
}
