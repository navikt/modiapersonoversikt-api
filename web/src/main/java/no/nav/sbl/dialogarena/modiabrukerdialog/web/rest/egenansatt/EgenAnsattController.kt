package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.READ
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Saksbehandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/egenansatt/{fnr}")
class EgenAnsattController @Autowired constructor(
        private val egenAnsattService: EgenAnsattService,
        private val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping
    fun erEgenAnsatt(@PathVariable("fnr") fnr: String): Map<String, Boolean> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Saksbehandler.EgenAnsatt, AuditIdentifier.FNR to fnr)) {
                    mapOf("erEgenAnsatt" to egenAnsattService.erEgenAnsatt(fnr))
                }
    }
}


