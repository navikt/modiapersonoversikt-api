package no.nav.modiapersonoversikt.utils

import io.mockk.every
import io.mockk.mockk
import no.finn.unleash.DefaultUnleash
import no.finn.unleash.util.UnleashConfig
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
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
        val ansattService = createAnsattServiceMock()
        val unleashService = createUnleashService(ansattService)
        val (instance1, instance2) = createInstances()

        val proxy = UnleashProxySwitcher.createSwitcher(
            featureToggle = Feature.SAMPLE_FEATURE,
            unleashService = unleashService,
            ifEnabled = instance1,
            ifDisabled = instance2
        )

        assertDoesNotThrow {
            proxy.equals("anything")
        }
    }

    private fun createAnsattServiceMock(): AnsattService {
        val ansattService = mockk<AnsattService>()
        every { ansattService.hentEnhetsliste() } returns emptyList()
        return ansattService
    }

    private fun createUnleashService(ansattService: AnsattService): UnleashServiceImpl {
        val unleashContextProvider = UnleashContextProviderImpl(ansattService)
        val unleashConfig = UnleashConfig.builder()
            .appName("modiabrukerdialog")
            .instanceId(System.getProperty("APP_ENVIRONMENT_NAME", "local"))
            .unleashAPI("http://dummy.io")
            .unleashContextProvider(unleashContextProvider)
            .build()

        val unleash = DefaultUnleash(unleashConfig)
        val unleashService = UnleashServiceImpl(null, unleash, null)
        return unleashService
    }

    private fun createInstances(): Pair<Dummy, Dummy> {
        return Pair(
            Dummy { "" },
            Dummy { "" },
        )
    }
}
