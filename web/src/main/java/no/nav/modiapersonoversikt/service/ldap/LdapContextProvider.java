package no.nav.modiapersonoversikt.service.ldap;

import no.nav.common.utils.EnvironmentUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

public class LdapContextProvider {

    @SuppressWarnings("PMD")
    private static Hashtable<String, String> env = new Hashtable<>();

    public static final String LDAP_USERNAME = "LDAP_USERNAME";
    public static final String LDAP_PASSWORD = "LDAP_PASSWORD";

    static {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.PROVIDER_URL, EnvironmentUtils.getRequiredProperty("LDAP_URL"));
        env.put(Context.SECURITY_PRINCIPAL, EnvironmentUtils.getRequiredProperty(LDAP_USERNAME));
        env.put(Context.SECURITY_CREDENTIALS, EnvironmentUtils.getRequiredProperty(LDAP_PASSWORD));
    }

    public LdapContext getInitialLdapContext() {
        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
