package no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.apis.DefaultApi
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.AggregertPeriodeArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.BrukerArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.BrukerTypeArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.MetadataArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.PeriodeStartetArbeidssoekerregisteretDto
import no.nav.modiapersonoversikt.consumer.arbeidssoekerregisteret.generated.models.QueryRequestArbeidssoekerregisteretDto
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.OffsetDateTime
import java.util.UUID

internal class ArbeidssoekerregisteretServiceTest {
    private val api: DefaultApi = mockk()
    private val service: ArbeidssoekerregisteretService = ArbeidssoekerregisteretServiceImpl(api)

    private val fnr = "12345678910"
    private val periodeId = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")

    private val enMetadata =
        MetadataArbeidssoekerregisteretDto(
            tidspunkt = OffsetDateTime.parse("2024-01-15T10:00:00Z"),
            utfoertAv =
                BrukerArbeidssoekerregisteretDto(
                    type = BrukerTypeArbeidssoekerregisteretDto.SLUTTBRUKER,
                    id = fnr,
                ),
            kilde = "VEILARBREGISTRERING",
            aarsak = "Registrering",
        )

    private val pågåendePeriode =
        AggregertPeriodeArbeidssoekerregisteretDto(
            id = periodeId,
            identitetsnummer = fnr,
            startet =
                PeriodeStartetArbeidssoekerregisteretDto(
                    type = PeriodeStartetArbeidssoekerregisteretDto.Type.PERIODE_STARTET_V1,
                    sendtInnAv = enMetadata,
                    tidspunkt = OffsetDateTime.parse("2024-01-15T10:00:00Z"),
                ),
            avsluttet = null,
        )

    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    @Test
    fun `hentOppslag returnerer pågående periode`() {
        every { api.apiV3SnapshotPost(any()) } returns pågåendePeriode

        val result = service.hentOppslag(fnr)

        snapshot.assertMatches(result)
    }

    @Test
    fun `hentOppslag returnerer null når API returnerer null`() {
        every { api.apiV3SnapshotPost(any()) } returns null

        val result = service.hentOppslag(fnr)

        assertNull(result)
    }

    @Test
    fun `hentOppslag sender fnr som IDENTITETSNUMMER-request`() {
        every { api.apiV3SnapshotPost(any()) } returns pågåendePeriode
        val requestSlot = slot<QueryRequestArbeidssoekerregisteretDto>()

        service.hentOppslag(fnr)

        verify { api.apiV3SnapshotPost(capture(requestSlot)) }
        val request = requestSlot.captured
        assert(request.type == QueryRequestArbeidssoekerregisteretDto.Type.IDENTITETSNUMMER)
        assert(request.identitetsnummer == fnr)
    }
}
