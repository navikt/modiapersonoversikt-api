package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/ytelse")
class YtelseController
    @Autowired
    constructor(
        private val arenaInfotrygdApi: ArenaInfotrygdApi,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @GetMapping("sykepenger/{fnr}")
        fun hentSykepenger(
            @PathVariable("fnr") fnr: String,
        ): Map<String, Any?> {
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnr)) {
                    arenaInfotrygdApi.hentSykepenger(fnr)
                }
        }

        @GetMapping("foreldrepenger/{fnr}")
        fun hentForeldrepenger(
            @PathVariable("fnr") fnr: String,
        ): Map<String, Any?> {
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnr)) {
                    arenaInfotrygdApi.hentForeldrepenger(fnr)
                }
        }

        @GetMapping("pleiepenger/{fnr}")
        fun hentPleiepenger(
            @PathVariable("fnr") fnr: String,
        ): Map<String, Any?> {
            return tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Pleiepenger, AuditIdentifier.FNR to fnr)) {
                    arenaInfotrygdApi.hentPleiepenger(fnr)
                }
        }
    }
