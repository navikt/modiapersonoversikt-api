package no.nav.modiapersonoversikt.service.enhetligkodeverk

import org.slf4j.LoggerFactory

object EnhetligKodeverk {
    interface Service {
        fun hentKodeverk(kodeverkNavn: KodeverkConfig): Kodeverk
    }

    class Kodeverk(private val navn: String, private val kodeverk: Map<String, String>) {
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
