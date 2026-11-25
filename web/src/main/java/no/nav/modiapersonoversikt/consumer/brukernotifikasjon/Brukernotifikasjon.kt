package no.nav.modiapersonoversikt.consumer.brukernotifikasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.tms.generated.BrukervarselDTO
import no.nav.modiapersonoversikt.service.varsel.VarslerService
import java.time.ZonedDateTime

object Brukernotifikasjon {
    data class Feilhistorikk(
        val feilmelding: String,
        val tidspunkt: ZonedDateTime,
    )

    data class Innhold(
        val tekst: String,
        val link: String?,
    )

    interface Client {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<BrukervarselDTO>
    }

    object Mapper {
        fun lagVarselFraEvent(event: BrukervarselDTO): VarslerService.Varsel {
            /* Teamet som eier endepunktet vi henter eventer fra la på nye attributter den 30.1.26. For eventer opprettet
             før cutover-datoen må vi selv finne ut om renotifikasjon er sendt ved å sammenligne opprettet-datoen og
             sistOppdatert-datoen. */
            val cutoverDate = ZonedDateTime.parse("2026-01-31T00:00:00.000Z")
            var renotifikasjonSendt: Boolean
            var renotifikasjonTidspunkt: ZonedDateTime?
            val opprettetDato = event.opprettet.toLocalDate()
            val eksternVarsling = event.eksternVarsling
            val sistOppdatertDato = eksternVarsling?.sistOppdatert?.toLocalDate()

            if (eksternVarsling == null) {
                renotifikasjonSendt = false
                renotifikasjonTidspunkt = null
            } else if (event.opprettet.isBefore(cutoverDate) && opprettetDato != sistOppdatertDato) {
                renotifikasjonSendt = true
                renotifikasjonTidspunkt = eksternVarsling.sistOppdatert
            } else {
                renotifikasjonSendt = eksternVarsling.renotifikasjonSendt ?: false
                renotifikasjonTidspunkt = eksternVarsling.renotifikasjonTidspunkt
            }

            return VarslerService.Varsel(
                type = event.type.value.uppercase(),
                varselId = event.varselId,
                aktiv = event.aktiv,
                produsent = event.produsent.appnavn,
                sensitivitet = event.sensitivitet.value,
                innhold =
                    Innhold(
                        tekst = event.innhold.tekst,
                        link = event.innhold.link,
                    ),
                eksternVarsling =
                    VarslerService.VarselInfo(
                        sendt = eksternVarsling?.sendt ?: false,
                        sendtTidspunkt = eksternVarsling?.sendtTidspunkt ?: event.opprettet,
                        renotifikasjonSendt = renotifikasjonSendt,
                        renotifikasjonTidspunkt = renotifikasjonTidspunkt,
                        sendteKanaler = eksternVarsling?.kanaler ?: emptyList(),
                        feilhistorikk =
                            eksternVarsling?.feilhistorikk?.map {
                                Feilhistorikk(
                                    feilmelding = it.feilmelding,
                                    tidspunkt = it.tidspunkt,
                                )
                            } ?: emptyList(),
                        sistOppdatert = eksternVarsling?.sistOppdatert ?: event.opprettet,
                        kanal = eksternVarsling?.kanal,
                        tittel = eksternVarsling?.tittel,
                        tekst = eksternVarsling?.tekst,
                        renotifikasjonKanal = eksternVarsling?.renotifikasjonKanal,
                        renotifikasjonTittel = eksternVarsling?.renotifikasjonTittel,
                        renotifikasjonTekst = eksternVarsling?.renotifikasjonTekst,
                    ),
                opprettet = event.opprettet,
            )
        }
    }

    interface Service {
        fun hentAlleBrukernotifikasjoner(fnr: Fnr): List<VarslerService.Varsel>
    }
}
