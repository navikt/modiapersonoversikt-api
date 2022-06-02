package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.consumer.abac.*
import no.nav.modiapersonoversikt.infrastructure.rsbac.DecisionEnums
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.testutils.AuthContextRule
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class TilgangskontrollTest {
    @Rule
    @JvmField
    val subject = AuthContextRule(
        AuthContext(
            UserRole.INTERN,
            PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
        )
    )

    @Test
    fun `deny om saksbehandler mangler modia-roller`() {
        val (message, decision) = Tilgangskontroll(mockContext(abacTilgang = Decision.Deny))
            .check(Policies.tilgangTilModia)
            .getDecision()

        assertEquals(DecisionEnums.DENY, decision)
        assertEquals("Saksbehandler (Optional[Z999999]) har ikke tilgang til modia. Årsak: FP3_EGEN_ANSATT", message)
    }

    @Test
    fun `deny om ny deny_policy for kode6 er brukt`() {
        val (message, decision) = Tilgangskontroll(
            mockContext(
                abacTilgang = Decision.Deny,
                denyPolicy = "adressebeskyttelse_strengt_fortrolig_adresse"
            )
        )
            .check(Policies.tilgangTilModia)
            .getDecision()

        assertEquals(DecisionEnums.DENY, decision)
        assertEquals("Saksbehandler (Optional[Z999999]) har ikke tilgang til modia. Årsak: FP1_KODE6", message)
    }

    @Test
    fun `deny om strengt fortrolig utland`() {
        val (message, decision) = Tilgangskontroll(
            mockContext(
                abacTilgang = Decision.Deny,
                denyPolicy = "adressebeskyttelse_strengt_fortrolig_adresse_utland"
            )
        )
            .check(Policies.tilgangTilModia)
            .getDecision()

        assertEquals(DecisionEnums.DENY, decision)
        assertEquals("Saksbehandler (Optional[Z999999]) har ikke tilgang til modia. Årsak: FP1_KODE6", message)
    }

    @Test
    fun `permit om saksbehandler har modiagenerell-rollen`() {
        val (_, decision) = Tilgangskontroll(mockContext())
            .check(Policies.tilgangTilModia)
            .getDecision()

        assertEquals(DecisionEnums.PERMIT, decision)
    }

    @Test
    fun `permit om bruker tilgang pa tema`() {
        val context = mockContext(tematilganger = setOf("OPP", "FMLI"))
        val decision = Policies.tilgangTilTema.with(TilgangTilTemaData("1234", "FMLI")).invoke(context).value

        assertEquals(DecisionEnums.PERMIT, decision)
    }

    @Test
    fun `deny om bruker ikke har tilgang pa tema`() {
        val context = mockContext(tematilganger = setOf("OPP", "ARBD"))
        val decision = Policies.tilgangTilTema.with(TilgangTilTemaData("1234", "FMLI")).invoke(context).value

        assertEquals(DecisionEnums.DENY, decision)
    }
}

private fun mockContext(
    saksbehandlerIdent: NavIdent = NavIdent("Z999999"),
    tematilganger: Set<String> = setOf(),
    abacTilgang: Decision = Decision.Permit,
    denyPolicy: String = "fp3_behandle_egen_ansatt"
): TilgangskontrollContext {
    val context: TilgangskontrollContext = mockk()
    val unleashMock: UnleashService = mockk()
    every { context.hentSaksbehandlerId() } returns Optional.of(saksbehandlerIdent)
    every { context.hentTemagrupperForSaksbehandler(any()) } returns tematilganger
    every { context.unleash() } returns unleashMock
    every { unleashMock.isEnabled(any<String>()) } returns true
    every { unleashMock.isEnabled(any<Feature>()) } returns true
    every { context.checkAbac(any()) } returns AbacResponse(
        listOf(
            Response(
                abacTilgang,
                listOf(
                    Advice(
                        NavAttributes.ADVICE_DENY_REASON.attributeId,
                        listOf(
                            AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_CAUSE.attributeId, "cause-0001-manglerrolle"),
                            AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_DENY_POLICY.attributeId, denyPolicy),
                            AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_DENY_RULE.attributeId, "intern_behandle_kode6_mangler_gruppetilgang")
                        )
                    )
                )
            )
        )
    )

    return context
}
