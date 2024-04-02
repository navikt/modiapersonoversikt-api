package no.nav.modiapersonoversikt.rest.featuretoggle

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

private const val APPLICATION_PREFIX = "modiapersonoversikt."

@RestController
@RequestMapping("/rest/featuretoggle", "/rest/featuretoggle/")
class FeatureToggleController
    @Autowired
    constructor(
        private val unleashService: UnleashService,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @GetMapping("/{id}")
        fun hentMedId(
            @PathVariable("id") toggleId: String,
        ): Boolean =
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    unleashService.isEnabled(sjekkPrefix(toggleId))
                }

        @GetMapping
        fun hentToggles(
            @RequestParam(value = "id", required = false) ids: Set<String>?,
        ): Map<String, Boolean> {
            return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    (ids ?: emptySet()).associateWith {
                        unleashService.isEnabled(sjekkPrefix(it))
                    }
                }
        }

        private fun sjekkPrefix(propertyKey: String): String {
            return if (propertyKey.contains(".")) propertyKey else APPLICATION_PREFIX + propertyKey
        }
    }
