package no.nav.modiapersonoversikt.service.unleash.strategier

import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENVIRONMENT_PROPERTY
import no.nav.personoversikt.common.test.testenvironment.TestEnvironment.Companion.withEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ByEnvironmentStrategyTest {
    val byEnvironmentStrategy = ByEnvironmentStrategy()

    @ParameterizedTest
    @MethodSource("testcases")
    internal fun `should evaluate strategy`(
        unleashConfig: String?,
        simulatedEnv: String?,
        expected: Boolean,
    ) {
        withEnvironment(mapOf(ENVIRONMENT_PROPERTY to simulatedEnv)) {
            val parameters = mapOf(ByEnvironmentStrategy.ENABLED_ENVIRONMENT_PROPERTY to unleashConfig)

            assertThat(byEnvironmentStrategy.isEnabled(parameters)).isEqualTo(expected)
        }
    }

    companion object {
        @JvmStatic
        fun testcases() =
            listOf(
                strategyEnabled(simulatedEnv = "t6", unleashConfig = "t6"),
                strategyEnabled(simulatedEnv = "q1", unleashConfig = "q6,q1,t6"),
                strategyDisabled(simulatedEnv = "p", unleashConfig = "q0"),
                strategyDisabled(simulatedEnv = null, unleashConfig = "q0"),
                strategyDisabled(simulatedEnv = null, unleashConfig = ""),
                strategyDisabled(simulatedEnv = "q0", unleashConfig = null),
                strategyDisabled(simulatedEnv = "", unleashConfig = null),
                strategyDisabled(simulatedEnv = "", unleashConfig = ""),
                strategyDisabled(simulatedEnv = "", unleashConfig = ",,,"),
            )

        private fun strategyEnabled(
            unleashConfig: String?,
            simulatedEnv: String?,
        ): Arguments {
            return Arguments.of(unleashConfig, simulatedEnv, true)
        }

        private fun strategyDisabled(
            unleashConfig: String?,
            simulatedEnv: String?,
        ): Arguments {
            return Arguments.of(unleashConfig, simulatedEnv, false)
        }
    }
}
