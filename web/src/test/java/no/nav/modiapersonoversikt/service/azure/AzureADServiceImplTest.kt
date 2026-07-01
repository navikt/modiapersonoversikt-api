package no.nav.modiapersonoversikt.service.azure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
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
import no.nav.modiapersonoversikt.utils.TestUtils
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AzureADServiceImplTest {
    private val tokenClient = mockk<BoundedOnBehalfOfTokenClient>()
    private val msGraphClient = mockk<MsGraphClient>()
    private val httpClient = mockk<OkHttpClient>()
    private val mockCall = mockk<Call>()
    private val mockResponse = mockk<Response>()
    private val mockResponseBody = mockk<ResponseBody>()
    private val objectMapper = jacksonObjectMapper()

    private val mockToken = "mockToken"
    private val mockUserId = "azure-user-id-123"
    private val testSubject =
        AuthContext(
            UserRole.INTERN,
            PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build()),
        )

    private lateinit var azureADService: AzureADServiceImpl

    @BeforeEach
    fun setUp() {
        every { tokenClient.exchangeOnBehalfOfToken(any()) } returns mockToken
        every { httpClient.newCall(any()) } returns mockCall
        every { mockCall.execute() } returns mockResponse
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body } returns mockResponseBody
        every { mockResponse.close() } just Runs

        azureADService =
            AzureADServiceImpl(tokenClient, msGraphClient, "https://graph.microsoft.com/v1.0", httpClient, objectMapper)
    }

    @Test
    fun `skal hente roller for en veileder`() {
        withTestGruppeIder {
            AuthContextUtils.withContext(testSubject) {
                val ident = NavIdent("Z9999")
                every { msGraphClient.hentAzureIdMedNavIdent(mockToken, ident.get()) } returns mockUserId
                every { mockResponseBody.string() } returns
                        objectMapper.writeValueAsString(
                            mapOf(
                                "value" to
                                        listOf(
                                            "uuid-modia-generell",
                                            "uuid-modia-oppfolging",
                                        ),
                            ),
                        )
                val roles = azureADService.hentRollerForVeileder(ident)
                assertEquals(listOf("uuid-modia-generell", "uuid-modia-oppfolging"), roles)
            }
        }
    }

    @Test
    fun `skal hente roller for en veileder når ingen roller`() {
        withTestGruppeIder {
            AuthContextUtils.withContext(testSubject) {
                val ident = NavIdent("Z9999")
                every { msGraphClient.hentAzureIdMedNavIdent(mockToken, ident.get()) } returns mockUserId
                every { mockResponseBody.string() } returns objectMapper.writeValueAsString(mapOf("value" to emptyList<String>()))
                val roles = azureADService.hentRollerForVeileder(ident)
                assertTrue(roles.isEmpty())
            }
        }
    }

    @Test
    fun `skal hente enheter for en veileder`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("Z9999")
            val mockGroups =
                listOf(
                    createGroupData("0000-GA-ENHET_EN1"),
                    createGroupData("0000-GA-ENHET_EN2"),
                )
            every { msGraphClient.hentAdGroupsForUser(mockToken, ident.get(), AdGroupFilter.ENHET) } returns mockGroups
            val enheter = azureADService.hentEnheterForVeileder(ident)
            assertEquals(listOf(EnhetId("EN1"), EnhetId("EN2")), enheter)
        }
    }

    @Test
    fun `skal hente temaer for en veileder`() {
        AuthContextUtils.withContext(testSubject) {
            val ident = NavIdent("VEILEDER123")
            val mockGroups =
                listOf(
                    createGroupData("0000-GA-TEMA_T1"),
                    createGroupData("0000-GA-TEMA_T2"),
                )
            every { msGraphClient.hentAdGroupsForUser(mockToken, ident.get(), AdGroupFilter.TEMA) } returns mockGroups
            val temaer = azureADService.hentTemaerForVeileder(ident)
            assertEquals(listOf("T1", "T2"), temaer)
        }
    }

    @Test
    fun `skal hente ansatte for en enhet`() {
        AuthContextUtils.withContext(testSubject) {
            val enhetId = EnhetId("ENHET123")
            val mockUsers =
                listOf(
                    createUserData("John", "Doe", "s12345"),
                    createUserData("Jane", "Smith", "s67890"),
                )
            every { msGraphClient.hentUserDataForGroup(mockToken, enhetId) } returns mockUsers
            val ansatte = azureADService.hentAnsatteForEnhet(enhetId)
            assertEquals(
                listOf(
                    Ansatt("John", "Doe", "s12345"),
                    Ansatt("Jane", "Smith", "s67890"),
                ),
                ansatte,
            )
        }
    }

    @Test
    fun `skal kunne håndtere exception og returnere tom liste`() {
        withTestGruppeIder {
            AuthContextUtils.withContext(testSubject) {
                val ident = NavIdent("TESTIDENT")
                every {
                    msGraphClient.hentAzureIdMedNavIdent(
                        mockToken,
                        ident.get(),
                    )
                } throws RuntimeException("Test Exception")
                val roles = azureADService.hentRollerForVeileder(ident)
                assertTrue(roles.isEmpty())
            }
        }
    }

    private fun withTestGruppeIder(fn: TestUtils.UnsafeRunneable) {
        fun withProp(name: String, value: String, inner: () -> Unit) {
            val original = System.getProperty(name)
            System.setProperty(name, value)
            try {
                inner()
            } finally {
                if (original == null) {
                    System.clearProperty(name)
                } else {
                    System.setProperty(name, original)
                }
            }
        }
        withProp("MODIA_GENERELL_TILGANG_ID", "uuid-modia-generell") {
            withProp("MODIA_OPPFOLGING_ID", "uuid-modia-oppfolging") {
                withProp("SYFO_SENSITIV_ID", "uuid-syfo-sensitiv") {
                    withProp("STRENGT_FORTROLIG_ADRESSE_ID", "uuid-strengt-fortrolig") {
                        withProp("FORTROLIG_ADRESSE_ID", "uuid-fortrolig") {
                            withProp("EGNE_ANSATTE_ID", "uuid-egne-ansatte") {
                                fn.call()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createGroupData(displayName: String) =
        AdGroupData(AzureObjectId(UUID.randomUUID().toString()), displayName)

    private fun createUserData(
        givenName: String,
        surname: String,
        accountName: String,
    ): UserData {
        val userData = UserData()
        userData.givenName = givenName
        userData.surname = surname
        userData.onPremisesSamAccountName = accountName
        return userData
    }
}
