package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.getProperty;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class LDAPServiceImpl implements LDAPService {

    private static LdapContextProvider ldapContextProvider;

    public LDAPServiceImpl(LdapContextProvider ldapContextProvider) {
        LDAPServiceImpl.ldapContextProvider = ldapContextProvider;
    }

    @Override
    public Saksbehandler hentSaksbehandler(String ident) {
        try {
            NamingEnumeration<SearchResult> result = sokLDAP(ident);

            Optional<Attribute> givenname = none();
            Optional<Attribute> surname = none();
            if (result.hasMore()) {
                Attributes attributes = result.next().getAttributes();
                givenname = optional(attributes.get("givenname"));
                surname = optional(attributes.get("sn"));
            }

            BasicAttribute nullAttribute = new BasicAttribute("", "");
            return new Saksbehandler(
                    (String) givenname.getOrElse(nullAttribute).get(),
                    (String) surname.getOrElse(nullAttribute).get(),
                    ident
            );

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private NamingEnumeration<SearchResult> sokLDAP(String ident) {
        String searchbase = "OU=Users,OU=NAV,OU=BusinessUnits," + getProperty("ldap.basedn");
        SearchControls searchCtrl = new SearchControls();
        searchCtrl.setSearchScope(SearchControls.SUBTREE_SCOPE);

        try {
            return ldapContext().search(searchbase, String.format("(&(objectClass=user)(CN=%s))", ident), searchCtrl);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private static LdapContext ldapContext() {
        return ldapContextProvider.getInitialLdapContext();
    }

    @Override
    public boolean saksbehandlerHarRolle(String ident, String rolle) {
        NamingEnumeration<SearchResult> result = sokLDAP(ident);
        return getRoller(result).contains(rolle);
    }

    private List<String> getRoller(NamingEnumeration<SearchResult> result) {
        try {
            NamingEnumeration<?> attributes = result.next().getAttributes().get("memberof").getAll();
            return parseRollerFraAD(attributes);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> parseRollerFraAD(NamingEnumeration<?> attributes) throws NamingException {
        List<String> rawRolleStrenger = new ArrayList<>();
        while (attributes.hasMore()) {
            rawRolleStrenger.add((String) attributes.next());
        }
        return  ADRolleParserKt.parseADRolle(rawRolleStrenger);
    }

}
