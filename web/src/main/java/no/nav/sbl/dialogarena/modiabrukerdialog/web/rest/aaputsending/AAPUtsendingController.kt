package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.aaputsending

import no.nav.common.leaderelection.LeaderElectionHttpClient
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit.Companion.skipAuditLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/internal/aaputsending")
class AAPUtsendingController @Autowired constructor(
        private val tilgangskontroll: Tilgangskontroll,
        stsService: SystemUserTokenProvider,
        sakerService: SakerService,
        henvendelseService: HenvendelseUtsendingService
) {
    val leaderElectionClient = LeaderElectionHttpClient()
    val service = AAPUtsendingService(sakerService, henvendelseService, leaderElectionClient, stsService)

    @GetMapping("/status")
    fun hentStatus() = service.status()

    @GetMapping
    fun hentStatus2() = service.status()

    @PostMapping("/start")
    fun startUtsending(@RequestBody fnrs: List<String>): AAPUtsendingService.Status {
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
        return service.reset()
    }
}
