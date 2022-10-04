package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.commondomain.Veileder
import javax.naming.ldap.LdapContext

interface LDAPService {
    fun hentVeileder(ident: NavIdent): Veileder
    fun hentRollerForVeileder(ident: NavIdent): List<String>
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
