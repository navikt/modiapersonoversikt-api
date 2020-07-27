package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import no.nav.sbl.dialogarena.abac.AbacRequest
import no.nav.sbl.dialogarena.abac.AbacResponse
import java.util.*

interface TilgangskontrollContext {
    fun checkAbac(request: AbacRequest): AbacResponse
    fun hentSaksbehandlerId() : Optional<String>
    fun harSaksbehandlerRolle(rolle: String): Boolean
    fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String>
    fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String>
    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
    fun featureToggleEnabled(featureToggle: String): Boolean
}
