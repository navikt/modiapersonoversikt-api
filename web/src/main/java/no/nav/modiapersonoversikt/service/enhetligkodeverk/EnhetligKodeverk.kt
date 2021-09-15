package no.nav.modiapersonoversikt.service.enhetligkodeverk

import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import org.slf4j.LoggerFactory

object EnhetligKodeverk {
    interface Service : Pingable {
        fun hentKodeverk(kodeverkNavn: KodeverkConfig): Kodeverk
    }

    class Kodeverk(val navn: String, val kodeverk: Map<String, String>) {
        private val log = LoggerFactory.getLogger(Kodeverk::class.java)

        fun hentBeskrivelse(kodeRef: String): String {
            val beskrivelse = kodeverk[kodeRef]
            if (beskrivelse == null) {
                log.warn("Ukjent kodeRef $kodeRef i kodeverk $navn")
                return kodeRef
            }
            return beskrivelse
        }
    }

    interface Kilde {
        fun hentKodeverk(providers: KodeverkProviders): Kodeverk
    }
}
