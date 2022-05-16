package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.abac.AbacClient
import no.nav.modiapersonoversikt.consumer.abac.AbacRequest
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.consumer.ldap.LDAPService
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentAdressebeskyttelse
import no.nav.modiapersonoversikt.consumer.skjermedePersoner.SkjermedePersonerApi
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.kabac.Decision
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.impl.PolicyEnforcementPointImpl
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.ConfiguredDecisionPoint
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.sfhenvendelse.fixKjedeId
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import java.util.*

open class TilgangskontrollContextImpl(
    private val abacClient: AbacClient,
    private val ldap: LDAPService,
    private val ansattService: AnsattService,
    private val norg: NorgApi,
    private val pdl: PdlOppslagService,
    private val skjermedePersonerApi: SkjermedePersonerApi,
    private val sfHenvendelseService: SfHenvendelseService,
    private val unleashService: UnleashService
) : TilgangskontrollContext {
    private val kabac: Kabac.PolicyEnforcementPoint = PolicyEnforcementPointImpl(
        bias = Decision.Type.DENY,
        policyDecisionPoint = ConfiguredDecisionPoint(pdl, skjermedePersonerApi, norg, ansattService, sfHenvendelseService, ldap)
    )
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

    override fun hentSaksbehandlersEnheter(): List<EnhetId> =
        ansattService.hentEnhetsliste().map { EnhetId(it.enhetId) }

    override fun hentBrukersRegionalEnhet(ident: EksternBrukerId): EnhetId? {
        return hentBrukersEnhet(ident)
            ?.let { norg.hentRegionalEnhet(it) }
    }

    override fun hentBrukersEnhet(ident: EksternBrukerId): EnhetId? {
        return hentBrukersGeografiskeTilknyttning(ident)
            ?.let { gt ->
                norg.finnNavKontor(gt, null)
            }
            ?.let {
                EnhetId(it.enhetId)
            }
    }

    override fun hentDiskresjonskode(ident: EksternBrukerId): String? {
        return pdl.hentAdressebeskyttelse(ident.get()).finnStrengesteKode()
    }

    private fun hentBrukersGeografiskeTilknyttning(ident: EksternBrukerId): String? {
        return pdl.hentGeografiskTilknyttning(ident.get())
    }

    private fun List<HentAdressebeskyttelse.Adressebeskyttelse>.finnStrengesteKode(): String? {
        return this
            .mapNotNull {
                when (it.gradering) {
                    HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG, HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> "6"
                    HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG -> "7"
                    else -> null
                }
            }.minOrNull()
    }

    override fun kabac(): Kabac.PolicyEnforcementPoint = kabac
    override fun unleash(): UnleashService = unleashService
}
