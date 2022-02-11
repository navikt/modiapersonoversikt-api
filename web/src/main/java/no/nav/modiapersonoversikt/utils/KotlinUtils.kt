package no.nav.modiapersonoversikt.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun String.isNumeric(): Boolean = toIntOrNull()?.let { true } ?: false

@OptIn(ExperimentalContracts::class)
public inline fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return !this.isNullOrEmpty()
}
