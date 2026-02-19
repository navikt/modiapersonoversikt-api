package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import com.fasterxml.jackson.annotation.JsonProperty
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import java.time.ZonedDateTime

object Brukernotifikasjon {
    data class Event(
        val type: Type,
        val varselId: String,
        val aktiv: Boolean,
        val produsent: Produsent,
        val sensitivitet: String,
        val innhold: Innhold,
        val eksternVarsling: EksternVarslingInfo?,
        val opprettet: ZonedDateTime,
        val aktivFremTil: ZonedDateTime? = null,
        val inaktivert: ZonedDateTime? = null,
        val inaktivertAv: String? = null,
    ) : VarslerService.UnifiedVarsel

    data class EksternVarslingInfo(
        val sendt: Boolean?,
        val sendtTidspunkt: ZonedDateTime?,
        val sendtSomBatch: Boolean?,
        var renotifikasjonSendt: Boolean?,
        var renotifikasjonTidspunkt: ZonedDateTime? = null,
        val kanaler: List<String>?,
        val feilHistorikk: List<Feilhistorikk>?,
        val sistOppdatert: ZonedDateTime,
    )

    data class Feilhistorikk(
        val feilmelding: String,
        val tidspunkt: ZonedDateTime,
    )

    data class Produsent(
        val namespace: String,
        val appnavn: String,
    )

    data class Innhold(
        val tekst: String,
        val link: String?,
    )

    enum class Type {
        @JsonProperty("oppgave")
        OPPGAVE,

        @JsonProperty("innboks")
        INNBOKS,

        @JsonProperty("beskjed")
        BESKJED,
    }

    interface Client {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Event>
    }

    object Mapper {
        fun lagVarselFraEvent(event: Event): VarslerService.Varsel {
            var renotifikasjonSendt: Boolean
            var renotifikasjonTidspunkt: ZonedDateTime?
            val cutoverDate = ZonedDateTime.parse("2026-01-31T00:00:00.000Z")
            val opprettetDato = event.opprettet.toLocalDate()
            val sistOppdatertDato = event.eksternVarsling?.sistOppdatert?.toLocalDate()

            if (event.eksternVarsling == null) {
                renotifikasjonSendt = false
                renotifikasjonTidspunkt = null
            } else if (event.opprettet.isBefore(cutoverDate) && opprettetDato != sistOppdatertDato) {
                renotifikasjonSendt = true
                renotifikasjonTidspunkt = event.eksternVarsling.sistOppdatert
            } else {
                renotifikasjonSendt = event.eksternVarsling.renotifikasjonSendt ?: false
                renotifikasjonTidspunkt = event.eksternVarsling.renotifikasjonTidspunkt
            }

            return VarslerService.Varsel(
                type = event.type.name,
                varselId = event.varselId,
                aktiv = event.aktiv,
                produsent = event.produsent.appnavn,
                sensitivitet = event.sensitivitet,
                innhold =
                    Innhold(
                        tekst = event.innhold.tekst,
                        link = event.innhold.link,
                    ),
                eksternVarsling =
                    VarslerService.VarselInfo(
                        sendt = event.eksternVarsling?.sendt ?: false,
                        sendtTidspunkt = event.eksternVarsling?.sendtTidspunkt ?: event.opprettet,
                        renotifikasjonSendt = renotifikasjonSendt,
                        renotifikasjonTidspunkt = renotifikasjonTidspunkt,
                        sendteKanaler = event.eksternVarsling?.kanaler ?: emptyList(),
                        feilhistorikk = event.eksternVarsling?.feilHistorikk ?: emptyList(),
                        sistOppdatert = event.eksternVarsling?.sistOppdatert ?: event.opprettet,
                    ),
                opprettet = event.opprettet,
            )
        }
    }

    interface Service {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<VarslerService.Varsel>
    }
}
