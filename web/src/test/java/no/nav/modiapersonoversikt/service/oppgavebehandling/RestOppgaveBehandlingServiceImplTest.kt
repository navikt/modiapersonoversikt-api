package no.nav.modiapersonoversikt.service.oppgavebehandling

import io.mockk.*
import no.nav.common.types.identer.NavIdent
import no.nav.modiapersonoversikt.commondomain.Temagruppe
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.config.interceptor.TjenestekallLoggingInterceptorFactory
import no.nav.modiapersonoversikt.consumer.oppgave.generated.apis.OppgaveApi
import no.nav.modiapersonoversikt.consumer.oppgave.generated.models.*
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService.AlleredeTildeltAnnenSaksbehandler
import no.nav.modiapersonoversikt.service.oppgavebehandling.Utils.SPORSMAL_OG_SVAR
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.modiapersonoversikt.testutils.AuthContextTestUtils
import no.nav.modiapersonoversikt.utils.BoundedMachineToMachineTokenClient
import no.nav.modiapersonoversikt.utils.BoundedOnBehalfOfTokenClient
import no.nav.personoversikt.common.kabac.Decision
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.*
import java.time.Clock
import java.time.Instant
import java.time.LocalDate.now
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

class RestOppgaveBehandlingServiceImplTest {
    private val unleashService: UnleashService = mockk()
    private val apiClient: OppgaveApi = mockk()
    private val systemApiClient: OppgaveApi = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val tilgangskontroll: Tilgangskontroll = TilgangskontrollMock.get()
    private val ansattService: AnsattService = mockk()
    private val oboTokenClient: BoundedOnBehalfOfTokenClient = mockk()
    private val machineToMachineTokenClient: BoundedMachineToMachineTokenClient = mockk()
    private val fixedClock = Clock.fixed(Instant.parse("2021-01-25T10:15:30Z"), ZoneId.systemDefault())
    private val tjenestekallLoggingInterceptorFactory: TjenestekallLoggingInterceptorFactory = mockk()

    private val oppgaveBehandlingService =
        RestOppgaveBehandlingServiceImpl(
            pdlOppslagService,
            ansattService,
            tilgangskontroll,
            oboTokenClient,
            machineToMachineTokenClient,
            tjenestekallLoggingInterceptorFactory,
            apiClient,
            systemApiClient,
            fixedClock,
        )

    @BeforeEach
    fun setupStandardMocker() {
        every { pdlOppslagService.hentAktorId(any()) } answers {
            val ident = this.args[0] as String
            "000${ident}000"
        }
        every { pdlOppslagService.hentFnr(any()) } answers {
            val ident = this.args[0] as String
            ident.substring(3, ident.length - 3)
        }
    }

    val dummyOppgave =
        OppgaveJsonDTO(
            id = 1234,
            aktoerId = "00007063000250000",
            aktivDato = now(fixedClock),
            oppgavetype = SPORSMAL_OG_SVAR,
            prioritet = OppgaveJsonDTO.Prioritet.HOY,
            status = OppgaveJsonDTO.Status.AAPNET,
            tildeltEnhetsnr = "",
            tema = "DAG",
            beskrivelse = "eksisterende beskrivelse",
            opprettetTidspunkt = OffsetDateTime.now(),
            versjon = 1,
        )

