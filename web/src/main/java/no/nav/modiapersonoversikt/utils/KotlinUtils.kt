package no.nav.modiapersonoversikt.utils

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
