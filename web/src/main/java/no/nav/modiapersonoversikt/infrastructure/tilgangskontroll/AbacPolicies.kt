package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.modiapersonoversikt.consumer.abac.*
import no.nav.modiapersonoversikt.consumer.abac.NavAttributes.*
import no.nav.modiapersonoversikt.consumer.abac.StandardAttributter.ACTION_ID
import java.util.*

private fun parseOidcToken(ssoToken: SsoToken): String {
    val fragments = ssoToken.token.split('.')
    return if (fragments.size == 1) fragments[0] else fragments[1]
}

private fun Optional<SsoToken>.createWithTokenBody(block: Request.(token: String) -> Unit): AbacRequest {
    return this
        .map(::parseOidcToken)
        .map { token -> abacRequest { block(this, token) } }
        .orElseThrow { IllegalStateException("Fant ikke saksbehandler-token") }
}

object AbacPolicies {
    fun tilgangTilModia(): AbacRequest = SubjectHandler.getSsoToken()
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

    fun tilgangTilBruker(fnr: String): AbacRequest = SubjectHandler.getSsoToken()
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

    fun tilgangTilBrukerMedAktorId(aktorId: String): AbacRequest = SubjectHandler.getSsoToken()
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

    fun kanPlukkeOppgave(): AbacRequest = SubjectHandler.getSsoToken()
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
