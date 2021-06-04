package no.nav.modiapersonoversikt.service.ldap

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

class ADRolleParserKtTest {

    @Test
    fun parserADStrengTilRolle() {
        val rolle = parseADRolle(listOf("CN=MIN_ROLLE,OU=AccountGroups,OU=Groups,OU=NAV,OU=BusinessUnits,DC=test,DC=local"))
        assertEquals("MIN_ROLLE", rolle[0])
    }

    @Test
    @DisplayName("Kaster excepction hvis strenger har feil format")
    fun kasterFeilVedFeilFormat() {
        assertThrows(IllegalStateException::class.java) { parseADRolle(Arrays.asList("Ugyldig streng")) }
    }
}
