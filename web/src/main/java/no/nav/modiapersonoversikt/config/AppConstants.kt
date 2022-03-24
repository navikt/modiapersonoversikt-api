package no.nav.modiapersonoversikt.config

import no.nav.common.utils.EnvironmentUtils

object AppConstants {
    @JvmField
    val SYSTEMUSER_USERNAME_PROPERTY = "no.nav.modig.security.systemuser.username"

    @JvmField
    val SYSTEMUSER_PASSWORD_PROPERTY = "no.nav.modig.security.systemuser.password"

    val SYSTEMUSER_USERNAME by lazy { EnvironmentUtils.getRequiredProperty(SYSTEMUSER_USERNAME_PROPERTY) }
    val SYSTEMUSER_PASSWORD by lazy { EnvironmentUtils.getRequiredProperty(SYSTEMUSER_PASSWORD_PROPERTY) }
}
