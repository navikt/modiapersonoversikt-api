package no.nav.modiapersonoversikt.service.azure

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.common.client.msgraph.AdGroupFilter
import no.nav.common.client.msgraph.MsGraphClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory

interface AzureADService {
    fun hentRollerForVeileder(ident: NavIdent): List<String>

    fun hentEnheterForVeileder(ident: NavIdent): List<EnhetId>

    fun hentTemaerForVeileder(ident: NavIdent): List<String>

    fun hentAnsatteForEnhet(enhet: EnhetId): List<Ansatt>
}

open class AzureADServiceImpl(
    private val tokenClient: BoundedOnBehalfOfTokenClient,
    private val msGraphClient: MsGraphClient,
    private val msGraphUrl: String,
    private val httpClient: OkHttpClient,
    private val objectMapper: ObjectMapper,
) : AzureADService {
    private val log = LoggerFactory.getLogger(AzureADServiceImpl::class.java)
    private val temaRolePrefix = "0000-GA-TEMA_"
    private val enhetRolePrefix = "0000-GA-ENHET_"

    private data class CheckMemberObjectsResponse(val value: List<String>)

    override fun hentRollerForVeileder(ident: NavIdent): List<String> {
        val token = tokenClient.exchangeOnBehalfOfToken(AuthContextUtils.requireToken())
        try {
            val userId = msGraphClient.hentAzureIdMedNavIdent(token, ident.get())
            val modiaTilganger = checkMemberObjects(token, userId, AdGruppeConfig.alleModiaTilgangsGrupper)

            if (modiaTilganger.isEmpty()) {
                log.warn("Bruker $ident er ikke medlem av noen tilgangsgrupper")
            }

            return modiaTilganger
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet", ident, e)
            return listOf()
        }
    }

    private fun checkMemberObjects(
        token: String,
        userId: String,
        groupIds: List<String>,
    ): List<String> {
        val body = objectMapper.writeValueAsString(mapOf("ids" to groupIds))
        val request =
            Request
                .Builder()
                .url("$msGraphUrl/users/$userId/checkMemberObjects")
                .header("Authorization", "Bearer $token")
                .post(body.toRequestBody("application/json".toMediaTypeOrNull()))
                .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Kall til azureAD feilet med HTTP ${response.code}")
            }
            return objectMapper.readValue(response.body?.string(), CheckMemberObjectsResponse::class.java).value
        }
    }

    override fun hentEnheterForVeileder(ident: NavIdent): List<EnhetId> {
        val token = tokenClient.exchangeOnBehalfOfToken(AuthContextUtils.requireToken())
        return try {
            val response = msGraphClient.hentAdGroupsForUser(token, ident.get(), AdGroupFilter.ENHET)
            if (response.isEmpty()) {
                log.warn("Bruker $ident har ingen AzureAD group for enhet")
            }
            response.map {
                requireNotNull(EnhetId(it.displayName.removePrefix(enhetRolePrefix)))
            }
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet", ident, e)
            return listOf()
        }
    }

    override fun hentTemaerForVeileder(ident: NavIdent): List<String> {
        val token = tokenClient.exchangeOnBehalfOfToken(AuthContextUtils.requireToken())
        return try {
            val response = msGraphClient.hentAdGroupsForUser(token, ident.get(), AdGroupFilter.TEMA)
            if (response.isEmpty()) {
                log.warn("Bruker $ident har ingen AzureAD group for tema")
            }
            response.map {
                requireNotNull(it.displayName.removePrefix(temaRolePrefix))
            }
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet", ident, e)
            return listOf()
        }
    }

    override fun hentAnsatteForEnhet(enhetId: EnhetId): List<Ansatt> {
        val token = tokenClient.exchangeOnBehalfOfToken(AuthContextUtils.requireToken())
        return try {
            msGraphClient.hentUserDataForGroup(token, enhetId).map {
                Ansatt(
                    it.givenName,
                    it.surname,
                    it.onPremisesSamAccountName,
                )
            }
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet", enhetId, e)
            return listOf()
        }
    }
}
