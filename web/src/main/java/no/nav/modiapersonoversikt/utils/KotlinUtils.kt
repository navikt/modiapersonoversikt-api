package no.nav.modiapersonoversikt.utils

import java.net.URL
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.reflect.KClass

fun String.isNumeric(): Boolean = toIntOrNull()?.let { true } ?: false

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean = !this.isNullOrEmpty()

fun <T> Collection<Collection<T>>.merge(): Collection<T> {
    val list: MutableList<T> = mutableListOf()
    for (sublist in this) {
        list.addAll(sublist)
    }
    return list
}

infix fun Int.inRange(range: Pair<Int, Int>): Boolean = this >= range.first && this < range.second

infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean = { this(it) || other(it) }

fun Any.readResource(name: String) = this::class.readResource(name)
fun KClass<*>.readResource(name: String): String = this.java.readResource(name)
fun Class<*>.readResource(name: String): String {
    val url: URL = this.getResource(name) ?: error("File not found: $name")
    return Path.of(url.toURI()).readText()
}
