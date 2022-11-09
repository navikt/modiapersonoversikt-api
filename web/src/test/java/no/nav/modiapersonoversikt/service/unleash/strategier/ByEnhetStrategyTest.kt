package no.nav.modiapersonoversikt.service.unleash.strategier

import io.mockk.every
import io.mockk.mockk
import no.finn.unleash.UnleashContext
import no.nav.modiapersonoversikt.service.unleash.strategier.StrategyUtils.ENHETER_PROPERTY
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class ByEnhetStrategyTest {
    val byEnhetStrategy = ByEnhetStrategy()
    val unleashContext = mockk<UnleashContext>()

    @Test
    internal fun `calls without context should return false`() {
        assertThat(byEnhetStrategy.isEnabled(emptyMap())).isFalse
    }

    @ParameterizedTest
    @MethodSource("testcases")
    internal fun `should evaluate strategy`(unleashConfig: String?, ansattesEnheter: String?, expected: Boolean) {
        val parameters = mapOf(ByEnhetStrategy.ENABLED_ENHETER_PROPERTY to unleashConfig)
        every { unleashContext.properties } returns mapOf(ENHETER_PROPERTY to ansattesEnheter)

        assertThat(byEnhetStrategy.isEnabled(parameters, unleashContext)).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun testcases() = listOf(
            strategyEnabled(unleashConfig = "0118", ansattesEnheter = "0118"),
            strategyEnabled(unleashConfig = "0118", ansattesEnheter = "1234,0118,0000"),
            strategyEnabled(unleashConfig = "0118,1234,1111", ansattesEnheter = "1234"),
            strategyEnabled(unleashConfig = "0118,1234,1111", ansattesEnheter = "0118,1234,1111"),
            strategyDisabled(unleashConfig = "0118", ansattesEnheter = "1234, 4455"),
            strategyDisabled(unleashConfig = "0118", ansattesEnheter = ""),
            strategyDisabled(unleashConfig = "0118", ansattesEnheter = null),
            strategyDisabled(unleashConfig = "", ansattesEnheter = "1234"),
            strategyDisabled(unleashConfig = null, ansattesEnheter = "1234"),
            strategyDisabled(unleashConfig = "", ansattesEnheter = ""),
            strategyDisabled(unleashConfig = null, ansattesEnheter = null),
            strategyDisabled(unleashConfig = "", ansattesEnheter = ",,,"),
            strategyDisabled(unleashConfig = ",,,", ansattesEnheter = ",,,"),
        )

        private fun strategyEnabled(unleashConfig: String?, ansattesEnheter: String?): Arguments {
            return Arguments.of(unleashConfig, ansattesEnheter, true)
        }
        private fun strategyDisabled(unleashConfig: String?, ansattesEnheter: String?): Arguments {
            return Arguments.of(unleashConfig, ansattesEnheter, false)
        }
    }
}
