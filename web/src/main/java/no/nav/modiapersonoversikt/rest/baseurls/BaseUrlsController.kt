package no.nav.modiapersonoversikt.rest.baseurls

import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Companion.skipAuditLog
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/baseurls")
class BaseUrlsController
    @Autowired
    constructor(
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        private val baseurls =
            BaseUrls(
                norg2Frontend = EnvironmentUtils.getRequiredProperty("SERVER_NORG2_FRONTEND_URL"),
                drek = EnvironmentUtils.getRequiredProperty("SERVER_DREK_URL"),
                personforvalter = EnvironmentUtils.getRequiredProperty("PERSONFORVALTER_URL"),
            )

        @GetMapping("/v2")
        fun hentV2(): BaseUrls =
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(skipAuditLog()) {
                    baseurls
                }

        data class BaseUrls(
            val norg2Frontend: String,
            val drek: String,
            val personforvalter: String,
        )
    }
