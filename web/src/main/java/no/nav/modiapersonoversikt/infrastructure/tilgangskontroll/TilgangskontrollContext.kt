package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.types.identer.EksternBrukerId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.abac.AbacRequest
import no.nav.modiapersonoversikt.consumer.abac.AbacResponse
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import java.util.*

interface TilgangskontrollContext {
    fun checkAbac(request: AbacRequest): AbacResponse
    fun hentSaksbehandlerId(): Optional<NavIdent>
    fun hentSaksbehandlerRoller(): List<String>
    fun harSaksbehandlerRolle(rolle: String): Boolean
    fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String>
    fun hentSaksbehandlereMedTilgangTilHastekassering(): List<NavIdent>
    fun hentSaksbehandlereMedTilgangTilInternal(): List<NavIdent>
    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
    fun featureToggleEnabled(featureToggle: String): Boolean
    fun hentSaksbehandlersEnheter(): List<EnhetId>
    fun hentBrukersRegionalEnhet(ident: EksternBrukerId): EnhetId?
    fun hentBrukersEnhet(ident: EksternBrukerId): EnhetId?
    fun hentDiskresjonskode(ident: EksternBrukerId): String?

    fun kabac(): Kabac.PolicyEnforcementPoint
}
