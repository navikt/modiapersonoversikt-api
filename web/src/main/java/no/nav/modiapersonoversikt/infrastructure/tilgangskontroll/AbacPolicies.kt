package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.auth.context.AuthContextHolderThreadLocal
import no.nav.modiapersonoversikt.consumer.abac.*
import no.nav.modiapersonoversikt.consumer.abac.NavAttributes.*
import no.nav.modiapersonoversikt.consumer.abac.StandardAttributter.ACTION_ID
import java.util.*

private fun parseOidcToken(ssoToken: String): String {
    val fragments = ssoToken.split('.')
    return if (fragments.size == 1) fragments[0] else fragments[1]
}

private fun Optional<String>.createWithTokenBody(block: Request.(token: String) -> Unit): AbacRequest {
    return this
        .map(::parseOidcToken)
        .map { token -> abacRequest { block(this, token) } }
        .orElseThrow { IllegalStateException("Fant ikke saksbehandler-token") }
}

object AbacPolicies {
    private val authcontext = AuthContextHolderThreadLocal.instance()
    fun tilgangTilModia(): AbacRequest = authcontext.idTokenString
        .createWithTokenBody { tokenBody ->
            environment {
                attribute(ENVIRONMENT_FELLES_PEP_ID, "modia")
                attribute(ENVIRONMENT_FELLES_OIDC_TOKEN_BODY, tokenBody)
            }
            resource {
                attribute(RESOURCE_FELLES_DOMENE, "modia")
                attribute(RESOURCE_FELLES_RESOURCE_TYPE, "no.nav.abac.attributter.resource.modia")
            }
        }

    fun tilgangTilBruker(fnr: String): AbacRequest = authcontext.idTokenString
        .createWithTokenBody { tokenBody ->
            environment {
                attribute(ENVIRONMENT_FELLES_PEP_ID, "modia")
                attribute(ENVIRONMENT_FELLES_OIDC_TOKEN_BODY, tokenBody)
            }
            resource {
                attribute(RESOURCE_FELLES_DOMENE, "modia")
                attribute(RESOURCE_FELLES_RESOURCE_TYPE, "no.nav.abac.attributter.resource.modia")
                attribute(RESOURCE_FELLES_PERSON_FNR, fnr)
            }
        }

    fun tilgangTilBrukerMedAktorId(aktorId: String): AbacRequest = authcontext.idTokenString
        .createWithTokenBody { tokenBody ->
            environment {
                attribute(ENVIRONMENT_FELLES_PEP_ID, "modia")
                attribute(ENVIRONMENT_FELLES_OIDC_TOKEN_BODY, tokenBody)
            }
            resource {
                attribute(RESOURCE_FELLES_DOMENE, "modia")
                attribute(RESOURCE_FELLES_RESOURCE_TYPE, "no.nav.abac.attributter.resource.modia")
                attribute(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktorId)
            }
        }

    fun kanPlukkeOppgave(): AbacRequest = authcontext.idTokenString
        .createWithTokenBody { tokenBody ->
            environment {
                attribute(ENVIRONMENT_FELLES_PEP_ID, "modia")
                attribute(ENVIRONMENT_FELLES_OIDC_TOKEN_BODY, tokenBody)
            }
            resource {
                attribute(RESOURCE_FELLES_DOMENE, "modia")
                attribute(RESOURCE_FELLES_RESOURCE_TYPE, "no.nav.abac.attributter.resource.modia.oppgave")
            }
            action {
                attribute(ACTION_ID, "read")
            }
        }
}
