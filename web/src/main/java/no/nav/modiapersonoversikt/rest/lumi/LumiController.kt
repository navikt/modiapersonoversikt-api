package no.nav.modiapersonoversikt.rest.lumi

import com.fasterxml.jackson.databind.JsonNode
import no.nav.modiapersonoversikt.consumer.lumi.LumiService
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/lumi")
class LumiController
    @Autowired
    constructor(
        private val lumiService: LumiService,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @PostMapping("/feedback")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        fun submitFeedback(
            @RequestBody transportPayload: JsonNode,
        ) {
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog()) {
                    lumiService.submitFeedback(transportPayload)
                }
        }
    }
