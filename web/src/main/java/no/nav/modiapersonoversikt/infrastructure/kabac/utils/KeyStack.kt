package no.nav.modiapersonoversikt.infrastructure.kabac.utils

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac

internal class KeyStack {
    private val keylock = LinkedHashSet<Key<*>>()
    fun <T> withCycleDetection(key: Key<*>, block: () -> T): T {
        if (!keylock.add(key)) {
            val cyclePrefix = keylock.joinToString(" -> ") { it.name }
            val cycleSuffix = key.name
            val cycle = "$cyclePrefix -> $cycleSuffix"
            throw Kabac.CycleInPipUsageException("Cycle: $cycle")
        }

        val result = block()

        check(keylock.remove(key)) {
            "Cyclic key error, expected $key to be removed but was not in stack"
        }
        keylock.remove(key)

        return result
    }
}
