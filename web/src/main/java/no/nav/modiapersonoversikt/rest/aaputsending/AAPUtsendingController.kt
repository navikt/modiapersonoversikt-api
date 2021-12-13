package no.nav.modiapersonoversikt.rest.aaputsending

import no.nav.common.job.leader_election.LeaderElectionHttpClient
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Companion.skipAuditLog
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseUtsendingService
import no.nav.modiapersonoversikt.legacy.api.service.saker.SakerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/internal/aaputsending")
class AAPUtsendingController @Autowired constructor(
    private val tilgangskontroll: Tilgangskontroll,
    sakerService: SakerService,
    henvendelseService: HenvendelseUtsendingService
) {
    private val leaderElectionClient = LeaderElectionHttpClient()
    private val service = AAPUtsendingService(sakerService, henvendelseService, leaderElectionClient)

    @GetMapping("/status")
    fun hentStatus(): AAPUtsendingService.Status {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanStarteHasteUtsending)
            .get(skipAuditLog) {
                return@get service.status()
            }
    }

    @PostMapping("/start")
    fun startUtsending(@RequestBody fnrs: List<FnrEnhet>): AAPUtsendingService.Status {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanStarteHasteUtsending)
            .get(skipAuditLog) {
                val unikeFnr = fnrs.toSet().toList()
                return@get service.utsendingAAP(unikeFnr)
            }
    }

    @PostMapping("/reset")
    fun reset(): AAPUtsendingService.Status {
        return tilgangskontroll
            .check(Policies.tilgangTilModia)
            .check(Policies.kanStarteHasteUtsending)
            .get(skipAuditLog) {
                return@get service.reset()
            }
    }
}
