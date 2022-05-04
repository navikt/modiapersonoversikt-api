package no.nav.modiapersonoversikt.infrastructure.kabac.utils

class Key<T>(val name: String) {
    init {
        if (name.isEmpty()) {
            throw IllegalStateException("PointKey can't be blank")
        }
    }

    override fun toString(): String {
        return "Key($name)"
    }

    companion object {
        operator fun <T> invoke(v: Any): Key<T> = Key(v::class.java.simpleName)
    }
}
