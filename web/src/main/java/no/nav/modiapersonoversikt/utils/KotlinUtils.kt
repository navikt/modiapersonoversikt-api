package no.nav.modiapersonoversikt.utils

import java.net.URL
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.reflect.KClass

fun String.isNumeric(): Boolean = toIntOrNull()?.let { true } ?: false

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

fun Any.readResource(name: String) = this::class.readResource(name)

fun KClass<*>.readResource(name: String): String = this.java.readResource(name)

fun Class<*>.readResource(name: String): String {
    val url: URL = this.getResource(name) ?: error("File not found: $name")
    return Path.of(url.toURI()).readText()
}
