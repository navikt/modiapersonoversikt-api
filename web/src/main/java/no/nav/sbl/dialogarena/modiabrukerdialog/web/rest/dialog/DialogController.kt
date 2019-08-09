package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.TemagruppeTemaMapping.hentTemagruppeForTema
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATO_TID_FORMAT
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad
import org.joda.time.format.DateTimeFormat
import java.util.*
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response

@Path("/dialog/{fnr}")
class DialogController @Inject constructor(private val ansattService: AnsattService,
                                           private val henvendelseService: HenvendelseBehandlingService,
                                           private val henvendelseUtsendingService: HenvendelseUtsendingService,
                                           private val sakerService: SakerService
) {
    @GET
    @Path("/meldinger")
    fun hentMeldinger(@Context request: HttpServletRequest,
                      @PathParam("fnr") fødselsnummer: String): Response {
        val valgtEnhet = hentValgtEnhet(request)
        if (ansattService.hentEnhetsliste().map { it.enhetId }.contains(valgtEnhet)) {
            return Response.ok(hentTråder(henvendelseService.hentMeldinger(fødselsnummer, valgtEnhet).traader))
                    .build()
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build()
        }
    }

    private fun hentTråder(tråder: List<Traad>): List<Map<String, Any?>> {
        return tråder.map {
            mapOf(
                    "traadId" to it.traadId,
                    "meldinger" to hentMeldinger(it.meldinger)
            )
        }
    }

    private fun hentMeldinger(meldinger: List<Melding>): List<Map<String, Any?>> {
        return meldinger.map {
            mapOf(
                    "id" to it.id,
                    "meldingstype" to it.meldingstype?.name,
                    "temagruppe" to it.gjeldendeTemagruppe?.name,
                    "skrevetAv" to it.skrevetAv?.let(this::hentPerson),
                    "journalførtAv" to it.journalfortAv?.let(this::hentPerson),
                    "fritekst" to it.fritekst,
                    "lestDato" to it.lestDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                    "status" to it.status?.name,
                    "opprettetDato" to it.opprettetDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                    "journalfortDato" to it.journalfortDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT)),
                    "ferdigstiltDato" to it.ferdigstiltDato?.toString(DateTimeFormat.forPattern(DATO_TID_FORMAT))
            )
        }
    }

    private fun hentPerson(person: Person): Map<String, Any?> =
            mapOf(
                    "fornavn" to person.fornavn,
                    "etternavn" to person.etternavn
            )

    @POST
    @Path("/sendreferat")
    fun sendMelding(@Context request: HttpServletRequest,
                    @PathParam("fnr") fødselsnummer: String,
                    referatRequest: SendReferatRequest): Response {
        val valgtEnhet = hentValgtEnhet(request)
        henvendelseUtsendingService.sendHenvendelse(lagReferat(referatRequest, fødselsnummer, valgtEnhet), Optional.empty(), Optional.empty(), valgtEnhet)
        return Response.ok().build()
    }

    private fun lagReferat(referatRequest: SendReferatRequest, fnr: String, valgtEnhet: String): Melding {
        val navident = SubjectHandler.getSubjectHandler().uid
        return Melding().withFnr(fnr)
                .withNavIdent(navident)
                .withEksternAktor(navident)
                .withKanal(referatRequest.kanal.name)
                .withType(Meldingstype.valueOf("SAMTALEREFERAT_" + referatRequest.kanal))
                .withFritekst(Fritekst(referatRequest.fritekst))
                .withTilknyttetEnhet(valgtEnhet)
                .withErTilknyttetAnsatt(true)
                .withTemagruppe(referatRequest.temagruppe)
    }

    @POST
    @Path("/sendsporsmal")
    fun sendSporsmal(@Context request: HttpServletRequest,
                     @PathParam("fnr") fødselsnummer: String,
                     sporsmalsRequest: SendSporsmalRequest): Response {
        val valgtEnhet = hentValgtEnhet(request)
        val saker = sakerService.hentSammensatteSaker(fødselsnummer)
        val valgtSak = saker.find { it.saksId == sporsmalsRequest.saksID }
        require(valgtSak != null)
        henvendelseUtsendingService.sendHenvendelse(lagSporsmal(sporsmalsRequest, fødselsnummer, valgtEnhet, valgtSak.temaKode), Optional.empty(), Optional.of(valgtSak), valgtEnhet)
        return Response.ok().build()
    }

    private fun lagSporsmal(sporsmalRequest: SendSporsmalRequest, fnr: String, valgtEnhet: String, sakstema: String): Melding {
        val navident = SubjectHandler.getSubjectHandler().uid
        return Melding().withFnr(fnr)
                .withNavIdent(navident)
                .withEksternAktor(navident)
                .withKanal("TEKST")
                .withType(Meldingstype.SPORSMAL_SKRIFTLIG)
                .withFritekst(Fritekst(sporsmalRequest.fritekst))
                .withTilknyttetEnhet(valgtEnhet)
                .withErTilknyttetAnsatt(sporsmalRequest.erOppgaveTilknyttetAnsatt)
                .withTemagruppe(hentTemagruppeForTema(sakstema))
    }
}

data class SendReferatRequest(
        val fritekst: String,
        val temagruppe: String,
        val kanal: Kanal
)

data class SendSporsmalRequest(
        val fritekst: String,
        val saksID: String,
        val erOppgaveTilknyttetAnsatt: Boolean
)

enum class Kanal {
    OPPMOTE,
    TELEFON
}