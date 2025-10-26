package no.nav.modiapersonoversikt.service.azure

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.client.msgraph.AdGroupData
import no.nav.common.client.msgraph.AdGroupFilter
import no.nav.common.client.msgraph.MsGraphClient
import no.nav.common.client.msgraph.UserData
import no.nav.common.types.identer.AzureObjectId
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AzureADServiceImplTest {

    private lateinit var tokenClient: BoundedOnBehalfOfTokenClient
    private lateinit var msGraphClient: MsGraphClient
    private lateinit var azureADService: AzureADServiceImpl

    private val mockToken = "mockToken"
    private val testSubject =
        AuthContext(
            UserRole.INTERN,
            PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build()),
        )

    @BeforeEach
    fun setUp() {
        tokenClient = mock(BoundedOnBehalfOfTokenClient::class.java)
        msGraphClient = mock(MsGraphClient::class.java)
        azureADService = AzureADServiceImpl(tokenClient, msGraphClient)
        `when`(tokenClient.exchangeOnBehalfOfToken(anyString())).thenReturn(mockToken)
    }

    @Test
    fun `skal hente roller for en veileder`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("Z9999")
            val mockGroups = listOf(
                createGroupData("0000-GA-TEMA_AAP"),
                createGroupData("0000-GA-TEMA_OPP")
            )
            `when`(msGraphClient.hentAdGroupsForUser(mockToken, ident.get()))
                .thenReturn(mockGroups)
            val roles = azureADService.hentRollerForVeileder(ident)
            assertEquals(listOf("0000-GA-TEMA_AAP", "0000-GA-TEMA_OPP"), roles)
        }
    }

    @Test
    fun `skal hente roller for en veileder når ingen roller`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("Z9999")
            `when`(msGraphClient.hentAdGroupsForUser(mockToken, ident.get()))
                .thenReturn(emptyList())
            val roles = azureADService.hentRollerForVeileder(ident)
            assertTrue(roles.isEmpty())
        }
    }

    @Test
    fun `skal hente enheter for en veileder`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("Z9999")
            val mockGroups = listOf(
                createGroupData("0000-GA-ENHET_EN1"),
                createGroupData("0000-GA-ENHET_EN2")
            )
            `when`(msGraphClient.hentAdGroupsForUser(mockToken, ident.get(), AdGroupFilter.ENHET))
                .thenReturn(mockGroups)
            val enheter = azureADService.hentEnheterForVeileder(ident)
            assertEquals(
                listOf(EnhetId("EN1"), EnhetId("EN2")),
                enheter
            )
        }
    }

    @Test
    fun `skal hente temaer for en veileder`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("VEILEDER123")
            val mockGroups = listOf(
                createGroupData("0000-GA-TEMA_T1"),
                createGroupData("0000-GA-TEMA_T2")
            )
            `when`(msGraphClient.hentAdGroupsForUser(mockToken, ident.get(), AdGroupFilter.TEMA))
                .thenReturn(mockGroups)
            val temaer = azureADService.hentTemaerForVeileder(ident)
            assertEquals(
                listOf("T1", "T2"),
                temaer
            )
        }
    }

    @Test
    fun `skal hente ansatte for en enhet`() {
        AuthContextUtils.withContext(testSubject) {
            val enhetId = EnhetId("ENHET123")
            val mockUsers = listOf(
                createUserData("John", "Doe", "s12345"),
                createUserData("Jane", "Smith", "s67890")
            )
            `when`(msGraphClient.hentUserDataForGroup(mockToken, enhetId))
                .thenReturn(mockUsers)
            val ansatte = azureADService.hentAnsatteForEnhet(enhetId)

            assertEquals(
                listOf(
                    Ansatt("John", "Doe", "s12345"),
                    Ansatt("Jane", "Smith", "s67890")
                ),
                ansatte
            )
        }
    }

    @Test
    fun `skal kunne håndtere exception og returnere tom liste`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("TESTIDENT")
            `when`(msGraphClient.hentAdGroupsForUser(mockToken, ident.get()))
                .thenThrow(RuntimeException("Test Exception"))
            val roles = azureADService.hentRollerForVeileder(ident)
            assertTrue(roles.isEmpty())
        }
    }

    private fun createGroupData(displayName: String) = AdGroupData(AzureObjectId( UUID.randomUUID().toString()), displayName)
    private fun createUserData(
        givenName: String,
        surname: String,
        accountName: String
    ): UserData {
        val userData = UserData()
        userData.givenName = givenName
        userData.surname = surname
        userData.onPremisesSamAccountName = accountName

        return userData
    }
}