    @Nested
    inner class OpprettOppgave {
        @Test
        fun `skal opprette oppgave`() {
            every { apiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()
            every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
                Veileder(
                    ident = "Z999999",
                    fornavn = "Fornavn",
                    etternavn = "Etternavn",
                )

            val response: OpprettOppgaveResponse =
                withIdent("Z999999") {
                    oppgaveBehandlingService.opprettOppgave(
                        OpprettOppgaveRequest(
                            fnr = "07063000250",
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = "beskrivelse",
                            temagruppe = "",
                            tema = "KNA",
                            oppgavetype = "SPM_OG_SVR",
                            behandlingstype = "",
                            prioritet = OppgaveKodeverk.Prioritet.PrioritetKode.NORM,
                            underkategoriKode = "",
                            opprettetavenhetsnummer = "4100",
                            oppgaveFrist = now(fixedClock),
                            valgtEnhetsId = "",
                            behandlingskjedeId = "",
                            dagerFrist = 3,
                            ansvarligIdent = "",
                            ansvarligEnhetId = "",
                        ),
                    )
                }

            assertThat(response.id).isEqualTo("1234")
            verifySequence {
                apiClient.opprettOppgave(
                    xCorrelationID = any(),
                    postOppgaveRequestJsonDTO =
                        PostOppgaveRequestJsonDTO(
                            opprettetAvEnhetsnr = "4100",
                            aktoerId = "00007063000250000",
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = "--- 25.01.2021 11:15 Fornavn Etternavn (Z999999, 4100) ---\nbeskrivelse",
                            tema = "KNA",
                            oppgavetype = "SPM_OG_SVR",
                            aktivDato = now(fixedClock),
                            fristFerdigstillelse = now(fixedClock),
                            prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                        ),
                )
            }
        }

        @Test
        fun `skal opprette skjermet oppgave`() {
            every { systemApiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()
            every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
                Veileder(
                    ident = "Z999999",
                    fornavn = "Fornavn",
                    etternavn = "Etternavn",
                )

            val response =
                withIdent("Z999999") {
                    oppgaveBehandlingService.opprettSkjermetOppgave(
                        OpprettSkjermetOppgaveRequest(
                            fnr = "07063000250",
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = "beskrivelse",
                            temagruppe = "",
                            tema = "KNA",
                            oppgavetype = "SPM_OG_SVR",
                            behandlingstype = "",
                            prioritet = OppgaveKodeverk.Prioritet.PrioritetKode.NORM,
                            underkategoriKode = "",
                            opprettetavenhetsnummer = "4100",
                            oppgaveFrist = now(fixedClock),
                        ),
                    )
                }

            assertThat(response.id).isEqualTo("1234")
            verifySequence {
                systemApiClient.opprettOppgave(
                    xCorrelationID = any(),
                    postOppgaveRequestJsonDTO =
                        PostOppgaveRequestJsonDTO(
                            aktoerId = "00007063000250000",
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = "--- 25.01.2021 11:15 Fornavn Etternavn (Z999999, 4100) ---\nbeskrivelse",
                            tema = "KNA",
                            oppgavetype = "SPM_OG_SVR",
                            prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                            opprettetAvEnhetsnr = "4100",
                            aktivDato = now(fixedClock),
                            fristFerdigstillelse = now(fixedClock),
                        ),
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
                    "4110",
                    false,
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        endretAvEnhetsnr = "4100",
                        tilordnetRessurs = "Z999999",
                    ),
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
                    "4110",
                    false,
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        endretAvEnhetsnr = "4110",
                        tilordnetRessurs = "Z999999",
                    ),
                )
            }
        }

        @Test
        fun `skal kaste exception om oppgaven allerede er tilordnet saksbehandler`() {
            every { apiClient.hentOppgave(any(), any()) } returns
                dummyOppgave
                    .copy(tilordnetRessurs = "Z999998")
                    .toGetOppgaveResponseJsonDTO()

            assertThatThrownBy {
                withIdent("Z999999") {
                    oppgaveBehandlingService.tilordneOppgaveIGsak(
                        "1234",
                        Temagruppe.ANSOS,
                        "4110",
                        false,
                    )
                }
            }.isExactlyInstanceOf(AlleredeTildeltAnnenSaksbehandler::class.java)
        }

        @Test
        fun `skal ignorere allerede-tilordnet sjekk om tvungen tilordner er satt til true`() {
            every { apiClient.hentOppgave(any(), any()) } returns
                dummyOppgave
                    .copy(tilordnetRessurs = "Z999998")
                    .toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

            withIdent("Z999999") {
                oppgaveBehandlingService.tilordneOppgaveIGsak(
                    "1234",
                    Temagruppe.FMLI,
                    "4110",
                    true,
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        endretAvEnhetsnr = "4100",
                        tilordnetRessurs = "Z999999",
                    ),
                )
            }
        }
    }

    @Nested
    inner class FinnTildelteOppgave {
        @Test
        fun `skal finne tildelte oppgaver`() {
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 1,
                    oppgaver =
                        listOf(
                            dummyOppgave
                                .copy(
                                    aktoerId = "00007063000250000",
                                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                                ),
                        ),
                )

            val result: List<Oppgave> =
                withIdent("Z999999") {
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
                    offset = 0,
                )
            }
        }

        @Test
        fun `skal finne tildelte oppgaver tilknyttet aktorId`() {
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 1,
                    oppgaver =
                        listOf(
                            dummyOppgave
                                .copy(
                                    aktoerId = "00007063000250000",
                                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                                ),
                        ),
                )

            val result: List<Oppgave> =
                withIdent("Z999999") {
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
                    offset = 0,
                )
            }
        }

        @Test
        fun `skal finne tildelte oppgaver når det er flere enn 50`() {
            val antallTreffTotalt = 111
            val forventetSvarFraOppgave =
                List(antallTreffTotalt) {
                    dummyOppgave.copy(
                        id = (1234 + it).toLong(),
                        aktoerId = "00007063000250000",
                        metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                    )
                }.chunked(49)
            every { apiClient.finnOppgaver(allAny()) } returnsMany
                forventetSvarFraOppgave.map { oppgaveListe ->
                    GetOppgaverResponseJsonDTO(
                        antallTreffTotalt = antallTreffTotalt.toLong(),
                        oppgaver = oppgaveListe,
                    )
                }

            val result: List<Oppgave> =
                withIdent("Z999999") {
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
                    offset = 0,
                )
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 49,
                )
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    statuskategori = "AAPEN",
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    limit = 49,
                    offset = 98,
                )
            }
        }

        @Test
        fun `skal legge tilbake tilordnet oppgave uten tilgang`() {
            val henvendelseOppgave =
                dummyOppgave
                    .copy(
                        aktoerId = "00007063000250000",
                        metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                    )
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 1,
                    oppgaver = listOf(henvendelseOppgave),
                )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any(),
                )
            } returns henvendelseOppgave.toPutOppgaveResponseJsonDTO()
            val result =
                TilgangskontrollMock.withDecision(Decision.Deny("", Decision.NO_APPLICABLE_POLICY_FOUND)) {
                    withIdent("Z999999") {
                        oppgaveBehandlingService.finnTildelteOppgaverIGsak()
                    }
                }

            assertThat(result).isEmpty()
            verifySequence {
                apiClient.finnOppgaver(
                    xCorrelationID = any(),
                    tilordnetRessurs = "Z999999",
                    aktivDatoTom = now(fixedClock).toString(),
                    statuskategori = "AAPEN",
                    limit = 49,
                    offset = 0,
                )
                systemApiClient.endreOppgave(
                    any(),
                    1234,
                    henvendelseOppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = "4100",
                    ),
                )
            }
        }

        @Test
        @DisplayName("oppgave tilknyttet orgnr istedetfor aktorId skal ikke automatisk legges tilbake")
        fun `oppgave tilknyttet orgnr`() {
            val henvendelseOppgave =
                dummyOppgave
                    .copy(
                        aktoerId = null,
                        orgnr = "123456",
                        metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                    )
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 1,
                    oppgaver = listOf(henvendelseOppgave),
                )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any(),
                )
            } returns henvendelseOppgave.toPutOppgaveResponseJsonDTO()

            val result =
                withIdent("Z999999") {
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
                    offset = 0,
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
            val oppgave =
                dummyOppgave
                    .copy(
                        aktoerId = "00007063000250000",
                        metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                    )
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 1,
                    oppgaver = listOf(oppgave),
                )
            every {
                systemApiClient.endreOppgave(
                    any(),
                    any(),
                    any(),
                )
            } returns oppgave.toPutOppgaveResponseJsonDTO()

            every { pdlOppslagService.hentAktorId(any()) } returns null
            every { pdlOppslagService.hentFnr(any()) } returns null

            val result =
                withIdent("Z999999") {
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
                    offset = 0,
                )
                systemApiClient.endreOppgave(
                    any(),
                    1234,
                    oppgave.toPutOppgaveRequestJsonDTO().copy(
                        tilordnetRessurs = null,
                        endretAvEnhetsnr = "4100",
                    ),
                )
            }
        }

        @Test
        fun `skal filtere bort oppgaver som ikke har henvendelse tilknyttning`() {
            every { apiClient.finnOppgaver(allAny()) } returns
                GetOppgaverResponseJsonDTO(
                    antallTreffTotalt = 3,
                    oppgaver =
                        listOf(
                            dummyOppgave
                                .copy(
                                    id = 1111,
                                    aktoerId = "00007063000250000",
                                ),
                            dummyOppgave
                                .copy(
                                    aktoerId = "00007063000250000",
                                    metadata = mapOf(MetadataKey.EKSTERN_HENVENDELSE_ID.name to "henvid"),
                                ),
                            dummyOppgave
                                .copy(
                                    id = 1114,
                                    aktoerId = "00007063000250000",
                                ),
                        ),
                )

            val result: List<Oppgave> =
                withIdent("Z999999") {
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
                    offset = 0,
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
            every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
                Veileder(
                    ident = "Z999999",
                    fornavn = "Fornavn",
                    etternavn = "Etternavn",
                )

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110",
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse =
                            dummyOppgave.nybeskrivelse(
                                ident = NavIdent("Z999999"),
                                navn = "Fornavn Etternavn",
                                enhet = "4110",
                                tekst = "Oppgaven er ferdigstilt i Modia. ",
                            ),
                        endretAvEnhetsnr = "4110",
                    ),
                )
            }
        }

        @Test
        fun `skal ferdigstille oppgave med beskrivelse`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
                Veileder(
                    ident = "Z999999",
                    fornavn = "Fornavn",
                    etternavn = "Etternavn",
                )

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                    "1234",
                    Optional.of(Temagruppe.ANSOS),
                    "4110",
                    "ny beskrivelse",
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse =
                            dummyOppgave.nybeskrivelse(
                                ident = NavIdent("Z999999"),
                                navn = "Fornavn Etternavn",
                                enhet = "4110",
                                tekst = "Oppgaven er ferdigstilt i Modia. ny beskrivelse",
                            ),
                        endretAvEnhetsnr = "4110",
                    ),
                )
            }
        }

        @Test
        fun `skal ferdigstille oppgaver`() {
            every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
            every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
            every { ansattService.hentVeileder(eq(NavIdent("Z999999"))) } returns
                Veileder(
                    ident = "Z999999",
                    fornavn = "Fornavn",
                    etternavn = "Etternavn",
                )

            withIdent("Z999999") {
                oppgaveBehandlingService.ferdigstillOppgaverIGsak(
                    mutableListOf("1234"),
                    Optional.of(Temagruppe.ANSOS),
                    "4110",
                )
            }

            verifySequence {
                apiClient.hentOppgave(any(), 1234)
                apiClient.endreOppgave(
                    any(),
                    1234,
                    dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                        status = PutOppgaveRequestJsonDTO.Status.FERDIGSTILT,
                        beskrivelse =
                            dummyOppgave.nybeskrivelse(
                                ident = NavIdent("Z999999"),
                                navn = "Fornavn Etternavn",
                                enhet = "4110",
                                tekst = "Oppgaven er ferdigstilt i Modia. ",
                            ),
                        endretAvEnhetsnr = "4110",
                    ),
                )
            }
        }
    }

    @Test
    fun `oppgave er ferdigstilt`() {
        every { apiClient.hentOppgave(any(), any()) } returnsMany
            listOf(
                dummyOppgave.copy(status = OppgaveJsonDTO.Status.FERDIGSTILT).toGetOppgaveResponseJsonDTO(),
                dummyOppgave.copy(status = OppgaveJsonDTO.Status.AAPNET).toGetOppgaveResponseJsonDTO(),
            )

        val ferdig = withIdent("Z999999") { oppgaveBehandlingService.oppgaveErFerdigstilt("1234") }
        val ikkeferdig = withIdent("Z999999") { oppgaveBehandlingService.oppgaveErFerdigstilt("1234") }

        assertThat(ferdig).isTrue
        assertThat(ikkeferdig).isFalse

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.hentOppgave(any(), 1234)
        }
    }

    private fun <T> withIdent(
        ident: String,
        fn: () -> T,
    ): T = AuthContextTestUtils.withIdent(ident, fn)

    private fun OppgaveJsonDTO.nybeskrivelse(
        ident: NavIdent,
        navn: String,
        enhet: String,
        tekst: String,
    ): String =
        Utils.leggTilBeskrivelse(
            this.beskrivelse,
            Utils.beskrivelseInnslag(ident, navn, enhet, tekst, fixedClock),
        )
}
