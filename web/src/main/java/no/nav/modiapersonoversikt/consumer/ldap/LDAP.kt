package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import javax.naming.ldap.LdapContext

interface LDAPService {
    fun hentVeileder(ident: NavIdent): Saksbehandler
    fun hentRollerForVeileder(ident: NavIdent): List<String>

    @Deprecated(
        message = "Bruk hentVeileder(NavIdent)",
        replaceWith = ReplaceWith(
            expression = "hentVeileder(NavIdent(ident))",
            imports = ["no.nav.common.types.identer.NavIdent"]
        )
    )
    fun hentSaksbehandler(ident: String): Saksbehandler = hentVeileder(NavIdent(ident))

    @Deprecated(
        message = "Bruk hentRollerForVeileder(NavIdent)",
        replaceWith = ReplaceWith(
            expression = "hentRollerForVeileder(NavIdent(ident))",
            imports = ["no.nav.common.types.identer.NavIdent"]
        )
    )
    fun hentRollerForVeileder(ident: String): List<String> = hentRollerForVeileder(NavIdent(ident))
}

interface LDAPContextProvider {
    fun getContext(): LdapContext
    val baseDN: String
        get() = EnvironmentUtils.getRequiredProperty("LDAP_BASEDN")
}

object LDAP {
    const val USERNAME = "LDAP_USERNAME"
    const val PASSWORD = "LDAP_PASSWORD"

    fun parseADRolle(raw: String): String {
        check(raw.startsWith("CN=")) {
            "Feil format på AD-rolle: $raw"
        }
        return raw.split(",")[0].split("CN=")[1]
    }
}
