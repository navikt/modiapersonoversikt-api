package no.nav.modiapersonoversikt.service.unleash.strategier

import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENVIRONMENT_PROPERTY
import no.nav.personoversikt.common.test.testenvironment.TestEnvironment
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class IsNotProdStrategyTest {
    val isNotProdStrategy = IsNotProdStrategy()

    @ParameterizedTest
    @MethodSource("testcases")
    internal fun `should evaluate strategy`(
        simulatedEnv: String?,
        expected: Boolean,
    ) {
        TestEnvironment.withEnvironment(mapOf(ENVIRONMENT_PROPERTY to simulatedEnv)) {
            assertThat(isNotProdStrategy.isEnabled(null)).isEqualTo(expected)
        }
    }

    companion object {
        @JvmStatic
        fun testcases() =
            listOf(
                strategyEnabled(simulatedEnv = "t1"),
                strategyEnabled(simulatedEnv = "q0"),
                strategyEnabled(simulatedEnv = "u10"),
                strategyEnabled(simulatedEnv = "local"),
                strategyEnabled(simulatedEnv = ""),
                strategyEnabled(simulatedEnv = null),
                strategyDisabled(simulatedEnv = "p"),
            )

        private fun strategyEnabled(simulatedEnv: String?): Arguments = Arguments.of(simulatedEnv, true)

        private fun strategyDisabled(simulatedEnv: String?): Arguments = Arguments.of(simulatedEnv, false)
    }
}
