package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import java.time.ZonedDateTime

object Brukernotifikasjon {
    data class Event(
        val fodselsnummer: String,
        val grupperingsId: String,
        val eventId: String,
        val forstBehandlet: ZonedDateTime,
        val produsent: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val tekst: String,
        val link: String,
        val aktiv: Boolean
    ) : VarslerService.UnifiedVarsel

    enum class Type {
        OPPGAVE, INNBOKS, BESKJED
    }

    interface Client {
        fun hentBrukernotifikasjoner(type: Type, fnr: Fnr): List<Event>
    }

    interface Service {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Event>
        fun hentBrukernotifikasjoner(type: Type, fnr: Fnr): List<Event>
    }
}
