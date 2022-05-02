package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.abac.AbacClient
import no.nav.modiapersonoversikt.consumer.abac.AbacRequest
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import java.util.*

open class TilgangskontrollContextImpl(
    private val abacClient: AbacClient,
    private val ldap: LDAPService,
    private val ansattService: AnsattService,
    private val sfHenvendelseService: SfHenvendelseService,
    private val unleashService: UnleashService
) : TilgangskontrollContext {
    override fun checkAbac(request: AbacRequest): AbacResponse = abacClient.evaluate(request)
    override fun hentSaksbehandlerId(): Optional<NavIdent> = AuthContextUtils.getIdent()
        .map(String::uppercase)
        .map(::NavIdent)

    override fun hentSaksbehandlerRoller(): List<String> =
        hentSaksbehandlerId()
            .map(ldap::hentRollerForVeileder)
            .orElse(emptyList())
            .map { it.lowercase() }

    override fun harSaksbehandlerRolle(rolle: String) = hentSaksbehandlerRoller().contains(rolle.lowercase())
    override fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String> {
        return ansattService.hentAnsattFagomrader(
            hentSaksbehandlerId().orElseThrow { RuntimeException("Fant ikke saksbehandlerIdent") }.get(),
            valgtEnhet
        )
    }

    override fun hentSaksbehandlereMedTilgangTilHastekassering(): List<NavIdent> {
        return EnvironmentUtils.getRequiredProperty("HASTEKASSERING_TILGANG", "")
            .split(",")
            .map(String::trim)
            .map(String::uppercase)
            .map(::NavIdent)
    }

    override fun hentSaksbehandlereMedTilgangTilInternal(): List<NavIdent> {
        return EnvironmentUtils.getRequiredProperty("INTERNAL_TILGANG", "")
            .split(",")
            .map(String::trim)
            .map(String::uppercase)
            .map(::NavIdent)
    }

    override fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean {
        val kjedeId = behandlingsIder.map { it.fixKjedeId() }.distinct()
        require(kjedeId.size == 1) {
            "Fant ${kjedeId.size} unike kjedeIder i samme spørring, men kan bare være 1."
        }

        val henvendelse = sfHenvendelseService.hentHenvendelse(kjedeId.first())

        return henvendelse.fnr == fnr
    }

    override fun featureToggleEnabled(featureToggle: String): Boolean = unleashService.isEnabled(featureToggle)
}
