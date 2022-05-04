package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac

import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.VeilederRollerPip

object Policies {
    private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")

    val tilgangTilModia = Kabac.Policy { ctx ->
        val veilederRoller = ctx.requireValue(VeilederRollerPip)
        if (modiaRoller.union(veilederRoller).isNotEmpty()) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.Deny("Veileder har ikke tilgang til modia")
        }
    }
}
