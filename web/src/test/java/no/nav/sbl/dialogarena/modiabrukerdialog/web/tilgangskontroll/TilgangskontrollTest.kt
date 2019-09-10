package no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll

import com.nhaarman.mockito_kotlin.*
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rsbac.DecisionEnums
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TilgangskontrollTest {
    @Nested
    inner class tilgangTilModia {
        @Test
        fun `deny om saksbehandler mangler modia-roller`() {
            val (_, decision) = Tilgangskontroll(mockContext())
                    .tilgangTilModia()
                    .getDecision()

            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `permit om saksbehandler har modiagenerell-rollen`() {
            val (_, decision) = Tilgangskontroll(mockContext(roller = listOf("0000-ga-bd06_modiagenerelltilgang")))
                    .tilgangTilModia()
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `permit om saksbehandler har modiaoppfolging-rollen`() {
            val (_, decision) = Tilgangskontroll(mockContext(roller = listOf("0000-ga-modia-oppfolging")))
                    .tilgangTilModia()
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `permit om saksbehandler har modiasyfo-rollen`() {
            val (_, decision) = Tilgangskontroll(mockContext(roller = listOf("0000-ga-syfo-sensitiv")))
                    .tilgangTilModia()
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }
    }

    @Nested
    inner class tilgangTilBruker {
        @Test
        fun `deny om saksbehandler mangler modia-roller`() {
            val (_, decision) = Tilgangskontroll(mockContext())
                    .tilgangTilBruker("fnr")
                    .getDecision()

            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `deny om bruker er kode6 og saksbehandler mangler kode6-rolle`() {
            val context = mockContext(
                    roller = listOf("0000-ga-bd06_modiagenerelltilgang"),
                    diskresjonsKode = "6"
            )
            val (_, decision) = Tilgangskontroll(context)
                    .tilgangTilBruker("fnr")
                    .getDecision()

            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `deny om bruker er kode7 og saksbehandler mangler kode7-rolle`() {
            val context = mockContext(
                    roller = listOf("0000-ga-bd06_modiagenerelltilgang"),
                    diskresjonsKode = "7"
            )
            val (_, decision) = Tilgangskontroll(context)
                    .tilgangTilBruker("fnr")
                    .getDecision()

            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `permit om bruker ikke er kode6_7 og saksbehandler har modia-rolle`() {
            val (_, decision) = Tilgangskontroll(mockContext(roller = listOf("0000-ga-bd06_modiagenerelltilgang")))
                    .tilgangTilBruker("fnr")
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `permit om bruker er kode6_7 og saksbehandler har modia-rolle og kode6_7-rolle`() {
            val context = mockContext(
                    roller = listOf("0000-ga-bd06_modiagenerelltilgang", "0000-GA-GOSYS_KODE7"),
                    diskresjonsKode = "7"
            )
            val (_, decision) = Tilgangskontroll(context)
                    .tilgangTilBruker("fnr")
                    .getDecision()

            assertEquals(DecisionEnums.PERMIT, decision)
        }
    }

    @Nested
    inner class `modiaRolle policy` {
        @Test
        fun `deny om saksbehandler mangler modia-roller`() {
            val decision = Policies.modiaRolle.invoke(mockContext())
            assertEquals(DecisionEnums.DENY, decision)
        }

        @Test
        fun `permit om saksbehandler har modiagenerell-rollen`() {
            val decision = Policies.modiaRolle.invoke(mockContext(roller = listOf("0000-ga-bd06_modiagenerelltilgang")))
            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `permit om saksbehandler har modiaoppfolging-rollen`() {
            val decision = Policies.modiaRolle.invoke(mockContext(roller = listOf("0000-ga-modia-oppfolging")))
            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `permit om saksbehandler har modiasyfo-rollen`() {
            val decision = Policies.modiaRolle.invoke(mockContext(roller = listOf("0000-ga-syfo-sensitiv")))
            assertEquals(DecisionEnums.PERMIT, decision)
        }
    }

    @Nested
    inner class `kode6 policy` {
        @Test
        fun `n_a om bruker ikke har kode6`() {
            val context = mockContext()
            val decision = Policies.kode6.with("fnr").invoke(context)

            verify(context, never()).hentSaksbehandlerId()
            verify(context, never()).hentSaksbehandlerRoller()
            assertEquals(DecisionEnums.NOT_APPLICABLE, decision)
        }

        @Test
        fun `permit om bruker har kode6 og saksbehandler har riktig rolle`() {
            val context = mockContext(roller = listOf("0000-GA-GOSYS_KODE6"), diskresjonsKode = "6")
            val decision = Policies.kode6.with("fnr").invoke(context)

            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `deny om bruker har kode6 og saksbehandler ikke har riktig rolle`() {
            val context = mockContext(roller = listOf("0000-GA-GOSYS_KODE0"), diskresjonsKode = "6")
            val decision = Policies.kode6.with("fnr").invoke(context)

            assertEquals(DecisionEnums.DENY, decision)
        }
    }

    @Nested
    inner class `kode7 policy` {
        @Test
        fun `n_a om bruker ikke har kode7`() {
            val context = mockContext()
            val decision = Policies.kode7.with("fnr").invoke(context)

            verify(context, never()).hentSaksbehandlerId()
            verify(context, never()).hentSaksbehandlerRoller()
            assertEquals(DecisionEnums.NOT_APPLICABLE, decision)
        }

        @Test
        fun `permit om bruker har kode7 og saksbehandler har riktig rolle`() {
            val context = mockContext(roller = listOf("0000-GA-GOSYS_KODE7"), diskresjonsKode = "7")
            val decision = Policies.kode7.with("fnr").invoke(context)

            assertEquals(DecisionEnums.PERMIT, decision)
        }

        @Test
        fun `deny om bruker har kode7 og saksbehandler ikke har riktig rolle`() {
            val context = mockContext(roller = listOf("0000-GA-GOSYS_KODE0"), diskresjonsKode = "7")
            val decision = Policies.kode7.with("fnr").invoke(context)

            assertEquals(DecisionEnums.DENY, decision)
        }
    }
}

private fun mockContext(
        saksbehandlerIdent: String = "Z999999",
        roller: List<String> = emptyList(),
        diskresjonsKode: String? = null
): GenerellContext {
    val context: GenerellContext = mock()
    whenever(context.hentSaksbehandlerId()).thenReturn(saksbehandlerIdent)
    whenever(context.hentSaksbehandlerRoller()).thenReturn(roller)
    whenever(context.hentDiskresjonkode(any())).thenReturn(diskresjonsKode)
    whenever(context.harSaksbehandlerRolle(any())).thenAnswer {
        roller.contains(it.arguments[0])
    }
    return context
}