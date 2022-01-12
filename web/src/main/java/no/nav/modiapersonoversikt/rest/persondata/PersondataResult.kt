package no.nav.modiapersonoversikt.rest.persondata

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

    fun <S> fold(onSuccess: (t: T) -> S, onFailure: (system: String, t: Throwable) -> S): S {
        return when (this) {
            is Failure<*> -> onFailure(this.system, this.exception)
            is Success<T> -> onSuccess(this.value)
        }
    }

    class Success<T>(name: String, val value: T) : PersondataResult<T>(name)
    class Failure<T>(name: String, val exception: Throwable) : PersondataResult<T>(name)

    companion object {
        @JvmStatic
        fun <T> runCatching(system: String, block: () -> T): PersondataResult<T> {
            return try {
                Success(system, block())
            } catch (e: Throwable) {
                Failure(system, e)
            }
        }
    }
}
