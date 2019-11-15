package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.ldap;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LDAPServiceImplTest {

    public static final String SAKSBEHANDLER_FORNAVN = "John";
    public static final String IDENT = "IDENT";
    public static final String ENDRE_NAVN_ROLLE = "0000-GA-BD06_EndreNavn";
    public static final String MODIA_ROLLE = "0000-GA-BD06_ModiaGenerellTilgang";

    @BeforeAll
    static void beforeAll() {
        System.setProperty("LDAP_BASEDN", "basedn");
        System.setProperty("LDAP_URL", "url");
        System.setProperty("LDAP_USERNAME", "username");
        System.setProperty("LDAP_PASSWORD", "password");
    }

    private LdapContextProvider mockLdapContextProvider(List<String> roller) {
        LdapContextProvider ldapContextProvider = mock(LdapContextProvider.class);
        LdapContext context = mockLdapContext(roller);
        when(ldapContextProvider.getInitialLdapContext()).thenReturn(context);
        return ldapContextProvider;
    }

    private LdapContext mockLdapContext(List<String> roller) {
        LdapContext ldapContext = mock(LdapContext.class);
        try {
            NamingEnumeration<SearchResult> result = mockResult(roller);
            when(ldapContext.search(anyString(), anyString(), any())).thenReturn(result);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
        return ldapContext;
    }

    private NamingEnumeration<SearchResult> mockResult(List<String> roller) throws NamingException {
        NamingEnumeration<SearchResult> result = mock(NamingEnumeration.class);
        when(result.hasMore()).thenReturn(true);
        when(result.next()).thenReturn(new SearchResult(null, null, mockAttributes(roller)));
        return result;
    }

    private BasicAttributes mockAttributes(List<String> roller) {
        BasicAttributes basicAttributes = new BasicAttributes();
        basicAttributes.put(new BasicAttribute("givenname", SAKSBEHANDLER_FORNAVN));
        basicAttributes.put(new BasicAttribute("sn", "Nordmann"));
        BasicAttribute rolleAttributt = new BasicAttribute("memberof");
        for (String rolle: roller ) {
            rolleAttributt.add(lagRawADRolleStreng(rolle));
        }
        basicAttributes.put(rolleAttributt);
        return basicAttributes;
    }

    private String lagRawADRolleStreng(String rolle) {
        return String.format("CN=%s,OU=AccountGroups,OU=Groups,OU=NAV,OU=BusinessUnits,DC=test,DC=local", rolle);
    }

    @Nested
    class HentSaksbehandler {

        @Test
        @DisplayName("Henter saksbehandler fra AD")
        void henterSaksbehandler() {
            LdapContextProvider ldapContextProvider = mockLdapContextProvider(Collections.EMPTY_LIST);
            LDAPService ldapService = new LDAPServiceImpl(ldapContextProvider);

            Saksbehandler saksbehandler = ldapService.hentSaksbehandler("123");

            assertEquals(SAKSBEHANDLER_FORNAVN, saksbehandler.fornavn);
        }
    }

    @Nested
    class Roller {
        @Test
        @DisplayName("Returnerer true om rollen finnes på ident")
        void saksbehandlerHarRolle() {
            LdapContextProvider ldapContextProvider = mockLdapContextProvider(Collections.singletonList(ENDRE_NAVN_ROLLE));
            LDAPService ldapService = new LDAPServiceImpl(ldapContextProvider);

            boolean saksbehandlerHarRolle = ldapService.saksbehandlerHarRolle(IDENT, ENDRE_NAVN_ROLLE);

            assertEquals(true, saksbehandlerHarRolle);
        }

        @Test
        @DisplayName("Returnerer false om rollen ikke finnes på ident")
        void saksbehandlerHarIkkeRolle() {
            LdapContextProvider ldapContextProvider = mockLdapContextProvider(Collections.singletonList("Annen rolle"));
            LDAPService ldapService = new LDAPServiceImpl(ldapContextProvider);

            boolean saksbehandlerHarRolle = ldapService.saksbehandlerHarRolle(IDENT, ENDRE_NAVN_ROLLE);

            assertEquals(false, saksbehandlerHarRolle);
        }

        @Test
        @DisplayName("Henter liste med roller for saksbehandler")
        void henterRollerForSaksbehandler() {
            List<String> roller = new ArrayList<>();
            roller.add(ENDRE_NAVN_ROLLE);
            roller.add(MODIA_ROLLE);

            LdapContextProvider ldapContextProvider = mockLdapContextProvider(roller);
            LDAPService ldapService = new LDAPServiceImpl(ldapContextProvider);

            List<String> result = ldapService.hentRollerForVeileder(IDENT);

            assertEquals(2, result.size());
            assertEquals(true, roller.contains(ENDRE_NAVN_ROLLE));
            assertEquals(true, roller.contains(MODIA_ROLLE));

        }
    }

}