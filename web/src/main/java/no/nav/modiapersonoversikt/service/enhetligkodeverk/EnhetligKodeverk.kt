package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.modiapersonoversikt.infrastructure.ping.Pingable
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import org.slf4j.LoggerFactory

object EnhetligKodeverk {
    interface Service : Pingable {
        fun <KEY, VALUE> hentKodeverk(kilde: Kilde<KEY, VALUE>): Kodeverk<KEY, VALUE>
    }

    class Kodeverk<KEY, VALUE>(val navn: String, private val kodeverk: Map<KEY, VALUE>) {
        private val log = LoggerFactory.getLogger(Kodeverk::class.java)

        fun hentVerdi(
            kodeRef: KEY,
            default: VALUE,
        ): VALUE {
            val verdi = kodeverk[kodeRef]
            if (verdi == null) {
                log.warn("Ukjent kodeRef $kodeRef i kodeverk $navn")
                return default
            }
            return verdi
        }

        fun hentVerdiEllerNull(kodeRef: KEY): VALUE? = kodeverk[kodeRef]

        fun hentVerdi(kodeRef: KEY): VALUE =
            requireNotNull(hentVerdiEllerNull(kodeRef)) {
                "Ukjent kodeRef $kodeRef i kodeverk $navn"
            }

        fun hentAlleVerdier(): Collection<VALUE> = kodeverk.values

        fun asMap(): Map<KEY, VALUE> = kodeverk.toMap()
    }

    interface Kilde<KEY, VALUE> {
        val navn: String

        fun hentKodeverk(providers: KodeverkProviders): Kodeverk<KEY, VALUE>
    }

    interface KodeverkProvider<KEY, VALUE> {
        fun hentKodeverk(kodeverkNavn: String): Kodeverk<KEY, VALUE>
    }
}
