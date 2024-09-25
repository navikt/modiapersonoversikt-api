package no.nav.modiapersonoversikt.utils

import io.getunleash.DefaultUnleash
import io.getunleash.util.UnleashConfig
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashContextProviderImpl
import no.nav.modiapersonoversikt.service.unleash.UnleashServiceImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class UnleashProxySwitcherTest {
    fun interface Dummy {
        fun getData(): String
    }

    @Test
    internal fun name() {
        val unleashService = createUnleashService()
        val (instance1, instance2) = createInstances()

        val proxy =
            UnleashProxySwitcher.createSwitcher(
                featureToggle = Feature.SAMPLE_FEATURE,
                unleashService = unleashService,
                ifEnabled = instance1,
                ifDisabled = instance2,
            )

        assertDoesNotThrow {
            assertNotNull(proxy.equals("anything"))
        }
    }

    private fun createUnleashService(): UnleashServiceImpl {
        val unleashContextProvider = UnleashContextProviderImpl()
        val unleashConfig =
            UnleashConfig
                .builder()
                .appName("modiapersonoversikt-api")
                .instanceId(System.getProperty("APP_ENVIRONMENT_NAME", "local"))
                .unleashAPI("http://dummy.io")
                .unleashContextProvider(unleashContextProvider)
                .build()

        val unleash = DefaultUnleash(unleashConfig)
        return UnleashServiceImpl(null, unleash, null)
    }

    private fun createInstances(): Pair<Dummy, Dummy> =
        Pair(
            Dummy { "" },
            Dummy { "" },
        )
}
