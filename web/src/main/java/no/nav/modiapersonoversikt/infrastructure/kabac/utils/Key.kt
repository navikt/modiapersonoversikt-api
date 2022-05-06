package no.nav.modiapersonoversikt.infrastructure.kabac.utils

class Key<T : Any>(val name: String) {
    init {
        if (name.isEmpty()) {
            throw IllegalStateException("PointKey can't be blank")
        }
    }

    fun withValue(value: T?) = AttributeValue(this, value)

    override fun toString(): String {
        return "Key($name)"
    }

    override fun hashCode(): Int = name.hashCode()

    override fun equals(other: Any?): Boolean = name == other

    companion object {
        operator fun <T : Any> invoke(v: Any): Key<T> = Key(v::class.java.simpleName)
    }
}
