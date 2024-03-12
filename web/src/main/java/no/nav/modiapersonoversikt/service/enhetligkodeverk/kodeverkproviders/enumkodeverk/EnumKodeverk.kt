package no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.enumkodeverk

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import kotlin.reflect.KClass

object EnumKodeverk {
    interface WithValue<T> {
        fun getValue(): T
    }

    class Kilde<ENUM, VALUE>(enum: KClass<ENUM>) : EnhetligKodeverk.Kilde<String, VALUE>
        where ENUM : Enum<ENUM>, ENUM : WithValue<VALUE> {
        override val navn: String = enum.toString()
        private val kodeverk =
            EnhetligKodeverk.Kodeverk(
                navn = navn,
                kodeverk =
                    enum.java
                        .enumConstants
                        .associate { it.name to it.getValue() },
            )

        override fun hentKodeverk(providers: KodeverkProviders): EnhetligKodeverk.Kodeverk<String, VALUE> = kodeverk
    }
}
