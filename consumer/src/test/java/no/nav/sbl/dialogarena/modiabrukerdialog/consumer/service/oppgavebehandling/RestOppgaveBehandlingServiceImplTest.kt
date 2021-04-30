package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import io.mockk.*
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.Response
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.MetadataKey
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toGetOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPostOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPutOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPutOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.domain.Behandling
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.Utils.SPORSMAL_OG_SVAR
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.Instant
import java.time.LocalDate.now
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

class RestOppgaveBehandlingServiceImplTest {
    private val apiClient: OppgaveApi = mockk()
    private val systemApiClient: OppgaveApi = mockk()
    private val kodeverksmapperService: KodeverksmapperService = mockk()
    private val fodselnummerAktorService: FodselnummerAktorService = mockk()
    private val tilgangskontrollContext: TilgangskontrollContext = mockk()
    private val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(tilgangskontrollContext)
    private val ansattService: AnsattService = mockk()
    private val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    private val stsService: SystemUserTokenProvider = mockk()
    private val fixedClock = Clock.fixed(Instant.parse("2021-01-25T10:15:30Z"), ZoneId.systemDefault())

    private val oppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
        kodeverksmapperService,
        fodselnummerAktorService,
        ansattService,
        arbeidsfordelingService,
        tilgangskontroll,
        stsService,
        apiClient,
        systemApiClient,
        fixedClock
    )

    @BeforeEach
    fun setupStandardMocker() {
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { fodselnummerAktorService.hentAktorIdForFnr(any()) } answers {
            val ident = this.args[0] as String
            "000${ident}000"
        }
        every { fodselnummerAktorService.hentFnrForAktorId(any()) } answers {
            val ident = this.args[0] as String
            ident.substring(3, ident.length - 3)
        }
    }

    val dummyOppgave = OppgaveJsonDTO(
        id = 1234,
        aktoerId = "00007063000250000",
        aktivDato = now(fixedClock),
        oppgavetype = SPORSMAL_OG_SVAR,
        prioritet = OppgaveJsonDTO.Prioritet.HOY,
        status = OppgaveJsonDTO.Status.AAPNET,
        tildeltEnhetsnr = "",
        beskrivelse = "eksisterende beskrivelse",
        opprettetTidspunkt = OffsetDateTime.now(),
        versjon = 1
    )

    @Nested
    inner class OpprettOppgave {
        @Test
        fun `skal opprette oppgave`() {
            every { apiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

            val response: OpprettOppgaveResponse = withIdent("Z999999") {
                oppgaveBehandlingService.opprettOppgave(
                    OpprettOppgaveRequest(
                        fnr = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = "beskrivelse",
                        temagruppe = "",
                        tema = "KNA",
                        oppgavetype = "SPM_OG_SVR",
                        behandlingstype = "",
                        prioritet = "NORM",
                        underkategoriKode = "KNA",
                        opprettetavenhetsnummer = "4100",
                        oppgaveFrist = now(fixedClock),
                        valgtEnhetsId = "",
                        behandlingskjedeId = "",
                        dagerFrist = 3,
                        ansvarligIdent = "",
                        ansvarligEnhetId = ""
                    )
                )
            }

            assertThat(response.id).isEqualTo("1234")
            verifySequence {
                apiClient.opprettOppgave(
                    xCorrelationID = any(),
                    postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        opprettetAvEnhetsnr = "4100",
                        aktoerId = "00007063000250000",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = "--- 25.01.2021 11:15 Fornavn Etternavn (Z999999, 4100) ---\nbeskrivelse",
                        tema = "KNA",
                        oppgavetype = "SPM_OG_SVR",
                        aktivDato = now(fixedClock),
                        fristFerdigstillelse = now(fixedClock),
                        prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM
                    )
                )
            }
        }

        @Test
        fun `skal opprette skjermet oppgave`() {
            every { systemApiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

            val response = withIdent("Z999999") {
                oppgaveBehandlingService.opprettSkjermetOppgave(
                    OpprettSkjermetOppgaveRequest(
                        fnr = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = "beskrivelse",
                        temagruppe = "",
                        tema = "KNA",
                        oppgavetype = "SPM_OG_SVR",
                        behandlingstype = "",
                        prioritet = OppgaveJsonDTO.Prioritet.NORM.value,
                        underkategoriKode = "",
                        opprettetavenhetsnummer = "4100",
                        oppgaveFrist = now(fixedClock)
                    )
                )
            }

            assertThat(response.id).isEqualTo("1234")
            verifySequence {
                systemApiClient.opprettOppgave(
                    xCorrelationID = any(),
                    postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                        aktoerId = "00007063000250000",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = "--- 25.01.2021 11:15 Fornavn Etternavn (Z999999, 4100) ---\nbeskrivelse",
                        tema = "KNA",
                        oppgavetype = "SPM_OG_SVR",
                        prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                        opprettetAvEnhetsnr = "4100",
                        aktivDato = now(fixedClock),
                        fristFerdigstillelse = now(fixedClock)
                    )
                )
            }
        }
    }

    @Nested
    inner class TilordneOppgave {
        @Test
        fun `skal hente og tilordne oppgave, setter 4100 som standard enhet`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

            withIdent("Z999999") {
                oppgaveBehandlingService.tilordneOppgaveIGsak(
                    "1234",
                    Temagruppe.FMLI,
                    "4110"
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        endretAvEnhetsnr = "4100",
                        tilordnetRessurs = "Z999999"
                    )
                )
            }
        }

        @Test
        fun `skal hente og tilordne oppgave, bruker saksbehandlers valgte enhet for ANSOS etc `() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

            withIdent("Z999999") {
                oppgaveBehandlingService.tilordneOppgaveIGsak(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        endretAvEnhetsnr = "4110",
                        tilordnetRessurs = "Z999999"
                    )
                )
            }
        }
    }

    @Nested
    inner class FinnTildelteOppgave {
        @Test
        fun `skal finne tildelte oppgaver`() {
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(
                    dummyOppgave
                        .copy(
                            aktoerId = "00007063000250000",
                            metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                        )
                )
            )
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )

            val result: List<Oppgave> = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }
            val oppgave: Oppgave = result[0]

            assertThat(oppgave.oppgaveId).isEqualTo("1234")
            assertThat(oppgave.fnr).isEqualTo("07063000250")
            assertThat(oppgave.henvendelseId).isEqualTo("henvid")
            assertThat(oppgave.erSTOOppgave).isEqualTo(true)

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
            }
        }

        @Test
        fun `skal finne tildelte oppgaver tilknyttet aktorId`() {
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(
                    dummyOppgave
                        .copy(
                            aktoerId = "00007063000250000",
                            metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                        )
                )
            )
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )

            val result: List<Oppgave> = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak("07063000250")
            }
            val oppgave: Oppgave = result[0]

            assertThat(oppgave.oppgaveId).isEqualTo("1234")
            assertThat(oppgave.fnr).isEqualTo("07063000250")
            assertThat(oppgave.henvendelseId).isEqualTo("henvid")
            assertThat(oppgave.erSTOOppgave).isEqualTo(true)

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    aktoerId = listOf("00007063000250000"),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
            }
        }

        @Test
        fun `skal finne tildelte KNA oppgaver tilknyttet saksbehandler`() {
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(
                    dummyOppgave
                        .copy(
                            aktoerId = "00007063000250000",
                            metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                        )
                )
            )
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )

            val result: List<Oppgave> = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteKNAOppgaverIGsak()
            }
            val oppgave: Oppgave = result[0]

            assertThat(oppgave.oppgaveId).isEqualTo("1234")
            assertThat(oppgave.fnr).isEqualTo("07063000250")
            assertThat(oppgave.henvendelseId).isEqualTo("henvid")
            assertThat(oppgave.erSTOOppgave).isEqualTo(true)

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    oppgavetype = listOf("SPM_OG_SVR"),
                    tema = listOf("KNA"),
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
            }
        }

        @Test
        fun `skal finne tildelte oppgaver når det er flere enn 50`() {
            val antallTreffTotalt = 111
            val forventetSvarFraOppgave = List(antallTreffTotalt) {
                dummyOppgave.copy(
                    id = (1234 + it).toLong(),
                    aktoerId = "00007063000250000",
                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                )
            }.chunked(49)
            every { apiClient.finnOppgaver(allAny()) } returnsMany forventetSvarFraOppgave.map { oppgaveListe ->
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = antallTreffTotalt.toLong(),
                    oppgaver = oppgaveListe
                )
            }
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )

            val result: List<Oppgave> = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }
            val oppgave: Oppgave = result[0]

            assertThat(oppgave.oppgaveId).isEqualTo("1234")
            assertThat(oppgave.fnr).isEqualTo("07063000250")
            assertThat(oppgave.henvendelseId).isEqualTo("henvid")
            assertThat(oppgave.erSTOOppgave).isEqualTo(true)

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 49
                )
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 98
                )
            }
        }

        @Test
        fun `skal legge tilbake tilordnet oppgave uten tilgang`() {
            val henvendelseOppgave = dummyOppgave
                .copy(
                    aktoerId = "00007063000250000",
                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                )
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(henvendelseOppgave)
            )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any()
                )
            } returns henvendelseOppgave.toPutOppgaveResponseJsonDTO()

            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Deny, null))
            )

            val result = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }

            assertThat(result).isEmpty()
            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    statuskategori = "AAPEN",
                    limit = 49,
                    offset = 0
                )
                systemApiClient.endreOppgave(
                    any(),
                    1234,
                    henvendelseOppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = "4100"
                    )
                )
            }
        }

        @Test
        fun `oppgave tilknyttet orgnr istedetfor aktorId skal ikke automatisk legges tilbake`() {
            val henvendelseOppgave = dummyOppgave
                .copy(
                    aktoerId = null,
                    orgnr = "123456",
                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                )
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(henvendelseOppgave)
            )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any()
                )
            } returns henvendelseOppgave.toPutOppgaveResponseJsonDTO()

            val result = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }

            assertThat(result).isEmpty()
            verify {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    statuskategori = "AAPEN",
                    limit = 49,
                    offset = 0
                )
            }

            verify(exactly = 0) {
                // Skal ikke legge tilbake oppgaven siden den tilhører en org.nr.
                systemApiClient.endreOppgave(any(), any(), any())
            }

            confirmVerified(apiClient, systemApiClient)
        }

        @Test
        fun `skal legge tilbake oppgave om aktørId fra oppgave ikke finnes i PDL`() {
            val oppgave = dummyOppgave
                .copy(
                    aktoerId = "00007063000250000",
                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                )
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 1,
                oppgaver = listOf(oppgave)
            )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any()
                )
            } returns oppgave.toPutOppgaveResponseJsonDTO()

            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )
            every { fodselnummerAktorService.hentAktorIdForFnr(any()) } returns null
            every { fodselnummerAktorService.hentFnrForAktorId(any()) } returns null

            val result = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }

            assertThat(result).isEmpty()
            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
                systemApiClient.endreOppgave(
                    any(),
                    1234,
                    oppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = "4100"
                    )
                )
            }
        }

        @Test
        fun `skal filtere bort oppgaver som ikke har henvendelse tilknyttning`() {
            every { apiClient.finnOppgaver(allAny()) } returns GetOppgaverResponseJsonDTO(
                antallTreffTotalt = 3,
                oppgaver = listOf(
                    dummyOppgave
                        .copy(
                            id = 1111,
                            aktoerId = "00007063000250000"
                        ),
                    dummyOppgave
                        .copy(
                            aktoerId = "00007063000250000",
                            metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid")
                        ),
                    dummyOppgave
                        .copy(
                            id = 1114,
                            aktoerId = "00007063000250000"
                        )
                )
            )
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )

            val result: List<Oppgave> = withIdent("Z999999") {
                oppgaveBehandlingService.finnTildelteOppgaverIGsak()
            }
            val oppgave: Oppgave = result[0]

            assertThat(result).hasSize(1)
            assertThat(oppgave.oppgaveId).isEqualTo("1234")
            assertThat(oppgave.fnr).isEqualTo("07063000250")
            assertThat(oppgave.henvendelseId).isEqualTo("henvid")
            assertThat(oppgave.erSTOOppgave).isEqualTo(true)

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 0
                )
            }
        }
    }

    @Nested
    inner class PlukkOppgave {
        @Test
        fun `skal hente eldste oppgave`() {
            val eldsteOppgave = dummyOppgave.copy(
                id = 1111,
                opprettetTidspunkt = OffsetDateTime.now().minusDays(1)
            )
            every { tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
                listOf(Response(Decision.Permit, null))
            )
            every { apiClient.finnOppgaver(allAny()) } returnsMany listOf(
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 3,
                    oppgaver = listOf(dummyOppgave, dummyOppgave.copy(id = 1235), eldsteOppgave)
                ),
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 2,
                    oppgaver = listOf(dummyOppgave, dummyOppgave.copy(id = 1235)) // eldsteOppgave blir tilordnet i forkant
                )
            )
            every { apiClient.endreOppgave(any(), any(), any()) } answers {
                thirdArg<PutOppgaveRequestJsonDTO>().toPutOppgaveResponseJsonDTO()
            }
            every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.of(
                Behandling()
                    .withBehandlingstema("behandlingstema_ARBD")
                    .withBehandlingstype("behandlingstype_ARBD")
            )

            withIdent("Z999999") {
                oppgaveBehandlingService.plukkOppgaverFraGsak(Temagruppe.ARBD, "4110")
            }

            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    aktoerId = null,
                    statuskategori = "AAPEN",
                    tema = listOf("KNA"),
                    oppgavetype = listOf("SPM_OG_SVR"),
                    tildeltRessurs = false,
                    tildeltEnhetsnr = "4100",
                    ikkeTidligereTilordnetRessurs = "Z999999",
                    behandlingstema = "behandlingstema_ARBD",
                    behandlingstype = "behandlingstype_ARBD",
                    sorteringsfelt = "OPPRETTET_TIDSPUNKT",
                    sorteringsrekkefolge = "ASC",
                    limit = 20
                )
                apiClient.endreOppgave(
                    any(),
                    1111,
                    eldsteOppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = "Z999999",
                        endretAvEnhetsnr = "4100"
                    )
                )
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    aktoerId = listOf("00007063000250000"),
                    statuskategori = "AAPEN",
                    tema = listOf("KNA"),
                    oppgavetype = listOf("SPM_OG_SVR"),
                    tildeltRessurs = false,
                    tildeltEnhetsnr = "4100",
                    ikkeTidligereTilordnetRessurs = "Z999999",
                    behandlingstema = "behandlingstema_ARBD",
                    behandlingstype = "behandlingstype_ARBD",
                    sorteringsfelt = "OPPRETTET_TIDSPUNKT",
                    sorteringsrekkefolge = "ASC",
                    limit = 100
                )
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = "Z999999",
                        endretAvEnhetsnr = "4100"
                    )
                )
                apiClient.endreOppgave(
                    any(),
                    1235,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        id = 1235,
                        tilordnetRessurs = "Z999999",
                        endretAvEnhetsnr = "4100"
                    )
                )
            }
        }
    }

    @Nested
    inner class FerdigstillOppgave {
        @Test
        fun `skal ferdigstille oppgave uten beskrivelse`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse = dummyOppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "Oppgaven er ferdigstilt i Modia. "
                        ),
                        endretAvEnhetsnr = "4110"
                    )
                )
            }
        }

        @Test
        fun `skal ferdigstille oppgave med beskrivelse`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                    "1234",
                    Optional.of(Temagruppe.ANSOS),
                    "4110",
                    "ny beskrivelse"
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse = dummyOppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "Oppgaven er ferdigstilt i Modia. ny beskrivelse"
                        ),
                        endretAvEnhetsnr = "4110"
                    )
                )
            }
        }

        @Test
        fun `skal ferdigstille oppgaver`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaverIGsak(
                    mutableListOf("1234"),
                    Optional.of(Temagruppe.ANSOS),
                    "4110"
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse = dummyOppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "Oppgaven er ferdigstilt i Modia. "
                        ),
                        endretAvEnhetsnr = "4110"
                    )
                )
            }
        }
    }

    @Nested
    inner class LeggTilbakeOppgave {
        @Test
        fun `systemet legger tilbake oppgave uten endringer`() {
            every { systemApiClient.hentOppgave(any(), any()) } returns dummyOppgave
                .copy(tilordnetRessurs = "Z999999")
                .toGetOppgaveResponseJsonDTO()
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any()
                )
            } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

            oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(
                "1234",
                Temagruppe.ARBD,
                "4100"
            )

            verifySequence {
                systemApiClient.hentOppgave(any(), 1234)
                systemApiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = "4100"
                    )
                )
            }
        }

        @Test
        fun `skal legge tilbake oppgave`() {
            // Bygger opp `tilordnetRessurs` vha buildString for å sikre at jvm string-pool ikke gjenbruker instansen
            val tilordnetRessurs = buildString { append("Z999999") }
            val testoppgave = dummyOppgave
                .copy(tilordnetRessurs = tilordnetRessurs, aktoerId = "00007063000250000")
            every { apiClient.hentOppgave(any(), any()) } returns testoppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns testoppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"
            every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns listOf(
                AnsattEnhet("4567", "NAV Mockenhet")
            )

            withIdent("Z999999") {
                oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                    LeggTilbakeOppgaveIGsakRequest()
                        .withSaksbehandlersValgteEnhet("4110")
                        .withOppgaveId("1234")
                        .withBeskrivelse("ny beskrivelse")
                        .withTemagruppe(Temagruppe.ANSOS)
                )
            }

            verifyAll {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    testoppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        beskrivelse = testoppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "ny beskrivelse"
                        ),
                        endretAvEnhetsnr = "4110",
                        tildeltEnhetsnr = "4567"
                    )
                )
            }
        }

        @Test
        fun `skal legge tilbake oppgave med endret temagruppe`() {
            val testoppgave = dummyOppgave
                .copy(tilordnetRessurs = "Z999999", aktoerId = "00007063000250000")
            every { apiClient.hentOppgave(any(), any()) } returns testoppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns testoppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"
            every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns listOf(
                AnsattEnhet("4567", "NAV Mockenhet")
            )
            every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.of(
                Behandling()
                    .withBehandlingstema("behandlingstema_ANSOS")
                    .withBehandlingstype("behandlingstype_ANSOS")
            )

            withIdent("Z999999") {
                oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                    LeggTilbakeOppgaveIGsakRequest()
                        .withSaksbehandlersValgteEnhet("4110")
                        .withOppgaveId("1234")
                        .withBeskrivelse("ny beskrivelse")
                        .withTemagruppe(Temagruppe.ANSOS)
                )
            }

            verifyAll {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    testoppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        beskrivelse = testoppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "ny beskrivelse"
                        ),
                        endretAvEnhetsnr = "4110",
                        tildeltEnhetsnr = "4567",
                        behandlingstema = "behandlingstema_ANSOS",
                        behandlingstype = "behandlingstype_ANSOS"
                    )
                )
            }
        }

        @Test
        fun `skal legge tilbake oppgave uten endret temagruppe`() {
            val testoppgave = dummyOppgave
                .copy(tilordnetRessurs = "Z999999", aktoerId = "00007063000250000")
            every { apiClient.hentOppgave(any(), any()) } returns testoppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns testoppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"
            every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns listOf(
                AnsattEnhet("4567", "NAV Mockenhet")
            )

            withIdent("Z999999") {
                oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                    LeggTilbakeOppgaveIGsakRequest()
                        .withSaksbehandlersValgteEnhet("4110")
                        .withOppgaveId("1234")
                        .withBeskrivelse("ny beskrivelse")
                )
            }

            verifyAll {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    testoppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        beskrivelse = testoppgave.nybeskrivelse(
                            ident = "Z999999",
                            navn = "Fornavn Etternavn",
                            enhet = "4110",
                            tekst = "ny beskrivelse"
                        ),
                        endretAvEnhetsnr = "4100"
                    )
                )
            }
        }

        @Test
        fun `skal kaste feil om saksbehandler ikke har tilgang til oppgaven`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave
                .copy(tilordnetRessurs = "Z999999", aktoerId = "00007063000250000")
                .toGetOppgaveResponseJsonDTO()

            assertThatThrownBy {
                withIdent("Z999997") {
                    oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                        LeggTilbakeOppgaveIGsakRequest()
                            .withSaksbehandlersValgteEnhet("4110")
                            .withOppgaveId("1234")
                            .withBeskrivelse("ny beskrivelse")
                            .withTemagruppe(Temagruppe.ANSOS)
                    )
                }
            }
                .isExactlyInstanceOf(ResponseStatusException::class.java)
                .hasMessageContaining("Z999999")
                .hasMessageContaining("Z999997")
                .hasMessageContaining("1234")

            verifyAll {
                apiClient.hentOppgave(any(), 1234)
            }
        }
    }

    @Test
    fun `oppgave er ferdigstilt`() {
        every { apiClient.hentOppgave(any(), any()) } returnsMany listOf(
            dummyOppgave.copy(status = OppgaveJsonDTO.Status.FERDIGSTILT).toGetOppgaveResponseJsonDTO(),
            dummyOppgave.copy(status = OppgaveJsonDTO.Status.AAPNET).toGetOppgaveResponseJsonDTO()
        )

        val ferdig = withIdent("Z999999") { oppgaveBehandlingService.oppgaveErFerdigstilt("1234") }
        val ikkeferdig = withIdent("Z999999") { oppgaveBehandlingService.oppgaveErFerdigstilt("1234") }

        assertThat(ferdig).isTrue()
        assertThat(ikkeferdig).isFalse()

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.hentOppgave(any(), 1234)
        }
    }

    private fun <T> withIdent(ident: String, fn: () -> T): T {
        return SubjectHandlerUtil.withIdent(ident, fn)
    }

    private fun OppgaveJsonDTO.nybeskrivelse(ident: String, navn: String, enhet: String, tekst: String): String {
        return Utils.leggTilBeskrivelse(
            this.beskrivelse,
            Utils.beskrivelseInnslag(ident, navn, enhet, tekst, fixedClock)
        )
    }
}
