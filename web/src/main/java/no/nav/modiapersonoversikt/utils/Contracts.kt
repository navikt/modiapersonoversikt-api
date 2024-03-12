package no.nav.modiapersonoversikt.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object Contracts {
    @ExperimentalContracts
    inline fun requireNotNullOrBlank(
        value: String?,
        lazyMessage: () -> String,
    ): String {
        contract {
            returns() implies (value != null)
        }
        return requireNotNullOrBlank(value, { IllegalArgumentException(it) }, lazyMessage)
    }

    @ExperimentalContracts
    inline fun requireNotNullOrBlank(
        value: String?,
        exception: (String) -> Exception = { IllegalArgumentException(it) },
        lazyMessage: () -> String,
    ): String {
        contract {
            returns() implies (value != null)
        }

        if (value.isNullOrBlank()) {
            val message = lazyMessage()
            throw exception(message)
        } else {
            return value
        }
    }
}
