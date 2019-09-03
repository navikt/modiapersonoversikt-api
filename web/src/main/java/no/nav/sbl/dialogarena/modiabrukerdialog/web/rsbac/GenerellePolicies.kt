package no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac

class GenerellContext(
        val diskresjonskode: String? = null,
        val roller: List<String> = emptyList()
)

private val modiaRoller = setOf("0000-ga-bd06_modiagenerelltilgang", "0000-ga-modia-oppfolging", "0000-ga-syfo-sensitiv")
private val modiaRolle = Policy<GenerellContext>("Saksbehandler har ikke tilgang til modia") {
    if (it.roller.any(modiaRoller::contains))
        DecisionEnums.PERMIT
    else
        DecisionEnums.DENY
}

private val TILGANG_TIL_BRUKER_KODE_6: Policy<GenerellContext> = Policy("Saksbehandler har ikke tilgang til kode6 brukere") {
    if (it.diskresjonskode == "6") {
        if (it.roller.contains("0000-GA-GOSYS_KODE6"))
            DecisionEnums.PERMIT
        else
            DecisionEnums.DENY
    }
    DecisionEnums.NOT_APPLICABLE
}

private val TILGANG_TIL_BRUKER_KODE_7: Policy<GenerellContext> = Policy("Saksbehandler har ikke tilgang til kode7 brukere") {
    if (it.diskresjonskode == "7") {
        if (it.roller.contains("0000-GA-GOSYS_KODE7"))
            DecisionEnums.PERMIT
        else
            DecisionEnums.DENY
    }
    DecisionEnums.NOT_APPLICABLE
}

class GenerellePolicies {
    companion object {
        @JvmField
        val tilgangTilModia = modiaRolle
        @JvmField
        val tilgangTilBruker = PolicySet(
                policies = listOf(modiaRolle, TILGANG_TIL_BRUKER_KODE_6, TILGANG_TIL_BRUKER_KODE_7)
        )
    }
}