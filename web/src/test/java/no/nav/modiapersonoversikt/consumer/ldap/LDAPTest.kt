package no.nav.modiapersonoversikt.consumer.ldap

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.NavIdent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.naming.NamingEnumeration
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.SearchResult
import javax.naming.ldap.LdapContext

class LDAPTest {
    @Test
    fun `henter veileder`() {
        val ldap = LDAPServiceImpl(createLDAPContext())
        val veileder = ldap.hentVeileder(NavIdent("123"))

        assertThat(veileder.ident).isEqualTo("123")
        assertThat(veileder.fornavn).isEqualTo("Fornavn")
        assertThat(veileder.etternavn).isEqualTo("Etternavn")
    }

    @Test
    fun `henter veileders roller`() {
        val ldap = LDAPServiceImpl(
            createLDAPContext(
                "0000-GA-Test-Rolle",
                "0000-GA-Annen-Rolle",
            )
        )
        val roller = ldap.hentRollerForVeileder(NavIdent("123"))

        assertThat(roller)
            .hasSize(2)
            .contains(
                "0000-GA-Test-Rolle",
                "0000-GA-Annen-Rolle",
            )
    }

    @Test
    fun parserADStrengTilRolle() {
        assertThat(
            LDAP.parseADRolle("CN=MIN_ROLLE,OU=AccountGroups,OU=Groups,OU=NAV,OU=BusinessUnits,DC=test,DC=local")
        ).isEqualTo("MIN_ROLLE")
    }

    @Test
    fun `kaster exception hvis strenger har feil format`() {
        assertThrows<IllegalStateException> {
            LDAP.parseADRolle("Ugyldig streng")
        }
    }

    private fun createLDAPContext(vararg roller: String): LDAPContextProvider {
        val context = mockk<LdapContext>()
        val result = mockk<NamingEnumeration<SearchResult>>()

        every { context.search(any<String>(), any<String>(), any()) } returns result
        every { result.hasMoreElements() } returns true
        every { result.nextElement() } returns
            SearchResult(
                null,
                null,
                BasicAttributes()
                    .medNavn("Fornavn", "Etternavn")
                    .medRoller(roller)
            )

        return object : LDAPContextProvider {
            override val context: LdapContext = context
            override val baseDN: String get() = ""
        }
    }

    private fun BasicAttributes.medNavn(fornavn: String, etternavn: String) =
        this.apply {
            put(BasicAttribute("givenname", fornavn))
            put(BasicAttribute("sn", etternavn))
        }

    private fun BasicAttributes.medRoller(roller: Array<out String>) =
        this.apply {
            BasicAttribute("memberof")
                .apply {
                    for (rolle in roller) {
                        add("CN=$rolle,OU=AccountGroups,OU=Groups,OU=NAV,OU=BusinessUnits,DC=test,DC=local")
                    }
                }
                .also(this::put)
        }
}
