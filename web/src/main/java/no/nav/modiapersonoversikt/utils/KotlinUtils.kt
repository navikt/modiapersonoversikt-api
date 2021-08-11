package no.nav.modiapersonoversikt.utils

object KotlinUtils {
    fun <S, T> Map<S, T?>.filterValuesNotNull(): Map<S, T> {
        return this.filterValues {
            it != null
        } as Map<S, T>
    }
}
