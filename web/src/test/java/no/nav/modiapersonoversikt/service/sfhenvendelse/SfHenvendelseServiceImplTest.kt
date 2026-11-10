package no.nav.modiapersonoversikt.service.sfhenvendelse

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import io.mockk.verifyOrder
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
import org.slf4j.LoggerFactory
import java.net.SocketTimeoutException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.concurrent.CancellationException

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
                kjedeId = "head",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = "head")),
            )
        val headMelding = head.meldinger!!.single()
        val child =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                opprettetDato = head.opprettetDato.plusMinutes(1),
                meldinger =
                    listOf(
                        headMelding.copy(
                            meldingsId = "child",
                            sendtDato = headMelding.sendtDato.plusMinutes(1),
                            fritekst = "Andre melding",
                        ),
                    ),
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(child, dummyHenvendelse), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns
            PaginertHenvendelseListeDTO(listOf(head), 2, 100, 2, false)

        val result = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(result).hasSize(2)
        assertThat(result.first()).isEqualTo(dummyHenvendelse)
        assertThat(result.last().kjedeId).isEqualTo("head")
        assertThat(result.last().gjeldendeTemagruppe).isEqualTo("FMLI")
        assertThat(result.last().opprettetDato).isEqualTo(child.opprettetDato)
        assertThat(result.last().meldinger?.map { it.meldingsId }).containsExactly("head", "child")
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) }
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) }
        verify(exactly = 0) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any()) }
    }

    @Test
    internal fun `viser referater uten hovedmelding som en trad med opprinnelig kjedeId`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG")
        val eldste =
            dummyHenvendelse.copy(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                kjedeId = "missing",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = "eldste")),
            )
        val nyeste =
            eldste.copy(
                gjeldendeTemagruppe = "FMLI",
                opprettetDato = eldste.opprettetDato.plusMinutes(1),
                meldinger =
                    listOf(
                        eldste.meldinger!!.single().copy(
                            meldingsId = "nyeste",
                            sendtDato =
                                eldste.meldinger!!
                                    .single()
                                    .sendtDato
                                    .plusMinutes(1),
                        ),
                    ),
                journalposter = dummyHenvendelse.medJournalpost("SYK").journalposter,
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(nyeste), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns
            PaginertHenvendelseListeDTO(listOf(eldste), 2, 100, 2, false)

        val resultat = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(resultat).hasSize(1)
        assertThat(resultat.single().kjedeId).isEqualTo("missing")
        assertThat(resultat.single().opprettetDato).isEqualTo(nyeste.opprettetDato)
        assertThat(resultat.single().gjeldendeTemagruppe).isEqualTo("FMLI")
        assertThat(resultat.single().meldinger?.map { it.meldingsId }).containsExactly("eldste", "nyeste")
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
    internal fun `avviser ufullstendig paginering`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(emptyList(), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns null

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isInstanceOf(org.springframework.web.server.ResponseStatusException::class.java)
        verify(exactly = 0) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any()) }
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
        val headMelding = head.meldinger!!.single()
        val child =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                meldinger =
                    listOf(headMelding.copy(meldingsId = "child", sendtDato = headMelding.sendtDato.plusMinutes(1))),
                journalposter = dummyHenvendelse.medJournalpost("SYK").journalposter,
            )
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(head, child), 1, 100, 1, false)

        val resultat = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(resultat.single().meldinger).hasSize(2)
        assertThat(resultat.single().gjeldendeTemagruppe).isEqualTo("FMLI")
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
        verify(exactly = 0) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any()) }
    }

    @Test
    internal fun `grupperingsvalideringer bruker v1 fallback med trygg arsakslogg`() {
        val cases =
            listOf(
                "FEIL_ANTALL_MELDINGER" to listOf(referat("empty", messages = emptyList())),
                "MANGLENDE_MELDINGS_ID" to listOf(referat(null)),
                "FLERE_HOVEDMELDINGER" to listOf(referat("same"), referat("same")),
                "ULIKE_EIERE" to
                    listOf(
                        referat("root"),
                        referat("child", chainId = "root").copy(fnr = "11111111111"),
                    ),
                "MANGLENDE_ELLER_DUPLIKAT_ID" to
                    listOf(
                        referat("root"),
                        referat("duplicate", chainId = "root"),
                        referat("duplicate", chainId = "root"),
                    ),
                "TEMAKONFLIKT" to
                    listOf(
                        referat("root", theme = "DAG"),
                        referat("child", chainId = "root", theme = "SYK", sentDate = referat("root").meldinger!!.single().sendtDato),
                    ),
            )

        withFallbackLogAppender { appender ->
            cases.forEach { (reason, invalidV2Data) ->
                clearMocks(henvendelseInfoApi)
                every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
                every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
                    page(invalidV2Data)
                every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) } returns page(emptyList())
                appender.list.clear()

                assertThat(
                    sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101"),
                ).isEmpty()
                verify(exactly = 1) {
                    henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100)
                }

                val events = appender.list.filter { it.formattedMessage.contains("v1-fallback") }
                assertThat(events).hasSize(1)
                assertThat(events.single().level).isEqualTo(Level.WARN)
                assertThat(events.single().formattedMessage).contains(reason)
                assertThat(events.single().formattedMessage).doesNotContain(
                    dummyHenvendelse.fnr,
                    dummyHenvendelse.aktorId,
                    "root",
                    "duplicate",
                )
                assertThat(events.single().throwableProxy).isNull()
                assertThat(
                    events
                        .single()
                        .argumentArray
                        .orEmpty()
                        .joinToString(),
                ).doesNotContain(
                    dummyHenvendelse.fnr,
                    dummyHenvendelse.aktorId,
                    "root",
                    "duplicate",
                )
            }
        }
    }

    @Test
    internal fun `ugyldig v2 pa senere side forkaster alle v2 data og henter alle v1 sider`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns setOf("DAG")
        val v2Dialog =
            dummyHenvendelse.copy(
                kjedeId = "v2-only",
                meldinger = listOf(dummyHenvendelse.meldinger!!.single().copy(fritekst = "V2 skal forkastes")),
            )
        val v1MultiMessageReferat =
            referat("v1-head").copy(
                journalposter = dummyHenvendelse.medJournalpost("SYK").journalposter,
                meldinger =
                    listOf(
                        dummyHenvendelse.meldinger!!.single().copy(meldingsId = "v1-first", fritekst = "V1 første"),
                        dummyHenvendelse.meldinger!!.single().copy(meldingsId = "v1-second", fritekst = "V1 andre"),
                    ),
            )
        val kassertV1Dialog = dummyHenvendelse.copy(kjedeId = "v1-kassert").somKassert()
        val finalV1Dialog = dummyHenvendelse.copy(kjedeId = "v1-final")

        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(v2Dialog), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } returns
            PaginertHenvendelseListeDTO(listOf(referat("invalid", messages = emptyList())), 2, 100, 2, false)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(v1MultiMessageReferat, kassertV1Dialog), 1, 100, 2, true)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 2, 100) } returns
            PaginertHenvendelseListeDTO(listOf(finalV1Dialog), 2, 100, 2, false)

        val result = sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")

        assertThat(result).hasSize(3)
        assertThat(result.map { it.kjedeId }).containsExactly("v1-head", "v1-kassert", "v1-final")
        assertThat(result.first().meldinger).hasSize(2)
        assertThat(
            result
                .first()
                .meldinger!!
                .map { it.fritekst }
                .distinct(),
        ).containsExactly(
            "Du kan ikke se innholdet i denne henvendelsen fordi tråden er journalført på et tema du ikke har tilgang til.",
        )
        assertThat(result[1].meldinger!!.single().fritekst).isEqualTo("Innholdet i denne henvendelsen er slettet av NAV.")
        assertThat(
            result
                .mapNotNull { it.meldinger }
                .flatten()
                .mapNotNull { it.fritekst },
        ).doesNotContain("V2 skal forkastes")
        verifyOrder {
            henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100)
            henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100)
            henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100)
            henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 2, 100)
        }
    }

    @Test
    internal fun `ukjent grupperingsfeil faller tilbake uten a logge feilmelding`() {
        val marker = "SENSITIVE-SYNTHETIC-MARKER"
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns page(listOf(dummyHenvendelse))
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) } returns page(emptyList())

        withFallbackLogAppender { appender ->
            mockkStatic(::grupperSamtalereferater)
            try {
                every { grupperSamtalereferater(any()) } throws IllegalStateException(marker)

                assertThat(
                    sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101"),
                ).isEmpty()

                val event = appender.list.single { it.formattedMessage.contains("v1-fallback") }
                assertThat(event.formattedMessage).contains("UKJENT_GRUPPERINGSFEIL").doesNotContain(marker)
                assertThat(event.throwableProxy).isNull()
                assertThat(event.argumentArray.orEmpty().joinToString()).doesNotContain(marker)
                verify(exactly = 1) {
                    henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100)
                }
            } finally {
                unmockkStatic(::grupperSamtalereferater)
            }
        }
    }

    @Test
    internal fun `kansellering og error fra gruppering propagerer uten v1 fallback`() {
        listOf(CancellationException("cancelled"), AssertionError("synthetic error")).forEach { failure ->
            every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
            every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
                page(listOf(dummyHenvendelse))
            mockkStatic(::grupperSamtalereferater)
            try {
                every { grupperSamtalereferater(any()) } throws failure

                assertThatThrownBy {
                    sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
                }.isSameAs(failure)
                verify(exactly = 0) {
                    henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any())
                }
            } finally {
                unmockkStatic(::grupperSamtalereferater)
            }
        }
    }

    @Test
    internal fun `etterbehandlingsfeil propagerer uten v1 fallback`() {
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        val postprocessingFailure = IllegalStateException("synthetic postprocessing failure")
        val postprocessingDto = mockk<HenvendelseDTO>()
        every { postprocessingDto.henvendelseType } returns HenvendelseDTO.HenvendelseType.MELDINGSKJEDE
        every { postprocessingDto.meldinger } throws postprocessingFailure
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns page(listOf(postprocessingDto))

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isSameAs(postprocessingFailure)
        verify(exactly = 0) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any()) }
    }

    @Test
    internal fun `feil pa v2 kall og senere v2 side propagerer uten fallback`() {
        listOf(IllegalStateException("v2 API failure"), SocketTimeoutException("v2 timeout")).forEach { failure ->
            clearMocks(henvendelseInfoApi)
            every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
            every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } throws failure

            assertThatThrownBy {
                sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
            }.isSameAs(failure)
            verify(exactly = 0) {
                henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any())
            }
        }

        clearMocks(henvendelseInfoApi)
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(emptyList(), 1, 100, 2, true)
        val laterPageFailure = IllegalStateException("later v2 page failure")
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 2, 100) } throws laterPageFailure

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isSameAs(laterPageFailure)
        verify(exactly = 0) {
            henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), any(), any())
        }
    }

    @Test
    internal fun `v1 forste og senere sidefeil propagerer uten delvis resultat eller nytt forsok`() {
        val invalidV2 = listOf(referat("invalid", messages = emptyList()))
        val firstPageFailure = IllegalStateException("v1 first page failure")
        every { ansattService.hentAnsattFagomrader(any()) } returns emptySet()
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns page(invalidV2)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) } throws firstPageFailure

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isSameAs(firstPageFailure)
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) }

        clearMocks(henvendelseInfoApi)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteV2Get(any(), any(), 1, 100) } returns page(invalidV2)
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) } returns
            PaginertHenvendelseListeDTO(listOf(dummyHenvendelse), 1, 100, 2, true)
        val laterPageFailure = IllegalStateException("v1 later page failure")
        every { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 2, 100) } throws laterPageFailure

        assertThatThrownBy {
            sfHenvendelseServiceImpl.hentHenvendelser(EksternBruker.AktorId(dummyHenvendelse.aktorId), "0101")
        }.isSameAs(laterPageFailure)
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 1, 100) }
        verify(exactly = 1) { henvendelseInfoApi.henvendelseinfoHenvendelselisteGet(any(), any(), 2, 100) }
    }

    private fun withFallbackLogAppender(block: (ListAppender<ILoggingEvent>) -> Unit) {
        val logger = LoggerFactory.getLogger(SfHenvendelseServiceImpl::class.java) as Logger
        val appender = ListAppender<ILoggingEvent>()
        appender.context = logger.loggerContext
        appender.start()
        logger.addAppender(appender)
        try {
            block(appender)
        } finally {
            logger.detachAppender(appender)
            appender.stop()
        }
    }

    private fun referat(
        messageId: String?,
        chainId: String = messageId ?: "missing-chain",
        theme: String = "DAG",
        sentDate: OffsetDateTime = dummyHenvendelse.meldinger!!.single().sendtDato,
        messages: List<MeldingDTO> = listOf(dummyHenvendelse.meldinger!!.single().copy(meldingsId = messageId, sendtDato = sentDate)),
    ): HenvendelseDTO =
        dummyHenvendelse.copy(
            henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
            kjedeId = chainId,
            gjeldendeTemagruppe = theme,
            meldinger = messages,
        )

    private fun page(data: List<HenvendelseDTO>): PaginertHenvendelseListeDTO = PaginertHenvendelseListeDTO(data, 1, 100, 1, false)

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
