package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.infrastructure.kabac.Kabac
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.CombiningAlgorithm
import no.nav.modiapersonoversikt.infrastructure.kabac.utils.EvaluationContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.*

private object BrukerUtenEnhet : Kabac.Policy {
    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val brukersEnhet: EnhetId? = ctx.getValue(BrukersEnhetPip)
        return if (brukersEnhet == null) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.NotApplicable("Bruker hadde nav-enhet")
        }
    }
}

private object TilgangTilBrukersNavKontor : Kabac.Policy {
    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val brukersEnhet: EnhetId? = ctx.getValue(BrukersEnhetPip)
        val veiledersEnheter: List<EnhetId> = ctx.getValue(VeiledersEnheterPip) ?: emptyList()
        return if (veiledersEnheter.contains(brukersEnhet)) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.NotApplicable("Veileder hadde ikke tilgang til bruker nav-enhet")
        }
    }
}

private object NasjonalTilgang : Kabac.Policy {
    private val nasjonalTilgangRoller = setOf(
        "0000-ga-gosys_nasjonal",
        "0000-ga-gosys_utvidbar_til_nasjonal",
        "0000-ga-pensjon_nasjonal_u_logg",
        "0000-ga-pensjon_nasjonal_m_logg"
    )

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()
        return if (nasjonalTilgangRoller.intersect(veilederRoller).isNotEmpty()) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.NotApplicable("Veileder hadde ikke nasjonal tilgang")
        }
    }
}

private object RegionalTilgangTilBrukersRegion : Kabac.Policy {
    private val regionalTilgangRoller = setOf(
        "0000-ga-gosys_regional",
        "0000-ga-gosys_utvidbar_til_regional",
    )

    override fun evaluate(ctx: EvaluationContext): Kabac.Decision {
        val veilederRoller = ctx.getValue(VeiledersRollerPip) ?: emptySet()
        if (regionalTilgangRoller.intersect(veilederRoller).isEmpty()) {
            return Kabac.Decision.NotApplicable("Veileder har ikke regional tilgang")
        }

        val brukersRegion: EnhetId? = ctx.getValue(BrukersRegionEnhetPip)
        val veiledersRegioner: List<EnhetId> = ctx.getValue(VeiledersRegionEnheterPip) ?: emptyList()
        return if (veiledersRegioner.contains(brukersRegion)) {
            Kabac.Decision.Permit()
        } else {
            Kabac.Decision.NotApplicable("Veileder har ikke tilgang til brukers region")
        }
    }
}

object GeografiskTilgangPolicy : Kabac.Policy by CombiningAlgorithm.firstApplicable.combine(
    listOf(
        NasjonalTilgang,
        BrukerUtenEnhet,
        TilgangTilBrukersNavKontor,
        RegionalTilgangTilBrukersRegion,
        Kabac.Policy { Kabac.Decision.Deny("Veileder har ikke tilgang til bruker basert på geografisk tilgang") }
    )
)
