package no.nav.modiapersonoversikt.rest.hode

import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils.requireNavIdent
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Companion.describe
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class Me(
    val ident: String,
    val navn: String,
    val fornavn: String,
    val etternavn: String,
)

data class Enheter(
    val ident: String,
    val enhetliste: List<Enhet>,
)

data class Enhet(
    val enhetId: String,
    val navn: String,
)

@RestController
@RequestMapping("/rest/hode")
class HodeController
    @Autowired
    constructor(
        private val ansattService: AnsattService,
        val tilgangskontroll: Tilgangskontroll,
    ) {
        @GetMapping("/me")
        fun hentSaksbehandler(): Me =
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(describe(Audit.Action.READ, AuditResources.Saksbehandler.Companion.NavnOgEnheter)) {
                    val navIdent = requireNavIdent()
                    val veileder = ansattService.hentVeileder(navIdent)
                    Me(navIdent.get(), veileder.navn, veileder.fornavn, veileder.etternavn)
                }

        @GetMapping("/enheter")
        fun hentEnheter(): Enheter =
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(describe(Audit.Action.READ, AuditResources.Saksbehandler.Companion.Enheter)) {
                    val navIdent = requireNavIdent()
                    val enheter =
                        ansattService
                            .hentEnhetsliste(navIdent)
                            .map { enhet -> Enhet(enhet.enhetId, enhet.enhetNavn) }
                    Enheter(navIdent.get(), enheter)
                }
    }
