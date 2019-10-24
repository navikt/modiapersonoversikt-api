package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import java.util.*


interface TilgangskontrollContext {
    fun hentSaksbehandlerId() : Optional<String>
    fun hentSaksbehandlerRoller(): List<String>
    fun harSaksbehandlerRolle(rolle: String): Boolean
    fun hentDiskresjonkode(fnr: String): String?
    fun hentBrukersEnhet(fnr: String): String?
    fun hentTemagrupperForSaksbehandler(valgtEnhet: String): Set<String>
    fun hentSaksbehandlerLokalEnheter(): Set<String>
    fun hentSaksbehandlersFylkesEnheter(): Set<String>
    fun hentSaksbehandlereMedTilgangTilHastekassering(): List<String>
    fun alleBehandlingsIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
    fun alleHenvendelseIderTilhorerBruker(fnr: String, behandlingsIder: List<String>): Boolean
}