package no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.policies

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.infrastructure.kabac.KabacTestUtils
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.CommonAttributes
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.kabac.providers.HenvendelseEierPip
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingDTO
import no.nav.modiapersonoversikt.legacy.api.domain.sfhenvendelse.generated.models.MeldingFraDTO
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

internal class HenvendelseTilhorerBrukerPolicyTest {
    private val policy = KabacTestUtils.PolicyTester(HenvendelseTilhorerBrukerPolicy)
    private val henvendelseService = mockk<SfHenvendelseService>()
    private val kjedeId = "abba1001"
    private val fnr = "12345678910"

    @Test
    internal fun `permit om henvendelse eies av bruker`() {
        every { henvendelseService.hentHenvendelse(kjedeId) } returns dummyHenvendelse.copy(fnr = fnr)
        policy.assertPermit(
            HenvendelseEierPip(henvendelseService),
            CommonAttributes.HENVENDELSE_KJEDE_ID.withValue(kjedeId),
            CommonAttributes.FNR.withValue(Fnr(fnr)),
        )
    }

    @Test
    internal fun `deny om henvendelse ikke eies av bruker`() {
        every { henvendelseService.hentHenvendelse(kjedeId) } returns dummyHenvendelse.copy(fnr = fnr)
        policy.assertDeny(
            HenvendelseEierPip(henvendelseService),
            CommonAttributes.HENVENDELSE_KJEDE_ID.withValue(kjedeId),
            CommonAttributes.FNR.withValue(Fnr("annet-fnr")),
        ).withMessage("Bruker eier ikke henvendelsen")
    }

    private val dummyHenvendelse = HenvendelseDTO(
        henvendelseType = HenvendelseDTO.HenvendelseType.MELDINGSKJEDE,
        fnr = "12345678910",
        aktorId = "00012345678910",
        opprinneligGT = "010101",
        opprettetDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
        kontorsperre = false,
        feilsendt = false,
        kjedeId = "ABBA12341010101",
        gjeldendeTemagruppe = "ARBD",
        avsluttetDato = null,
        kasseringsDato = null,
        gjeldendeTema = null,
        journalposter = null,
        meldinger = listOf(
            MeldingDTO(
                fritekst = "Melding innhold",
                sendtDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                fra = MeldingFraDTO(
                    identType = MeldingFraDTO.IdentType.NAVIDENT,
                    ident = "Z123456"
                )
            )
        ),
        markeringer = null
    )
}
