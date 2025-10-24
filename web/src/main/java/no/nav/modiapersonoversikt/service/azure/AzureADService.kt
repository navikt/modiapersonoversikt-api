package no.nav.modiapersonoversikt.service.azure

import no.nav.common.client.msgraph.AdGroupFilter
import no.nav.common.client.msgraph.MsGraphClient
import no.nav.common.types.identer.EnhetId
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils
import no.nav.modiapersonoversikt.service.ansattservice.domain.Ansatt
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
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
) : AzureADService {
    private val log = LoggerFactory.getLogger(AzureADServiceImpl::class.java)
    private val temaRolePrefix = "0000-GA-TEMA_"
    private val enhetRolePrefix = "0000-GA-ENHET_"

    override fun hentRollerForVeileder(ident: NavIdent): List<String> {
        val token = tokenClient.exchangeOnBehalfOfToken(AuthContextUtils.requireToken())
        return try {
            val response = msGraphClient.hentAdGroupsForUser(token, ident.get())
            if (response.isEmpty()) {
                log.warn("Bruker $ident har ingen AzureAD group")
            }
            response.map {
                requireNotNull(it.displayName)
            }
        } catch (e: Exception) {
            log.error("Kall til azureAD feilet", ident, e)
            return listOf()
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
