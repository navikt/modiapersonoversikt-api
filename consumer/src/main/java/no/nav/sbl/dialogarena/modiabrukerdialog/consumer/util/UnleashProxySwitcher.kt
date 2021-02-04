package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

object UnleashProxySwitcher {
    val log = LoggerFactory.getLogger(UnleashProxySwitcher::class.java)

    inline fun <reified T : Any> createSwitcher(
        featureToggle: Feature,
        unleashService: UnleashService,
        ftDisabledImpl: T,
        ftEnabledImpl: T
    ): T {
        val name = T::class.java.simpleName
        val invocationHandler = InvocationHandler { _, method, args ->
            val nullsafeArgs = args ?: arrayOfNulls<Any>(0)
            if (unleashService.isEnabled(featureToggle)) {
                method.invoke(ftEnabledImpl, *nullsafeArgs)
                log.warn("[UnleashProxySwitcher] $name is enabled")
            } else {
                method.invoke(ftDisabledImpl, *nullsafeArgs)
            }
        }
        val proxy = Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            invocationHandler
        )

        return proxy as T
    }
}
