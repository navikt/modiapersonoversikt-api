package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import java.time.ZonedDateTime

object Brukernotifikasjon {
    data class Event(
        val fodselsnummer: String,
        val grupperingsId: String? = null,
        val eventId: String,
        val forstBehandlet: ZonedDateTime,
        val produsent: String,
        val sikkerhetsnivaa: Int,
        val sistOppdatert: ZonedDateTime,
        val tekst: String,
        val link: String,
        val aktiv: Boolean,
        val eksternVarslingSendt: Boolean,
        val eksternVarslingKanaler: List<String>,
        val eksternVarsling: EksternVarslingInfo? = null,
        val varslingsTidspunkt: VarslingsTidspunkt? = null,
    ) : VarslerService.UnifiedVarsel

    data class EksternVarslingInfo(
        val sendt: Boolean,
        val renotifikasjonSendt: Boolean,
        val prefererteKanaler: List<String>,
        val sendteKanaler: List<String>,
        val historikk: List<HistorikkEntry>,
    )

    data class HistorikkEntry(
        val melding: String,
        val status: String,
        val distribusjonsId: Long? = null,
        val kanal: String? = null,
        val renotifikasjon: Boolean? = null,
        val tidspunkt: ZonedDateTime,
    )

    data class VarslingsTidspunkt(
        val sendt: Boolean,
        val tidspunkt: ZonedDateTime?,
        val renotifikasjonSendt: Boolean,
        val renotifikasjonTidspunkt: ZonedDateTime?,
        val sendteKanaler: List<String>,
        val renotifikasjonsKanaler: List<String>,
        val harFeilteVarslinger: Boolean,
        val harFeilteRevarslinger: Boolean,
        val feilteVarsliner: List<FeiletVarsling>,
        val feilteRevarslinger: List<FeiletVarsling>,
    )

    data class FeiletVarsling(
        val tidspunkt: ZonedDateTime,
        val feilmelding: String,
        val kanal: String?,
    )

    data class EventV2(
        val type: String,
        val varselId: String,
        val aktiv: Boolean,
        val produsent: Produsent,
        val sensitivitet: String,
        val innhold: Innhold,
        val eksternVarsling: EksternVarslingInfoV2? = null,
        val opprettet: ZonedDateTime,
        val aktivFremTil: ZonedDateTime? = null,
        val inaktivert: ZonedDateTime? = null,
        val inaktivertAv: String? = null,
        val varslingsTidspunkt: VarslingsTidspunkt? = null,
    ) : VarslerService.UnifiedVarsel

    data class Produsent(
        val namespace: String,
        val appnavn: String,
    )

    data class Innhold(
        val tekst: String,
        val link: String? = null,
    )

    data class EksternVarslingInfoV2(
        val sendt: Boolean,
        val renotifikasjonSendt: Boolean,
        val kanaler: List<String>,
        val historikk: List<HistorikkEntry>,
        val sistOppdatert: ZonedDateTime,
    )

    enum class Type {
        OPPGAVE,
        INNBOKS,
        BESKJED,
    }

    interface Client {
        fun hentBrukernotifikasjoner(
            type: Type,
            fnr: Fnr,
        ): List<Event>

        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<EventV2>
    }

    object Mapper {
        private fun filtrerUtRevarslinger(historikk: List<HistorikkEntry>): Pair<List<HistorikkEntry>, List<HistorikkEntry>> {
            val varslinger = mutableListOf<HistorikkEntry>()
            val revarslinger = mutableListOf<HistorikkEntry>()

            for (entry in historikk) {
                if (entry.renotifikasjon == null || !entry.renotifikasjon) {
                    varslinger.add(entry)
                } else {
                    revarslinger.add(entry)
                }
            }
            return Pair(varslinger, revarslinger)
        }

        private fun finnTidspunktFraVarslingsHistorikk(historikk: List<HistorikkEntry>): ZonedDateTime? =
            historikk.filter { it.status == "sendt" }.minByOrNull { it.tidspunkt }?.tidspunkt

        private fun finnFeilteVarslinger(historikk: List<HistorikkEntry>): List<FeiletVarsling> {
            val feilteVarsliner = mutableListOf<FeiletVarsling>()

            for (entry in historikk) {
                if (entry.status == "feilet") {
                    feilteVarsliner.add(
                        FeiletVarsling(
                            tidspunkt = entry.tidspunkt,
                            feilmelding = entry.melding,
                            kanal = entry.kanal,
                        ),
                    )
                }
            }

            return feilteVarsliner
        }

