package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.types.identer.NavIdent
import no.nav.common.utils.EnvironmentUtils
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import javax.naming.ldap.LdapContext

@CacheConfig(cacheNames = ["ldap"], keyGenerator = "methodawarekeygenerator")
interface LDAPService {
    @Cacheable
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
