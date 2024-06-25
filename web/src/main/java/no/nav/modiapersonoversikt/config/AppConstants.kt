package no.nav.modiapersonoversikt.config

import no.nav.common.utils.EnvironmentUtils

object AppConstants {
    const val SYSTEMUSER_USERNAME_PROPERTY = "no.nav.modig.security.systemuser.username"
    const val SYSTEMUSER_PASSWORD_PROPERTY = "no.nav.modig.security.systemuser.password"

    val APP_NAME: String by lazy { EnvironmentUtils.getRequiredProperty("NAIS_APP_NAME") }
}
