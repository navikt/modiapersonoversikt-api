package no.nav.modiapersonoversikt.service.azure

import no.nav.common.utils.EnvironmentUtils

object AdGruppeConfig {
    val modiaGenerellTilgang: String
        get() = EnvironmentUtils.getRequiredProperty("MODIA_GENERELL_TILGANG_ID")

    val modiaOppfolging: String
        get() = EnvironmentUtils.getRequiredProperty("MODIA_OPPFOLGING_ID")

    val syfoSensitiv: String
        get() = EnvironmentUtils.getRequiredProperty("SYFO_SENSITIV_ID")

    val strengtFortroligAdresse: String
        get() = EnvironmentUtils.getRequiredProperty("STRENGT_FORTROLIG_ADRESSE_ID")

    val fortroligAdresse: String
        get() = EnvironmentUtils.getRequiredProperty("FORTROLIG_ADRESSE_ID")

    val egneAnsatte: String
        get() = EnvironmentUtils.getRequiredProperty("EGNE_ANSATTE_ID")

    val alleModiaTilgangsGrupper: List<String>
        get() =
            listOf(
                modiaGenerellTilgang,
                modiaOppfolging,
                syfoSensitiv,
                strengtFortroligAdresse,
                fortroligAdresse,
                egneAnsatte,
            )
}
