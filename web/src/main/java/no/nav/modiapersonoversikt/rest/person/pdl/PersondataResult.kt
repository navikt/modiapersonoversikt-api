package no.nav.modiapersonoversikt.rest.person.pdl

sealed class PersondataResult<T>(val system: String) {
    fun <S> map(newSystem: String = system, block: (t: T) -> S): PersondataResult<S> {
        return when (this) {
            is Failure<*> -> this as PersondataResult<S>
            is Success<T> -> runCatching(newSystem) {
                block(this.value)
            }
        }
    }

    fun getOrElse(other: T): T {
        return when (this) {
            is Failure<*> -> other
            is Success<T> -> this.value
        }
    }

    fun getOrNull(): T? {
        return when (this) {
            is Failure<*> -> null
            is Success<T> -> this.value
        }
    }

    class Success<T>(name: String, val value: T) : PersondataResult<T>(name)
    class Failure<T>(name: String, val exception: Throwable) : PersondataResult<T>(name)

    companion object {
        fun <T> runCatching(system: String, block: () -> T): PersondataResult<T> {
            return try {
                Success(system, block())
            } catch (e: Throwable) {
                Failure(system, e)
            }
        }
    }
}
