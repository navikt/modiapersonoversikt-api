package no.nav.modiapersonoversikt.service.varsel

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.brukernotifikasjon.Brukernotifikasjon
import no.nav.modiapersonoversikt.utils.ConcurrencyUtils.inParallel
import no.nav.tjeneste.virksomhet.brukervarsel.v1.BrukervarselV1
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSPerson
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarsel
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.WSVarselbestilling
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.WSHentVarselForBrukerRequest
import org.slf4j.LoggerFactory

class VarslerServiceImpl(
    private val brukervarselV1: BrukervarselV1,
    private val brukernotifikasjonService: Brukernotifikasjon.Service
) : VarslerService {
    private val log = LoggerFactory.getLogger("VarslerService")

    override fun hentLegacyVarsler(fnr: Fnr): List<VarslerService.Varsel> {
        return hentBrukervarsel(fnr)
            .getOrElse {
                log.error("Feilet ved uthenting a varsler.", it)
                emptyList()
            }
    }

    override fun hentAlleVarsler(fnr: Fnr): VarslerService.Result {
        val (varsel: Result<List<VarslerService.Varsel>>, notifikasjoner: Result<List<Brukernotifikasjon.Event>>) = inParallel(
            { hentBrukervarsel(fnr) },
            { runCatching { brukernotifikasjonService.hentAlleBrukernotifikasjoner(fnr) } }
        )

        val feil = listOfNotNull(
            varsel.exceptionOrNull()?.let { "Feil ved uthenting av varsler" },
            notifikasjoner.exceptionOrNull()?.let { "Feil ved uthenting av notifikasjoner" },
        )
        val varsler = listOfNotNull(
            varsel.getOrDefault(emptyList()),
            notifikasjoner.getOrDefault(emptyList()),
        ).flatten()

        return VarslerService.Result(
            feil = feil,
            varsler = varsler
        )
    }

    private fun hentBrukervarsel(fnr: Fnr): Result<List<VarslerService.Varsel>> {
        return brukervarselV1.runCatching {
            val request = WSHentVarselForBrukerRequest().withBruker(WSPerson().withIdent(fnr.get()))
            val response = hentVarselForBruker(request)
            response
                .brukervarsel
                .varselbestillingListe
                .map(::tilVarsel)
        }
    }

    private fun tilVarselMelding(varsel: WSVarsel): VarslerService.VarselMelding {
        val utsendingsTidspunkt = when {
            varsel.distribuert != null -> varsel.distribuert.toGregorianCalendar().toZonedDateTime()
            varsel.sendt != null -> varsel.sendt.toGregorianCalendar().toZonedDateTime()
            else -> null
        }
        return VarslerService.VarselMelding(
            kanal = varsel.kanal,
            innhold = varsel.varseltekst,
            mottakerInformasjon = varsel.kontaktinfo,
            utsendingsTidspunkt = utsendingsTidspunkt,
            feilbeskrivelse = "",
            epostemne = varsel.varseltittel,
            url = varsel.varselURL,
            erRevarsel = varsel.isReVarsel
        )
    }

    private fun tilVarsel(varselBestilling: WSVarselbestilling): VarslerService.Varsel {
        var sendtTidspunkt = varselBestilling.bestilt?.toGregorianCalendar()?.toZonedDateTime()
        if (varselBestilling.sisteVarselutsendelse != null) {
            sendtTidspunkt = varselBestilling.sisteVarselutsendelse?.toGregorianCalendar()?.toZonedDateTime()
        }

        val meldingListe = varselBestilling.varselListe.map(::tilVarselMelding)
        val erRevarsling = meldingListe.any { it.erRevarsel == true }

        return VarslerService.Varsel(
            varselType = varselBestilling.varseltypeId,
            mottattTidspunkt = sendtTidspunkt,
            meldingListe = meldingListe,
            erRevarsling = erRevarsling,
        )
    }
}