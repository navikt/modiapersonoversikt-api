package no.nav.modiapersonoversikt.utils

import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.KClass

@PublishedApi
internal class UnleashHandler<T : Any>(
    val type: KClass<T>,
    val feature: Feature,
    val unleashService: no.nav.modiapersonoversikt.service.unleash.UnleashService,
    val ifEnabled: T,
    val ifDisabled: T
) : InvocationHandler {
    val log: Logger = LoggerFactory.getLogger(UnleashHandler::class.java)
    val name: String = type.java.simpleName

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val nullsafeArgs = args ?: arrayOfNulls<Any>(0)
        return try {
            if (unleashService.isEnabled(feature)) {
                log.warn("[UnleashProxySwitcher] $name is enabled")
                method.invoke(ifEnabled, *nullsafeArgs)
            } else {
                method.invoke(ifDisabled, *nullsafeArgs)
            }
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }
}

object UnleashProxySwitcher {
    inline fun <reified T : Any> createSwitcher(
        featureToggle: Feature,
        unleashService: no.nav.modiapersonoversikt.service.unleash.UnleashService,
        ifEnabled: T,
        ifDisabled: T
    ): T {
        val invocationHandler = UnleashHandler(
            type = T::class,
            feature = featureToggle,
            unleashService = unleashService,
            ifEnabled = ifEnabled,
            ifDisabled = ifDisabled
        )

        val proxy = Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java),
            invocationHandler
        )

        return proxy as T
    }
}