        fun byggVarslingsTidspunkt(event: Event): Event {
            val eksternVarsling = event.eksternVarsling ?: return event

            val (varslinger, revarslinger) = filtrerUtRevarslinger(eksternVarsling.historikk)
            val feilteVarsliner = finnFeilteVarslinger(varslinger)
            val feilteRevarslinger = finnFeilteVarslinger(revarslinger)

            val varslingsTidspunkt =
                VarslingsTidspunkt(
                    sendt = eksternVarsling.sendt,
                    tidspunkt = finnTidspunktFraVarslingsHistorikk(varslinger),
                    renotifikasjonSendt = eksternVarsling.renotifikasjonSendt,
                    renotifikasjonTidspunkt = finnTidspunktFraVarslingsHistorikk(revarslinger),
                    sendteKanaler = varslinger.filter { it.kanal != null }.map { it.kanal!! },
                    renotifikasjonsKanaler = revarslinger.filter { it.kanal != null }.map { it.kanal!! },
                    feilteVarsliner = feilteVarsliner,
                    harFeilteVarslinger = feilteVarsliner.isNotEmpty(),
                    feilteRevarslinger = feilteRevarslinger,
                    harFeilteRevarslinger = feilteRevarslinger.isNotEmpty(),
                )

            return Event(
                fodselsnummer = event.fodselsnummer,
                grupperingsId = event.grupperingsId,
                eventId = event.eventId,
                forstBehandlet = event.forstBehandlet,
                produsent = event.produsent,
                sikkerhetsnivaa = event.sikkerhetsnivaa,
                sistOppdatert = event.sistOppdatert,
                tekst = event.tekst,
                link = event.link,
                aktiv = event.aktiv,
                eksternVarslingSendt = event.eksternVarslingSendt,
                eksternVarslingKanaler = event.eksternVarslingKanaler,
                varslingsTidspunkt = varslingsTidspunkt,
            )
        }

        fun byggVarslingsTidspunktV2(
            fnr: String,
            eventV2: EventV2,
        ): Event {
            val eksternVarsling = eventV2.eksternVarsling
            val event =
                Event(
                    fodselsnummer = fnr,
                    eventId = eventV2.varselId,
                    forstBehandlet = eventV2.opprettet,
                    produsent = eventV2.produsent.appnavn,
                    sikkerhetsnivaa = toSikkerhetsnivaa(eventV2.sensitivitet),
                    sistOppdatert = eventV2.opprettet,
                    tekst = eventV2.innhold.tekst,
                    link = eventV2.innhold.link ?: "",
                    aktiv = eventV2.aktiv,
                    eksternVarslingSendt = eventV2.eksternVarsling?.sendt ?: false,
                    eksternVarslingKanaler = eventV2.eksternVarsling?.kanaler ?: listOf(),
                )

            if (eksternVarsling != null) {
                val (varslinger, revarslinger) = filtrerUtRevarslinger(eksternVarsling.historikk)
                val feilteVarsliner = finnFeilteVarslinger(varslinger)
                val feilteRevarslinger = finnFeilteVarslinger(revarslinger)

                val varslingsTidspunkt =
                    VarslingsTidspunkt(
                        sendt = eksternVarsling.sendt,
                        tidspunkt = finnTidspunktFraVarslingsHistorikk(varslinger),
                        renotifikasjonSendt = eksternVarsling.renotifikasjonSendt,
                        renotifikasjonTidspunkt = finnTidspunktFraVarslingsHistorikk(revarslinger),
                        sendteKanaler = varslinger.filter { it.kanal != null }.map { it.kanal!! },
                        renotifikasjonsKanaler = revarslinger.filter { it.kanal != null }.map { it.kanal!! },
                        feilteVarsliner = feilteVarsliner,
                        harFeilteVarslinger = feilteVarsliner.isNotEmpty(),
                        feilteRevarslinger = feilteRevarslinger,
                        harFeilteRevarslinger = feilteRevarslinger.isNotEmpty(),
                    )

                return event.copy(
                    sistOppdatert = eksternVarsling.sistOppdatert,
                    varslingsTidspunkt = varslingsTidspunkt,
                )
            }

            return event
        }

        fun toSikkerhetsnivaa(sensitivitet: String): Int =
            when (sensitivitet) {
                "substantial" -> 3
                "high" -> 4
                else -> throw IllegalArgumentException("Ugyldig sikkerhetsnivaa")
            }
    }

    interface Service {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<Event>

        fun hentBrukernotifikasjoner(
            type: Type,
            fnr: Fnr,
        ): List<Event>
    }
}
