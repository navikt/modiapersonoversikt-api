package no.nav.modiapersonoversikt.service.azure

import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.infrastructure.http.getCallId
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable

interface AzureADService {
    fun hentRollerForVeileder(veilederIdent: NavIdent): List<String>

    fun hentAnsatteForEnhet(
        enhetId: String,
        gruppeId: String,
    ): List<Ansatt>

    fun hentEnhetGruppe(enhetId: String): Gruppe?
}

@CacheConfig(cacheNames = ["azureAdCache"], keyGenerator = "userkeygenerator")
open class AzureADServiceImpl(
    private val tokenClient: BoundedOnBehalfOfTokenClient,
    private val graphUrl: Url,
    private val httpClient: OkHttpClient,
) : AzureADService {
    private val json = Json { ignoreUnknownKeys = true }
    private val log = LoggerFactory.getLogger(AzureADServiceImpl::class.java)

    @Cacheable(unless = "#result.size()==0")
    override fun hentRollerForVeileder(veilederIdent: NavIdent): List<String> {
        val userToken = AuthContextUtils.requireToken()
        val url =
            URLBuilder(graphUrl)
                .apply {
                    path("v1.0/me/memberOf/microsoft.graph.group")
                    parameters.append("\$count", "true")
                    parameters.append("\$top", "500")
                    parameters.append("\$select", "displayName")
                }.buildString()

        return try {
            runBlocking {
                val response = handleRequest(url, userToken)
                if (response.value.isEmpty()) {
                    log.warn("Bruker $veilederIdent har ingen AzureAD group")
                }
                response.value.map {
                    requireNotNull(it.displayName)
                }
            }
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet for veileder: $veilederIdent", e)
            return listOf()
        }
    }

    @Cacheable(unless = "#result.size()==0")
    override fun hentAnsatteForEnhet(
        enhetId: String,
        gruppeId: String,
    ): List<Ansatt> {
        val userToken = AuthContextUtils.requireToken()
        val url =
            URLBuilder(graphUrl)
                .apply {
                    path("v1.0/groups/$gruppeId/members")
                    parameters.append("\$count", "true")
                    parameters.append("\$accountEnabled", "true")
                    parameters.append("\$select", "givenName,surname,onPremisesSamAccountName")
                }.buildString()

        return try {
            runBlocking {
                val response = handleRequest(url, userToken)
                val brukereUtenIdent = response.value.filter { it.onPremisesSamAccountName == null }

                if (brukereUtenIdent.isNotEmpty()) {
                    log.warn("Det finnes: ${brukereUtenIdent.size} brukere uten ident i gruppe for enhet $enhetId. CallId=${getCallId()}")
                }

                response.value
                    .mapNotNull { user ->
                        user.onPremisesSamAccountName?.let { accountName ->
                            Ansatt(
                                user.givenName ?: "",
                                user.surname ?: "",
                                accountName,
                            )
                        }
                    }
            }
        } catch (e: Exception) {
            log.error("Azure AD: Feil ved henting av alle ansatte med gruppe id for enhet $enhetId", e)
            return listOf()
        }
    }

    @Cacheable
    override fun hentEnhetGruppe(enhetId: String): Gruppe? {
        val userToken = AuthContextUtils.requireToken()
        val url =
            URLBuilder(graphUrl)
                .apply {
                    path("v1.0/groups")
                    parameters.append("\$count", "true")
                    parameters.append("\$filter", "displayName eq '${displayName(enhetId)}'")
                    parameters.append("\$select", "displayName,id")
                }.buildString()

        return try {
            runBlocking {
                val response = handleRequest(url, userToken)
                response.value.first().let {
                    Gruppe(
                        requireNotNull(it.displayName),
                        requireNotNull(it.id),
                    )
                }
            }
        } catch (e: Exception) {
            log.error("Azure AD: Feil ved henting av gruppe for enhet $enhetId", e)
            return null
        }
    }

    private fun handleRequest(
        url: String,
        userToken: String,
    ): AzureAdResponse {
        val token = tokenClient.exchangeOnBehalfOfToken(userToken)
        val response: Response =
            httpClient
                .newCall(
                    Request
                        .Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer $token")
                        .addHeader("ConsistencyLevel", "eventual")
                        .build(),
                ).execute()

        val body =
            response.body
                ?: throw IllegalArgumentException("Mottok ingen response fra MS Graph. Body var null")

        if (!response.isSuccessful) {
            throw java.lang.IllegalArgumentException(
                "Feil i kall til MS Graph. Body var ${
                    json.decodeFromString<AzureErrorResponse>(body.string())
                }",
            )
        }

        return json.decodeFromString(
            AzureAdResponse.serializer(),
            body.string(),
        )
    }

    private fun displayName(enhetId: String): String = "0000-GA-ENHET_$enhetId"
}

data class Gruppe(
    val gruppeNavn: String,
    val gruppeId: String,
)

@Serializable
data class AzureAdResponse(
    @SerialName("@odata.count")
    val count: Int,
    val value: List<AzureGroupResponse>,
)

@Serializable
private data class AzureErrorResponse(
    val error: NestedAzureErrorResponse,
)

@Serializable
private data class NestedAzureErrorResponse(
    val code: String,
    val message: String,
)

@Serializable
data class AzureGroupResponse(
    val id: String? = null,
    val deletedDateTime: String? = null,
    val classification: String? = null,
    val createdDateTime: String? = null,
    val creationOptions: List<String> = listOf(),
    val description: String? = null,
    val displayName: String? = null,
    val expirationDateTime: String? = null,
    val groupTypes: List<String> = listOf(),
    val isAssignableToRole: Boolean? = null,
    val mail: String? = null,
    val mailEnabled: Boolean? = null,
    val mailNickname: String? = null,
    val membershipRule: String? = null,
    val membershipRuleProcessingState: String? = null,
    val onPremisesDomainName: String? = null,
    val onPremisesLastSyncDateTime: String? = null,
    val onPremisesNetBiosName: String? = null,
    val onPremisesSamAccountName: String? = null,
    val givenName: String? = null,
    val surname: String? = null,
    val onPremisesSecurityIdentifier: String? = null,
    val onPremisesSyncEnabled: Boolean? = null,
    val preferredDataLocation: String? = null,
    val preferredLanguage: String? = null,
    val proxyAddresses: List<String> = listOf(),
    val renewedDateTime: String? = null,
    val resourceBehaviorOptions: List<String> = listOf(),
    val resourceProvisioningOptions: List<String> = listOf(),
    val securityEnabled: Boolean? = null,
    val securityIdentifier: String? = null,
    val theme: String? = null,
    val visibility: String? = null,
    val onPremisesProvisioningErrors: List<String> = listOf(),
)
