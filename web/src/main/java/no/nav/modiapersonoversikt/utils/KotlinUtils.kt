package no.nav.modiapersonoversikt.utils

fun String.isNumeric(): Boolean = toIntOrNull()?.let { true } ?: false

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

infix fun Int.inRange(range: Pair<Int, Int>): Boolean = this >= range.first && this < range.second
