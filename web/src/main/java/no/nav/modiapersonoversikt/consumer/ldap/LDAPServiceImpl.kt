package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.metrics.Event
import no.nav.common.metrics.InfluxClient
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.ldap.LDAP.parseADRolle
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult
@CacheConfig(cacheNames = ["ldap"], keyGenerator = "methodawarekeygenerator")
open class LDAPServiceImpl(private val contextProvider: LDAPContextProvider) : LDAPService {
    private val searchBase = "OU=Users,OU=NAV,OU=BusinessUnits,${contextProvider.baseDN}"
    private val influxClient = InfluxClient()
    private val log = LoggerFactory.getLogger(LDAPServiceImpl::class.java)

    @Cacheable
    override fun hentRollerForVeileder(ident: NavIdent): List<String> {
        val result = search(ident).firstOrNull() ?: return emptyList()
        val memberof = result.attributes.get("memberof").all.toList()
        return memberof
            .filterIsInstance(String::class.java)
            .map(::parseADRolle)
            .also { report(ident, it) }
    }

    private fun report(ident: NavIdent, roller: List<String>) {
        try {
            val rollerLowercase = roller.map(String::lowercase)
            val harNasjonal = rollerLowercase.contains("0000-ga-gosys_nasjonal") || rollerLowercase.contains("0000-ga-gosys_utvidbar_til_nasjonal")
            val harRegional = rollerLowercase.contains("0000-ga-gosys_regional") || rollerLowercase.contains("0000-ga-gosys_utvidbar_til_regional")
            val tilgang = when {
                harNasjonal -> "nasjonal"
                harRegional -> "regional"
                else -> "lokal"
            }

            influxClient.report(
                Event("modia-tilgang")
                    .addFieldToReport("ident", ident.get().hashCode().toString(16))
                    .addTagToReport("tilgang", tilgang)
            )
        } catch (e: Exception) {
            log.warn("Kunne ikke rapportere rolle-metrikk til influx")
        }
    }

    private fun search(ident: NavIdent): Sequence<SearchResult> {
        val searchCtl = SearchControls().apply {
            searchScope = SearchControls.SUBTREE_SCOPE
        }
        return contextProvider
            .getContext()
            .search(
                searchBase,
                "(&(objectClass=user)(CN=${ident.get()}))",
                searchCtl
            ).asSequence()
    }
}
