package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private const val APPLICATION_PREFIX = "modiabrukerdialog."

@RestController
@RequestMapping("/featuretoggle")
class FeatureToggleController @Autowired constructor(
        private val unleashService: UnleashService,
        private val tilgangskontroll: Tilgangskontroll
) {
    @GetMapping("/{id}")
    fun hentMedId(@PathVariable("id") toggleId: String): Boolean =
            tilgangskontroll
                    .check(Policies.tilgangTilModia)
                    .get(Audit.skipAuditLog()) {
                        unleashService.isEnabled(sjekkPrefix(toggleId))
                    }


    fun sjekkPrefix(propertyKey: String): String {
        return if (propertyKey.contains(".")) propertyKey else APPLICATION_PREFIX + propertyKey
    }
}
