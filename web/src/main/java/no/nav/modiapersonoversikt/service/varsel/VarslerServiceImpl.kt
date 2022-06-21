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
    }

    override fun hentAlleVarsler(fnr: Fnr): List<VarslerService.UnifiedVarsel> {
        val (varsel, notifikasjoner) = inParallel(
            { hentBrukervarsel(fnr) },
            { brukernotifikasjonService.hentAlleBrukernotifikasjoner(fnr) }
        )
        return varsel + notifikasjoner
    }

    private fun hentBrukervarsel(fnr: Fnr): List<VarslerService.Varsel> {
        return brukervarselV1.runCatching {
            val request = WSHentVarselForBrukerRequest().withBruker(WSPerson().withIdent(fnr.get()))
            val response = hentVarselForBruker(request)
            response
                .brukervarsel
                .varselbestillingListe
                .map(::tilVarsel)
        }.getOrElse {
            log.error("Feilet ved uthenting a varsler.", it)
            emptyList()
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
