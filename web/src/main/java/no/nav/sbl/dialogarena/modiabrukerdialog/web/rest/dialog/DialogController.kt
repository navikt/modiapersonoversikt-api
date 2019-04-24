package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet
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
                                           private val henvendelseUtsendingService: HenvendelseUtsendingService) {
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
    @Path("/sendmelding")
    fun sendMelding(@Context request: HttpServletRequest,
                    @PathParam("fnr") fødselsnummer: String,
                    meldingRequest: SendMeldingRequest): Response {
        // TODO Vi må også sende inn sak og oppgave etterhvert
        henvendelseUtsendingService.sendHenvendelse(lagMelding(meldingRequest), Optional.empty(), Optional.empty(), hentValgtEnhet(request))
        return Response.ok().build()
    }

    private fun lagMelding(meldingRequest: SendMeldingRequest): Melding =
            Melding().withFnr(meldingRequest.fnr)
                    .withNavIdent(meldingRequest.navident)
                    .withKanal(meldingRequest.kanal)
                    .withType(meldingRequest.type?.let { Meldingstype.valueOf(it) })
                    .withFritekst(Fritekst(meldingRequest.fritekst))
                    .withEksternAktor(meldingRequest.navident)
                    .withTilknyttetEnhet(meldingRequest.tilknyttetEnhet)
                    .withErTilknyttetAnsatt(meldingRequest.erTilknyttetAnsatt)
                    .withTraadId(meldingRequest.traadId)
                    .withKontorsperretEnhet(meldingRequest.kontorsperretEnhet)
                    .withTemagruppe(meldingRequest.temagruppe)
}

data class SendMeldingRequest(
        val fnr: String,
        val navident: String,
        val kanal: String,
        val type: String?,
        val fritekst: String,
        val tilknyttetEnhet: String,
        val erTilknyttetAnsatt: Boolean,
        val traadId: String?,
        val kontorsperretEnhet: String?,
        val temagruppe: String
)