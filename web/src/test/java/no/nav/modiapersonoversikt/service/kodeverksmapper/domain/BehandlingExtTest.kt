package no.nav.modiapersonoversikt.service.kodeverksmapper.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BehandlingExtTest {
    @Nested
    inner class ToString {
        @Test
        internal fun `should include both values`() {
            val behandling = Behandling("tema", "type")
            assertThat(behandling.asV2BehandlingString()).isEqualTo("tema:type")
        }

        @Test
        internal fun `should replace null values with empty string`() {
            val behandling = Behandling(null, null)
            assertThat(behandling.asV2BehandlingString()).isEqualTo(":")
        }

        @Test
        internal fun `should work with single value`() {
            val tema = Behandling("tema", null)
            assertThat(tema.asV2BehandlingString()).isEqualTo("tema:")

            val type = Behandling(null, "type")
            assertThat(type.asV2BehandlingString()).isEqualTo(":type")
        }
    }

    @Nested
    inner class Parsing {
        @Test
        internal fun `should throw exception if invalid format`() {
            assertThatThrownBy { "tema".parseV2BehandlingString() }
                .hasMessageContaining("<tema>:<type>")
                .hasMessageContaining("'tema'")
                .isInstanceOf(IllegalArgumentException::class.java)

            assertThatThrownBy { "tema:type:test".parseV2BehandlingString() }
                .hasMessageContaining("<tema>:<type>")
                .hasMessageContaining("'tema:type:test'")
                .isInstanceOf(IllegalArgumentException::class.java)

            assertThatCode { "tema:type".parseV2BehandlingString() }
                .doesNotThrowAnyException()
        }

        @Test
        internal fun `should set field as null for empty values`() {
            val tema = "tema:".parseV2BehandlingString()
            assertThat(tema.behandlingstema).isEqualTo("tema")
            assertThat(tema.behandlingstype).isNull()

            val type = ":type".parseV2BehandlingString()
            assertThat(type.behandlingstema).isNull()
            assertThat(type.behandlingstype).isEqualTo("type")

            val none = ":".parseV2BehandlingString()
            assertThat(none.behandlingstema).isNull()
            assertThat(none.behandlingstype).isNull()

            val empty = "".parseV2BehandlingString()
            assertThat(empty.behandlingstema).isNull()
            assertThat(empty.behandlingstype).isNull()

            val both = "tema:type".parseV2BehandlingString()
            assertThat(both.behandlingstema).isEqualTo("tema")
            assertThat(both.behandlingstype).isEqualTo("type")
        }
    }
}
