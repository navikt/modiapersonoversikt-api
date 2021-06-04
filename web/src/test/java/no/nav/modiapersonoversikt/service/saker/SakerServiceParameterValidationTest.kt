package no.nav.modiapersonoversikt.service.saker

import no.nav.modiapersonoversikt.api.domain.saker.Sak
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class SakerServiceParameterValidationTest {
    val saksId = "12345"
    val fnr = "9999999999"
    val enhet = "4100"
    val behandlingskjedeId = "6789"

    val sak = Sak().also {
        it.saksId = saksId
    }

    @Test
    fun `returnerer vanlig om alt er ok`() {
        assertDoesNotThrow { requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjedeId, fnr, enhet) }
    }

    @Test
    fun `kaster exception om sak mangler`() {
        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(null, behandlingskjedeId, fnr, enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(null, null, fnr, enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `kaster exception om behandlingskjedeid mangler eller er blankt`() {
        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, null, fnr, enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, "", fnr, enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `kaster exception om fnr mangler eller er blankt`() {
        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjedeId, null, enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)

        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjedeId, "", enhet) }
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `kaster exception om enhet mangler eller er blankt`() {
        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjedeId, fnr, null) }
            .isExactlyInstanceOf(EnhetIkkeSatt::class.java)

        assertThatThrownBy { requireKnyttTilSakParametereNotNullOrBlank(sak, behandlingskjedeId, fnr, "") }
            .isExactlyInstanceOf(EnhetIkkeSatt::class.java)
    }
}
