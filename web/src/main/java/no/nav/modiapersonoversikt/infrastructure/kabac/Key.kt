package no.nav.modiapersonoversikt.infrastructure.kabac

class Key<T>(val name: String) {
    init {
        if (name.isEmpty()) {
            throw IllegalStateException("PointKey can't be blank")
        }
    }

    override fun toString(): String {
        return "Key{$name}"
    }
}
