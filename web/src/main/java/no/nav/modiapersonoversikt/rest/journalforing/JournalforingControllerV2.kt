package no.nav.modiapersonoversikt.rest.journalforing

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Companion.describe
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.henvendelseTilhorerBruker
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies.tilgangTilBruker
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.journalforingsaker.SakerService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/rest/v2/journalforing")
class JournalforingControllerV2 @Autowired constructor(
    private val sakerService: SakerService,
    private val sfHenvendelseService: SfHenvendelseService,
    private val tilgangskontroll: Tilgangskontroll,
) {
    @PostMapping("/saker/")
    fun hentSaker(@RequestBody fnrRequest: FnrRequest): SakerService.Resultat {
        return tilgangskontroll
            .check(tilgangTilBruker(Fnr.of(fnrRequest.fnr)))
            .get(describe(Audit.Action.READ, AuditResources.Person.GsakSaker, FNR to fnrRequest.fnr)) {
                sakerService.hentSaker(fnrRequest.fnr)
            }
    }

    @PostMapping("/{traadId}")
    fun knyttTilSak(
        @PathVariable("traadId") traadId: String,
        @RequestBody sak: JournalforingSak,
        @RequestParam(value = "enhet") enhet: String?
    ) {
        val auditIdentifier = arrayOf(FNR to sak.fnr, TRAAD_ID to traadId, SAK_ID to sak.saksId)
        return tilgangskontroll
            .check(tilgangTilBruker(Fnr.of(sak.fnr)))
            .check(henvendelseTilhorerBruker(Fnr.of(sak.fnr), traadId))
            .get(describe(Audit.Action.UPDATE, AuditResources.Person.Henvendelse.Journalfor, *auditIdentifier)) {
                if (enhet == null) {
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, FEILMELDING_UTEN_ENHET)
                }
                try {
                    sfHenvendelseService.journalforHenvendelse(
                        enhet = enhet,
                        kjedeId = traadId,
                        saksTema = sak.temaKode,
                        fagsakSystem = sak.fagsystemKode,
                        saksId = sak.fagsystemSaksId
                    )
                } catch (exception: Exception) {
                    throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, UKJENT_FEIL, exception)
                }
            }
    }

    companion object {
        const val FEILMELDING_UTEN_ENHET =
            "Det er dessverre ikke mulig å journalføre henvendelsen. Du må velge enhet du jobber på vegne av på nytt. Bekreft enhet med å trykke på \"Velg\"-knappen."
        const val UKJENT_FEIL = "Ukjent feil"
    }
}
