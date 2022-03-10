package no.nav.modiapersonoversikt.consumer.ldap

import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.ldap.LDAP.parseADRolle
import no.nav.modiapersonoversikt.legacy.api.domain.Saksbehandler
import javax.naming.directory.SearchControls
import javax.naming.directory.SearchResult

class LDAPServiceImpl(contextProvider: LDAPContextProvider) : LDAPService {
    private val searchBase = "OU=Users,OU=NAV,OU=BusinessUnits,${contextProvider.baseDN}"
    private val context = contextProvider.context

    override fun hentVeileder(ident: NavIdent): Saksbehandler {
        val result = search(ident).firstOrNull()
            ?: return Saksbehandler("", "", ident.get())

        return Saksbehandler(
            result.attributes.get("givenname").get() as String,
            result.attributes.get("sn").get() as String,
            ident.get()
        )
    }

    override fun hentRollerForVeileder(ident: NavIdent): List<String> {
        val result = search(ident).firstOrNull() ?: return emptyList()
        val memberof = result.attributes.get("memberof").all.toList()
        return memberof
            .filterIsInstance(String::class.java)
            .map(::parseADRolle)
    }

    private fun search(ident: NavIdent): Sequence<SearchResult> {
        val searchCtl = SearchControls().apply {
            searchScope = SearchControls.SUBTREE_SCOPE
        }
        return context.search(
            searchBase,
            "(&(objectClass=user)(CN=${ident.get()}))",
            searchCtl
        ).asSequence()
    }
}
