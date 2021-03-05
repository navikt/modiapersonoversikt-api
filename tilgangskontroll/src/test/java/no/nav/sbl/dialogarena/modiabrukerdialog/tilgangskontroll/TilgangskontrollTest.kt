package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.SubjectRule
import no.nav.sbl.dialogarena.abac.*
import no.nav.sbl.dialogarena.rsbac.DecisionEnums
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class TilgangskontrollTest {
    @Rule
    @JvmField
    val subject = SubjectRule(Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))

    @Test
    fun `deny om saksbehandler mangler modia-roller`() {
        val (message, decision) = Tilgangskontroll(mockContext(abacTilgang = Decision.Deny))
            .check(Policies.tilgangTilModia)
            .getDecision()

        assertEquals("Saksbehandler (Optional[Z999999]) har ikke tilgang til modia. Årsak: FP3_EGEN_ANSATT", message)
        assertEquals(DecisionEnums.DENY, decision)
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
    saksbehandlerIdent: String = "Z999999",
    tematilganger: Set<String> = setOf(),
    abacTilgang: Decision = Decision.Permit
): TilgangskontrollContext {
    val context: TilgangskontrollContext = mock()
    whenever(context.hentSaksbehandlerId()).thenReturn(Optional.of(saksbehandlerIdent))
    whenever(context.hentTemagrupperForSaksbehandler(any())).thenReturn(tematilganger)
    whenever(context.checkAbac(any())).thenReturn(
        AbacResponse(
            listOf(
                Response(
                    abacTilgang,
                    listOf(
                        Advice(
                            NavAttributes.ADVICE_DENY_REASON.attributeId,
                            listOf(
                                AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_CAUSE.attributeId, "cause-0001-manglerrolle"),
                                AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_DENY_POLICY.attributeId, "fp3_behandle_egen_ansatt"),
                                AttributeAssignment(NavAttributes.ADVICEOROBLIGATION_DENY_RULE.attributeId, "intern_behandle_kode6_mangler_gruppetilgang")
                            )
                        )
                    )
                )
            )
        )
    )
    return context
}
