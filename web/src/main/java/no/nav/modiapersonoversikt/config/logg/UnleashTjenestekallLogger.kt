package no.nav.modiapersonoversikt.config.logg

import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.personoversikt.common.logging.TjenestekallLogger
import org.slf4j.Marker

class UnleashTjenestekallLogger(
    private val delegate: TjenestekallLogger,
    private val unleashService: UnleashService,
) : TjenestekallLogger {
    override fun info(
        header: String,
        fields: Map<String, Any?>,
        tags: Map<String, Any?>,
        throwable: Throwable?,
    ) = logIfEnabled { delegate.info(header, fields, tags, throwable) }

    override fun warn(
        header: String,
        fields: Map<String, Any?>,
        tags: Map<String, Any?>,
        throwable: Throwable?,
    ) = delegate.warn(header, fields, tags, throwable)

    override fun error(
        header: String,
        fields: Map<String, Any?>,
        tags: Map<String, Any?>,
        throwable: Throwable?,
    ) = delegate.error(header, fields, tags, throwable)

    override fun raw(
        level: TjenestekallLogger.Level,
        message: String,
        markers: Marker?,
        throwable: Throwable?,
    ) = delegate.raw(level, message, markers, throwable)

    private inline fun logIfEnabled(block: TjenestekallLogger.() -> Unit) {
        if (unleashService.isEnabled(Feature.TJENESTEKALL_LOGGING)) {
            delegate.block()
        }
    }
}
