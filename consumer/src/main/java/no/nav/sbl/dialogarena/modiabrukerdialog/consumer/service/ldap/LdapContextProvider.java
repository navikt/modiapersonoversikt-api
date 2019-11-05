package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.sbl.util.EnvironmentUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

public class LdapContextProvider {

    @SuppressWarnings("PMD")
    private static Hashtable<String, String> env = new Hashtable<>();

    static {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, EnvironmentUtils.getRequiredProperty("ldap.url", "LDAP_URL"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, EnvironmentUtils.getRequiredProperty("ldap.username", "LDAP_USERNAME"));
        env.put(Context.SECURITY_CREDENTIALS, EnvironmentUtils.getRequiredProperty("ldap.password", "LDAP_PASSWORD"));
    }

    public LdapContext getInitialLdapContext() {
        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
