package no.nav.modiapersonoversikt.utils

fun String.isNumeric(): Boolean = toIntOrNull()?.let { true } ?: false
