package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.sbl.util.EnvironmentUtils;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

public class LdapContextProvider {

    @SuppressWarnings("PMD")
    private static Hashtable<String, String> env = new Hashtable<>();

    static {
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, getRequiredProperty("ldap.url"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, getRequiredProperty("ldap.username"));
        env.put(Context.SECURITY_CREDENTIALS, getRequiredProperty("ldap.password"));
    }

    public LdapContext getInitialLdapContext() {
        try {
            return new InitialLdapContext(env, null);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
