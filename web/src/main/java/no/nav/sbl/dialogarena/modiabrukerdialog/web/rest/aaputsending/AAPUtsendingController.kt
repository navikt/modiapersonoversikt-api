package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.aaputsending

import no.nav.common.leaderelection.LeaderElectionHttpClient
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit.Companion.skipAuditLog
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
