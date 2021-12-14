package no.nav.modiapersonoversikt.config

import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.abac.AbacClient
import no.nav.modiapersonoversikt.consumer.abac.AbacRequest
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext
import no.nav.modiapersonoversikt.legacy.api.service.HenvendelseLesService
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService
import no.nav.modiapersonoversikt.legacy.api.service.norg.AnsattService
import java.util.*

open class TilgangskontrollContextImpl(
    private val abacClient: AbacClient,
    private val ldap: LDAPService,
    private val ansattService: AnsattService,
    private val henvendelseLesService: HenvendelseLesService,
    private val unleashService: no.nav.modiapersonoversikt.service.unleash.UnleashService
) : TilgangskontrollContext {
    override fun checkAbac(request: AbacRequest): AbacResponse = abacClient.evaluate(request)
    override fun hentSaksbehandlerId(): Optional<String> = SubjectHandler.getIdent().map(String::uppercase)
    override fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.lowercase())
    override fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> {
        return ansattService.hentAnsattFagomrader(
            hentSaksbehandlerId().orElseThrow { RuntimeException("Fant ikke saksbehandlerIdent") },
            valgtEnhet
        )
    }

    override fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String> {
        return EnvironmentUtils.getRequiredProperty("HASTEKASSERING_TILGANG", "")
            .split(",")
            .map(String::trim)
            .map(String::uppercase)
    }

    override fun hentSaksbehandlereMedTilgangTilInternal(): List<String> {
        return EnvironmentUtils.getRequiredProperty("INTERNAL_TILGANG", "")
            .split(",")
            .map(String::trim)
            .map(String::uppercase)
    }

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        return henvendelseLesService.alleBehandlingsIderTilhorerBruker(fnr, behandlingsIder)
    }

    override fun featureToggleEnabled(featureToggle: String): Boolean = unleashService.isEnabled(featureToggle)

    private fun hentSaksbehandlerRoller(): List<String> =
        hentSaksbehandlerId()
            .map(ldap::hentRollerForVeileder)
            .orElse(emptyList())
            .map { it.lowercase() }
}
