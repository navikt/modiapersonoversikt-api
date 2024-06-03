package no.modiapersonoversikt.common

import no.nav.common.utils.EnvironmentUtils

object AppConstants {
    const val SYSTEMUSER_USERNAME_PROPERTY = "no.nav.modig.security.systemuser.username"
    const val SYSTEMUSER_PASSWORD_PROPERTY = "no.nav.modig.security.systemuser.password"

    val SYSTEMUSER_USERNAME: String by lazy { EnvironmentUtils.getRequiredProperty(SYSTEMUSER_USERNAME_PROPERTY) }
    val SYSTEMUSER_PASSWORD: String by lazy { EnvironmentUtils.getRequiredProperty(SYSTEMUSER_PASSWORD_PROPERTY) }
}
