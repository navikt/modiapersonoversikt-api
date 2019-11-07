package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class LDAPServiceImpl implements LDAPService {

    private static LdapContextProvider ldapContextProvider;

    public LDAPServiceImpl(LdapContextProvider ldapContextProvider) {
        LDAPServiceImpl.ldapContextProvider = ldapContextProvider;
    }

    @Override
    public Saksbehandler hentSaksbehandler(String ident) {
        try {
            NamingEnumeration<SearchResult> result = sokLDAP(ident);

            Optional<Attribute> givenname = empty();
            Optional<Attribute> surname = empty();
            if (result.hasMore()) {
                Attributes attributes = result.next().getAttributes();
                givenname = ofNullable(attributes.get("givenname"));
                surname = ofNullable(attributes.get("sn"));
            }

            BasicAttribute nullAttribute = new BasicAttribute("", "");
            return new Saksbehandler(
                    (String) givenname.orElse(nullAttribute).get(),
                    (String) surname.orElse(nullAttribute).get(),
                    ident
            );

        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> hentRollerForVeileder(String ident) {
        NamingEnumeration<SearchResult> result = sokLDAP(ident);
        return getRoller(result);
    }

    private NamingEnumeration<SearchResult> sokLDAP(String ident) {
        String searchbase = "OU=Users,OU=NAV,OU=BusinessUnits," + System.getProperty("LDAP_BASEDN");
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
