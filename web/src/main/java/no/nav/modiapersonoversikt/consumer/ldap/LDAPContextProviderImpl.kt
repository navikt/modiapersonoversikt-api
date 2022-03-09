package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.utils.EnvironmentUtils
import java.util.*
import javax.naming.Context
import javax.naming.ldap.InitialLdapContext
import javax.naming.ldap.LdapContext

class LDAPContextProviderImpl : LDAPContextProvider {
    private val ldapEnvironment = Hashtable(
        mutableMapOf(
            Context.INITIAL_CONTEXT_FACTORY to "com.sun.jndi.ldap.LdapCtxFactory",
            Context.SECURITY_AUTHENTICATION to "simple",
            Context.PROVIDER_URL to EnvironmentUtils.getRequiredProperty("LDAP_URL"),
            Context.SECURITY_PRINCIPAL to EnvironmentUtils.getRequiredProperty(LDAP.USERNAME),
            Context.SECURITY_CREDENTIALS to EnvironmentUtils.getRequiredProperty(LDAP.PASSWORD),
        )
    )

    override val context: LdapContext = InitialLdapContext(ldapEnvironment, null)
}
