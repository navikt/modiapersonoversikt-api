package no.nav.modiapersonoversikt.service.sfhenvendelse

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.modiapersonoversikt.consumer.norg.NorgApi
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.EnhetGeografiskTilknyttning
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.HenvendelseBehandlingApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.HenvendelseInfoApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.apis.NyHenvendelseApi
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.*
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.testutils.AuthContextExtension
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.modiapersonoversikt.utils.Utils.withProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

internal class SfHenvendelseServiceImplTest {

    companion object {
        @JvmField
        @RegisterExtension
        val subject = AuthContextExtension(
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
            )
        )
    }

    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = mockk()
    private val henvendelseInfoApi: HenvendelseInfoApi = mockk()
    private val henvendelseOpprettApi: NyHenvendelseApi = mockk()
    private val henvendelseBehandlingProxyApi: HenvendelseBehandlingApi = mockk()
    private val henvendelseInfoProxyApi: HenvendelseInfoApi = mockk()
    private val henvendelseOpprettProxyApi: NyHenvendelseApi = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val norgApi: NorgApi = mockk()
    private val oboApiTokenClient: BoundedOnBehalfOfTokenClient = mockk()
    private val machineToMachineApiTokenClient: BoundedMachineToMachineTokenClient = mockk()
    private val oboProxyApiTokenClient: BoundedOnBehalfOfTokenClient = mockk()
    private val machineToMachineProxyApiTokenClient: BoundedMachineToMachineTokenClient = mockk()
    private val ansattService: AnsattService = mockk()
    private val sfHenvendelseServiceImpl = withProperties(mapOf("SF_HENVENDELSE_URL" to "http://dummy.io", "SF_HENVENDELSE_PROXY_URL" to "http://dummy.io")) {
        SfHenvendelseServiceImpl(
            oboApiTokenClient,
            machineToMachineApiTokenClient,
            oboProxyApiTokenClient,
            machineToMachineProxyApiTokenClient,
            pdlOppslagService,
            norgApi,
            ansattService,
            henvendelseBehandlingApi,
            henvendelseInfoApi,
            henvendelseOpprettApi,
            henvendelseBehandlingProxyApi,
            henvendelseInfoProxyApi,
            henvendelseOpprettProxyApi,
        )
    }

    @Test
    internal fun `skal fjerne kontorsperrede henvendelser`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns listOf(
            EnhetGeografiskTilknyttning(
                enhetId = "5678",
                geografiskOmraade = "005678"
            )
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.medJournalpost("DAG"),
            dummyHenvendelse.medKontorsperre(enhet = "1234")
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(1)
        assertThat(henvendelser[0].kontorsperre).isFalse
    }

    @Test
    internal fun `skal fjerne innhold om man ikke har tematilgang`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns listOf(
            EnhetGeografiskTilknyttning(
                enhetId = "5678",
                geografiskOmraade = "005678"
            )
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.medJournalpost("DAG"),
            dummyHenvendelse.medJournalpost("SYK")
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(2)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Melding innhold")
        assertThat(henvendelser[1].meldinger?.get(0)?.fritekst).isEqualTo("Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.")
    }

    @Test
    internal fun `skal fjerne lage dummy innhold om henvendelse er kassert`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns listOf(
            EnhetGeografiskTilknyttning(
                enhetId = "5678",
                geografiskOmraade = "005678"
            )
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.somKassert()
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(1)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Innholdet i denne henvendelsen er slettet av NAV.")
    }

    @Test
    internal fun `skal fjerne henvendelse om den ikke har noen meldinger`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns listOf(
            EnhetGeografiskTilknyttning(
                enhetId = "5678",
                geografiskOmraade = "005678"
            )
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.medJournalpost("DAG"),
            dummyHenvendelse.copy(meldinger = emptyList()),
            dummyHenvendelse.medJournalpost("SYK")
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")

        assertThat(henvendelser).hasSize(2)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Melding innhold")
        assertThat(henvendelser[1].meldinger?.get(0)?.fritekst).isEqualTo("Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.")
    }

    @Test
    internal fun `skal sortere meldinger kronologisk`() {
        every { ansattService.hentAnsattFagomrader(any(), any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns listOf(
            EnhetGeografiskTilknyttning(
                enhetId = "5678",
                geografiskOmraade = "005678"
            )
        )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any()) } returns listOf(
            dummyHenvendelse.copy(
                meldinger = listOf(
                    MeldingDTO(
                        meldingsId = UUID.randomUUID().toString(),
                        fritekst = "Andre melding",
                        sendtDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                        fra = MeldingFraDTO(
                            identType = MeldingFraDTO.IdentType.NAVIDENT,
                            ident = "Z123456"
                        )
                    ),
                    MeldingDTO(
                        meldingsId = UUID.randomUUID().toString(),
                        fritekst = "Første melding",
                        sendtDato = OffsetDateTime.of(2021, 2, 1, 12, 37, 37, 0, ZoneOffset.UTC),
                        fra = MeldingFraDTO(
                            identType = MeldingFraDTO.IdentType.NAVIDENT,
                            ident = "Z123456"
                        )
                    )
                )
            )
        )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        val henvendelse = henvendelser.first()
        assertThat(henvendelse.meldinger).hasSize(2)
        assertThat(henvendelse.meldinger?.get(0)?.fritekst).isEqualTo("Første melding")
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
                meldingsId = UUID.randomUUID().toString(),
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

    private fun HenvendelseDTO.medJournalpost(tema: String): HenvendelseDTO {
        val journalpost = JournalpostDTO(
            journalforerNavIdent = "Z123456",
            journalforendeEnhet = "1234",
            journalfortDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            journalfortTema = tema,
            journalpostId = "1a2sd5a4sd"
        )
        return dummyHenvendelse.copy(
            journalposter = if (this.journalposter == null) {
                listOf(journalpost)
            } else {
                this.journalposter!!.plus(journalpost)
            }
        )
    }

    private fun HenvendelseDTO.medKontorsperre(enhet: String? = null, gt: String? = null): HenvendelseDTO {
        val markering = MarkeringDTO(
            markeringstype = MarkeringDTO.Markeringstype.KONTORSPERRE,
            markertDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            markertAv = "Z123456",
            kontorsperreEnhet = enhet,
            kontorsperreGT = gt
        )
        return this.copy(
            kontorsperre = true,
            markeringer = if (this.markeringer == null) listOf(markering) else this.markeringer!!.plus(markering)
        )
    }

    private fun HenvendelseDTO.somKassert(): HenvendelseDTO {
        return this.copy(
            kasseringsDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            meldinger = this.meldinger?.map { it.copy(fritekst = "") }
        )
    }
}
