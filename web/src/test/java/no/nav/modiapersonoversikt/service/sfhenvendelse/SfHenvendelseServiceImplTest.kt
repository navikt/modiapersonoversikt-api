package no.nav.modiapersonoversikt.service.sfhenvendelse

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import no.nav.modiapersonoversikt.utils.Utils.withProperty
import okhttp3.OkHttpClient
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

internal class SfHenvendelseServiceImplTest {
    companion object {
        @JvmField
        @RegisterExtension
        val subject =
            AuthContextExtension(
                AuthContext(
                    UserRole.INTERN,
                    PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build()),
                ),
            )
    }

    private val httpClient: OkHttpClient = mockk()
    private val henvendelseBehandlingApi: HenvendelseBehandlingApi = mockk()
    private val henvendelseInfoApi: HenvendelseInfoApi = mockk()
    private val henvendelseOpprettApi: NyHenvendelseApi = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val norgApi: NorgApi = mockk()
    private val ansattService: AnsattService = mockk()
    private val sfHenvendelseServiceImpl =
        withProperty("SF_HENVENDELSE_URL", "http://dummy.io") {
            SfHenvendelseServiceImpl(
                pdlOppslagService,
                norgApi,
                ansattService,
                httpClient,
                henvendelseBehandlingApi,
                henvendelseInfoApi,
                henvendelseOpprettApi,
            )
        }

    @Test
    internal fun `skal fjerne innhold om man ikke har tematilgang`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns
            listOf(
                EnhetGeografiskTilknyttning(
                    enhetId = "5678",
                    geografiskOmraade = "005678",
                ),
            )

        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), any(), any()) } returns
            PaginertHenvendelseListeDTO(
                listOf(
                    dummyHenvendelse.medJournalpost("DAG"),
                    dummyHenvendelse.medJournalpost("SYK"),
                ),
                1,
                10,
                1,
                false,
            )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(2)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Melding innhold")
        assertThat(
            henvendelser[1].meldinger?.get(0)?.fritekst,
        ).isEqualTo("Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.")
    }

    @Test
    internal fun `skal fjerne lage dummy innhold om henvendelse er kassert`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns
            listOf(
                EnhetGeografiskTilknyttning(
                    enhetId = "5678",
                    geografiskOmraade = "005678",
                ),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), any(), any()) } returns
            PaginertHenvendelseListeDTO(
                listOf(dummyHenvendelse.somKassert()),
                1,
                10,
                1,
                false,
            )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        assertThat(henvendelser).hasSize(1)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Innholdet i denne henvendelsen er slettet av NAV.")
    }

    @Test
    internal fun `skal fjerne henvendelse om den ikke har noen meldinger`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns
            listOf(
                EnhetGeografiskTilknyttning(
                    enhetId = "5678",
                    geografiskOmraade = "005678",
                ),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), any(), any()) } returns
            PaginertHenvendelseListeDTO(
                data =
                    listOf(
                        dummyHenvendelse.medJournalpost("DAG"),
                        dummyHenvendelse.copy(meldinger = emptyList()),
                        dummyHenvendelse.medJournalpost("SYK"),
                    ),
                1,
                10,
                1,
                false,
            )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")

        assertThat(henvendelser).hasSize(2)
        assertThat(henvendelser[0].meldinger?.get(0)?.fritekst).isEqualTo("Melding innhold")
        assertThat(
            henvendelser[1].meldinger?.get(0)?.fritekst,
        ).isEqualTo("Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.")
    }

    @Test
    internal fun `skal sortere meldinger kronologisk`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG", "OPP")
        every { norgApi.hentGeografiskTilknyttning(any()) } returns
            listOf(
                EnhetGeografiskTilknyttning(
                    enhetId = "5678",
                    geografiskOmraade = "005678",
                ),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), any(), any()) } returns
            PaginertHenvendelseListeDTO(
                listOf(
                    dummyHenvendelse.copy(
                        meldinger =
                            listOf(
                                MeldingDTO(
                                    meldingsId = UUID.randomUUID().toString(),
                                    fritekst = "Andre melding",
                                    sendtDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                                    fra =
                                        MeldingFraDTO(
                                            identType = MeldingFraDTO.IdentType.NAVIDENT,
                                            ident = "Z123456",
                                        ),
                                ),
                                MeldingDTO(
                                    meldingsId = UUID.randomUUID().toString(),
                                    fritekst = "Første melding",
                                    sendtDato = OffsetDateTime.of(2021, 2, 1, 12, 37, 37, 0, ZoneOffset.UTC),
                                    fra =
                                        MeldingFraDTO(
                                            identType = MeldingFraDTO.IdentType.NAVIDENT,
                                            ident = "Z123456",
                                        ),
                                ),
                            ),
                    ),
                ),
                1,
                10,
                1,
                false,
            )

        val henvendelser = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId("00012345678910"), "0101")
        val henvendelse = henvendelser.first()
        assertThat(henvendelse.meldinger).hasSize(2)
        assertThat(henvendelse.meldinger?.get(0)?.fritekst).isEqualTo("Første melding")
    }

    @Test
    internal fun `henter alle sider for gruppering og lar andre henvendelser sta uendret`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG")
        val head =
            dummyHenvendelse.copy(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                kjedeId = "",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = "head")),
            )
        val child =
            head.copy(
                kjedeId = "head",
                meldinger = listOf(head.meldinger!!.single().copy(meldingsId = "child", fritekst = "Andre melding")),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(child, dummyHenvendelse), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns
            PaginertHenvendelseListeDTO(listOf(head), 2, 100, 2, false)

        val result = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(result).hasSize(2)
        assertThat(result.first()).isEqualTo(dummyHenvendelse)
        assertThat(result.last().kjedeId).isEqualTo("head")
        assertThat(result.last().meldinger?.map { it.meldingsId }).containsExactly("head", "child")
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) }
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) }
    }

    @Test
    internal fun `avviser ufullstendig paginering`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(emptyList(), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns null

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isInstanceOf(org.springframework.web.server.ResponseStatusException::class.java)
    }

    @Test
    internal fun `maskerer hele referatkjeden naar journalposten mangler tematilgang`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG")
        val head =
            dummyHenvendelse.copy(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                kjedeId = "",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = "head")),
            )
        val child =
            head.copy(
                kjedeId = "head",
                meldinger = listOf(head.meldinger!!.single().copy(meldingsId = "child")),
                journalposter = dummyHenvendelse.medJournalpost("SYK").journalposter,
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(head, child), 1, 100, 1, false)

        val resultat = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(resultat.single().meldinger).hasSize(2)
        assertThat(
            resultat
                .single()
                .meldinger!!
                .map { it.fritekst }
                .distinct(),
        ).containsExactly(
            "Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.",
        )
    }

    @Test
    internal fun `maskerer hele referatkjeden naar ett referat er kassert`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        val head =
            dummyHenvendelse.copy(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                kjedeId = "",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = "head")),
            )
        val child =
            head.copy(
                kjedeId = "head",
                kasseringsDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                meldinger = listOf(head.meldinger!!.single().copy(meldingsId = "child")),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(head, child), 1, 100, 1, false)

        val resultat = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(resultat.single().meldinger!!.map { it.fritekst }).containsExactly(
            "Innholdet i denne henvendelsen er slettet av NAV.",
            "Innholdet i denne henvendelsen er slettet av NAV.",
        )
    }

    @Test
    internal fun `avviser motstridende pagineringsmetadata`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(emptyList(), 1, 100, 2, false)

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isInstanceOf(IllegalStateException::class.java)
    }

    private val dummyHenvendelse =
        HenvendelseDTO(
            henvendelseType = HenvendelseDTO.HenvendelseType.MELDINGSKJEDE,
            fnr = "12345678910",
            aktorId = "00012345678910",
            opprettetDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            feilsendt = false,
            kjedeId = "ABBA12341010101",
            gjeldendeTemagruppe = "ARBD",
            avsluttetDato = null,
            kasseringsDato = null,
            gjeldendeTema = null,
            journalposter = null,
            meldinger =
                listOf(
                    MeldingDTO(
                        meldingsId = UUID.randomUUID().toString(),
                        fritekst = "Melding innhold",
                        sendtDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                        fra =
                            MeldingFraDTO(
                                identType = MeldingFraDTO.IdentType.NAVIDENT,
                                ident = "Z123456",
                            ),
                    ),
                ),
            markeringer = null,
        )

    private fun HenvendelseDTO.medJournalpost(tema: String): HenvendelseDTO {
        val journalpost =
            JournalpostDTO(
                journalforerNavIdent = "Z123456",
                journalforendeEnhet = "1234",
                journalfortDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
                journalfortTema = tema,
                journalpostId = "1a2sd5a4sd",
            )
        return dummyHenvendelse.copy(
            journalposter =
                if (this.journalposter == null) {
                    listOf(journalpost)
                } else {
                    this.journalposter!!.plus(journalpost)
                },
        )
    }

    private fun HenvendelseDTO.somKassert(): HenvendelseDTO =
        this.copy(
            kasseringsDato = OffsetDateTime.of(2021, 2, 2, 12, 37, 37, 0, ZoneOffset.UTC),
            meldinger = this.meldinger?.map { it.copy(fritekst = "") },
        )
}
