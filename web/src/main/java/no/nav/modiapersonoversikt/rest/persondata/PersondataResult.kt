package no.nav.modiapersonoversikt.rest.persondata

sealed class PersondataResult<T>(val system: InformasjonElement) {
    enum class InformasjonElement {
        PDL_GT,
        PDL_TREDJEPARTSPERSONER,
        EGEN_ANSATT,
        DKIF,
        DKIF_TREDJEPARTSPERSONER,
        BANKKONTO,
        VEILEDER_ROLLER,
        NORG_NAVKONTOR,
        NORG_KONTAKTINFORMASJON,
    }
    fun <S> map(newSystem: InformasjonElement = system, block: (t: T) -> S): PersondataResult<S> {
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

    fun <S> fold(onSuccess: (t: T) -> S, onFailure: (system: InformasjonElement, t: Throwable) -> S): S {
        return when (this) {
            is Failure<*> -> onFailure(this.system, this.exception)
            is Success<T> -> onSuccess(this.value)
        }
    }

    class Success<T>(name: InformasjonElement, val value: T) : PersondataResult<T>(name)
    class Failure<T>(name: InformasjonElement, val exception: Throwable) : PersondataResult<T>(name)

    companion object {
        @JvmStatic
        fun <T> runCatching(system: InformasjonElement, block: () -> T): PersondataResult<T> {
            return try {
                Success(system, block())
            } catch (e: Throwable) {
                Failure(system, e)
            }
        }
    }
}
