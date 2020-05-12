package no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll

import com.nhaarman.mockitokotlin2.*
import no.nav.brukerdialog.security.context.SubjectExtension
import no.nav.brukerdialog.security.domain.IdentType
import no.nav.common.auth.SsoToken
import no.nav.common.auth.Subject
import no.nav.sbl.dialogarena.abac.*
import no.nav.sbl.dialogarena.rsbac.DecisionEnums
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*
import kotlin.test.assertEquals


@ExtendWith(SubjectExtension::class)
internal class TilgangskontrollTest {
    @BeforeEach
    fun setup(subjectStore: SubjectExtension.SubjectStore) {
        subjectStore.setSubject(Subject("Z999999", IdentType.InternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))
    }

    @Nested
    inner class tilgangTilModia {
        @Test
        fun `deny om saksbehandler mangler modia-roller`() {
            val (message, decision) = Tilgangskontroll(mockContext(abacTilgang = Decision.Deny))
                    .check(Policies.tilgangTilModia)
                    .getDecision()

            assertEquals("Saksbehandler (Optional[Z999999]) har ikke tilgang til modia. Årsak: fp3_behandle_egen_ansatt", message)
            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `permit om saksbehandler har modiagenerell-rollen`() {
            val (_, decision) = Tilgangskontroll(mockContext())
                    .check(Policies.tilgangTilModia)
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }
    }
    @Nested
    inner class `tematilganger policy` {
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

}

private fun mockContext(
        saksbehandlerIdent: String = "Z999999",
        tematilganger: Set<String> = setOf(),
        abacTilgang: Decision = Decision.Permit
): TilgangskontrollContext {
    val context: TilgangskontrollContext = mock()
    whenever(context.hentSaksbehandlerId()).thenReturn(Optional.of(saksbehandlerIdent))
    whenever(context.hentTemagrupperForSaksbehandler(any())).thenReturn(tematilganger)
    whenever(context.checkAbac(any())).thenReturn(AbacResponse(listOf(
            Response(abacTilgang, listOf(
                    Advice("deny_reason", listOf(
                            AttributeAssignment("cause", "cause-0001-manglerrolle"),
                            AttributeAssignment("actual_policy", "fp3_behandle_egen_ansatt")
                    ))
            ))
    )))
    return context
}
