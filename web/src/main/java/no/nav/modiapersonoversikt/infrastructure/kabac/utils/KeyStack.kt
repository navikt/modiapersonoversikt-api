package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.KabacException

internal class KeyStack {
    private val stack = LinkedHashSet<Key<*>>()
    fun <T> withCycleDetection(key: Key<*>, block: () -> T): T {
        if (!stack.add(key)) {
            val cyclePrefix = stack.joinToString(" -> ") { it.name }
            val cycleSuffix = key.name
            throw KabacException.CyclicDependenciesException("Cycle: $cyclePrefix -> $cycleSuffix")
        }

        val result = block()
        check(stack.remove(key)) {
            "Cyclic key error, expected $key to be removed but was not in stack"
        }

        return result
    }
}
