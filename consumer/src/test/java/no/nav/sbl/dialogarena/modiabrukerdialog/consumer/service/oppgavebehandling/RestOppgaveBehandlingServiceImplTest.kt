package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import io.mockk.*
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.Response
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toGetOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPostOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.toPutOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.RestOppgaveBehandlingServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalDate.now
import java.util.*

class RestOppgaveBehandlingServiceImplTest {
    val apiClient: OppgaveApi = mockk()
    val systemApiClient: OppgaveApi = mockk()
    val kodeverksmapperService: KodeverksmapperService = mockk()
    val pdlOppslagService: PdlOppslagService = mockk()
    val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(RestOppgaveMockFactory.tilgangskontrollContext)
    val ansattService: AnsattService = mockk()
    val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    val stsService: SystemUserTokenProvider = mockk()

    val oppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
        kodeverksmapperService,
        pdlOppslagService,
        ansattService,
        arbeidsfordelingService,
        tilgangskontroll,
        stsService,
        apiClient,
        systemApiClient
    )

    @Test
    fun `skal opprette oppgave`() {
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { pdlOppslagService.hentIdent(any()) } returns HentIdent.Identliste(
            listOf(
                HentIdent.IdentInformasjon(
                    "00007063000250000",
                    HentIdent.IdentGruppe.AKTORID
                )
            )
        )
        every { apiClient.opprettOppgave(any(), any()) } returns PostOppgaveResponseJsonDTO(
            id = 123,
            aktivDato = now(),
            oppgavetype = "",
            prioritet = PostOppgaveResponseJsonDTO.Prioritet.HOY,
            status = PostOppgaveResponseJsonDTO.Status.AAPNET,
            tildeltEnhetsnr = "",
            versjon = 1
        )

        val response: OpprettOppgaveResponse = oppgaveBehandlingService.opprettOppgave(
            OpprettOppgaveRequest(
                fnr = "07063000250",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                temagruppe = "",
                tema = "KNA",
                oppgavetype = "SPM_OG_SVR",
                behandlingstype = "",
                prioritet = "NORM",
                underkategoriKode = "",
                opprettetavenhetsnummer = "4100",
                oppgaveFrist = now(),
                valgtEnhetsId = "",
                behandlingskjedeId = "",
                dagerFrist = 3,
                ansvarligIdent = "",
                ansvarligEnhetId = ""
            )
        )

        assertThat(response.id).isEqualTo("123")
        verify { apiClient.opprettOppgave(
            xminusCorrelationMinusID = any(),
            postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                opprettetAvEnhetsnr = "4100",
                aktoerId = "00007063000250000",
                behandlesAvApplikasjon = "FS22",
                beskrivelse = "beskrivelse",
                tema = "KNA",
                oppgavetype = "SPM_OG_SVR",
                aktivDato = now(),
                fristFerdigstillelse = now(),
                prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM
            )
        ) }
    }

    @Test
    fun `skal opprette skjermet oppgave`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.opprettOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOpprettOppgaveResponseSkjermet.toPostOppgaveResponseJsonDTO()
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { pdlOppslagService.hentIdent(any()) } returns HentIdent.Identliste(
            listOf(
                HentIdent.IdentInformasjon(
                    "07063000250",
                    HentIdent.IdentGruppe.AKTORID
                )
            )
        )

        SubjectHandlerUtil.withIdent("Z999998") {
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
                    oppgaveFrist = now()
                )
            )
        }

        verify {
            apiClient.opprettOppgave(
                any(), PostOppgaveRequestJsonDTO(
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = "beskrivelse",
                    temagruppe = "",
                    tema = "KNA",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                    tilordnetRessurs = "",
                    tildeltEnhetsnr = "",
                    opprettetAvEnhetsnr = "4100",
                    behandlingstema = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now()
                )
            )
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, setter 4100 som standard enhet`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgave.toGetOppgaveResponseJsonDTO()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgaveIGsak(
                "1234",
                Temagruppe.FMLI,
                "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.patchOppgave(
                any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4100",
                    tilordnetRessurs = "Z999998"
                )
            )
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, bruker saksbehandlers valgte enhet for ANSOS etc `() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgave.toGetOppgaveResponseJsonDTO()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgaveIGsak(
                "1234",
                Temagruppe.ANSOS,
                "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.patchOppgave(
                any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    tilordnetRessurs = "Z999998"
                )
            )
        }
    }

    @Test
    fun `skal ferdigstille oppgave uten beskrivelse`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every {
            apiClient.patchOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveFerdigstiltUtenBeskrivelse
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                "1234",
                Temagruppe.ANSOS,
                "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format(
                        "--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110"
                    ) + "Oppgaven er ferdigstilt i Modia. " + "\n\n" + "beskrivelse",
                    temagruppe = "ARBD_KNA",
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4110",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = "Z999998"
                )
            )
            apiClient.patchOppgave(
                any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT
                )
            )
        }
    }

    @Test
    fun `skal ferdigstille oppgave med beskrivelse`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every {
            apiClient.patchOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveFerdigstiltMedBeskrivelse
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                "1234",
                Optional.of(Temagruppe.ANSOS),
                "4110",
                "ny beskrivelse"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format(
                        "--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110"
                    ) + "Oppgaven er ferdigstilt i Modia. " + "ny beskrivelse" + "\n\n" + "beskrivelse",
                    temagruppe = "ARBD_KNA",
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4110",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = "Z999998"
                )
            )
            apiClient.patchOppgave(
                any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT
                )
            )
        }
    }

    @Test
    fun `skal ferdigstille oppgaver`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every { apiClient.patchOppgaver(any(), any()) } returns RestOppgaveMockFactory.mockOppgaverFerdigstilt
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgaverIGsak(
                mutableListOf("1234"),
                Optional.of(Temagruppe.ANSOS),
                "4110"
            )
        }

        verify {
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let { apiClient.hentOppgave(any(), it) }
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let {
                apiClient.endreOppgave(
                    any(), it, PutOppgaveRequestJsonDTO(
                        id = it,
                        tildeltEnhetsnr = "4100",
                        aktoerId = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = String.format(
                            "--- %s %s (%s, %s) ---\n",
                            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                            ansattService.hentAnsattNavn("Z999998"),
                            "Z999998",
                            "4110"
                        ) + "Oppgaven er ferdigstilt i Modia. " + "\n\n" + "beskrivelse",
                        temagruppe = "ARBD_KNA",
                        tema = "KNA",
                        behandlingstema = "",
                        oppgavetype = "SPM_OG_SVR",
                        behandlingstype = "",
                        aktivDato = now(),
                        fristFerdigstillelse = now(),
                        prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                        endretAvEnhetsnr = "4110",
                        status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                        versjon = 1,
                        tilordnetRessurs = "Z999998"
                    )
                )
            }
            apiClient.patchOppgaver(
                any(), PatchOppgaverRequestJsonDTO(
                    oppgaver = listOf(PatchJsonDTO(versjon = 1, id = 1234)),
                    status = PatchOppgaverRequestJsonDTO.Status.FERDIGSTILT,
                    endretAvEnhetsnr = "4110"
                )
            )
        }
    }

    @Test
    fun `systemet legger tilbake oppgave uten endringer`() {
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(
                "1234",
                Temagruppe.ARBD,
                "4100"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = "beskrivelse",
                    temagruppe = "ARBD_KNA",
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4100",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = ""
                )
            )
        }
    }

    @Test
    fun `skal finne tildelte oppgaver`() {
        every {
            apiClient.finnOppgaver(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any()
            )
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every { kodeverksmapperService.mapOppgavetype(any()) } returns ""
        every { RestOppgaveMockFactory.tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
            listOf(
                Response(
                    Decision.Permit,
                    emptyList()
                )
            )
        )
        every { pdlOppslagService.hentIdent(any()) } returns HentIdent.Identliste(
            listOf(
                HentIdent.IdentInformasjon(
                    "07063000250",
                    HentIdent.IdentGruppe.FOLKEREGISTERIDENT
                )
            )
        )


        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.finnTildelteOppgaverIGsak()
        }

        verify {
            apiClient.finnOppgaver(
                xminusCorrelationMinusID = any(),
                statuskategori = "AAPEN",
                tema = listOf("KNA"),
                oppgavetype = listOf("SPM_OG_SVR"),
                tilordnetRessurs = "Z999998",
                status = null,
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
                aktoerId = null,
                journalpostId = null,
                saksreferanse = null,
                opprettetAv = null,
                opprettetAvEnhetsnr = null,
                aktivDatoFom = null,
                aktivDatoTom = null,
                opprettetFom = null,
                opprettetTom = null,
                ferdigstiltFom = null,
                ferdigstiltTom = null,
                fristFom = null,
                fristTom = null,
                orgnr = null,
                sorteringsfelt = null,
                limit = null,
                offset = null
            )
        }
    }

    @Test
    fun `skal legge tilbake tilordnet oppgave uten tilgang`() {
        every {
            apiClient.finnOppgaver(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any()
            )
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns ""
        every { RestOppgaveMockFactory.tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(
            listOf(
                Response(
                    Decision.Deny,
                    emptyList()
                )
            )
        )

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.finnTildelteOppgaverIGsak()
        }

        verify {
            apiClient.finnOppgaver(
                xminusCorrelationMinusID = any(),
                statuskategori = "AAPEN",
                tema = listOf("KNA"),
                oppgavetype = listOf("SPM_OG_SVR"),
                tilordnetRessurs = "Z999998",
                status = null,
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
                aktoerId = null,
                journalpostId = null,
                saksreferanse = null,
                opprettetAv = null,
                opprettetAvEnhetsnr = null,
                aktivDatoFom = null,
                aktivDatoTom = null,
                opprettetFom = null,
                opprettetTom = null,
                ferdigstiltFom = null,
                ferdigstiltTom = null,
                fristFom = null,
                fristTom = null,
                orgnr = null,
                sorteringsfelt = null,
                limit = null,
                offset = null
            )
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let { apiClient.hentOppgave(any(), it) }
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let {
                apiClient.endreOppgave(
                    any(), it, PutOppgaveRequestJsonDTO(
                        id = it,
                        tildeltEnhetsnr = "4100",
                        aktoerId = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = "beskrivelse",
                        temagruppe = "ARBD_KNA",
                        tema = "KNA",
                        behandlingstema = "",
                        oppgavetype = "SPM_OG_SVR",
                        behandlingstype = "",
                        aktivDato = now(),
                        fristFerdigstillelse = now(),
                        prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                        endretAvEnhetsnr = "4100",
                        status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                        versjon = 1,
                        tilordnetRessurs = ""
                    )
                )
            }
        }
    }

    @Test
    fun `skal legge tilbake oppgave`() {
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toGetOppgaveResponseJsonDTO()
        every {
            apiClient.finnOppgaver(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any()
            )
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every { ansattService.hentAnsattNavn(any()) } returns ""
        every {
            arbeidsfordelingService.finnBehandlendeEnhetListe(
                any(),
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockAnsattEnhetListe
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockLeggTilbakeOppgaveResponse.toPutOppgaveResponseJsonDTO()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                LeggTilbakeOppgaveIGsakRequest()
                    .withSaksbehandlersValgteEnhet("4110")
                    .withOppgaveId("1234")
                    .withBeskrivelse("ny beskrivelse")
                    .withTemagruppe(Temagruppe.ANSOS)
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.finnOppgaver(
                xminusCorrelationMinusID = any(),
                statuskategori = "AAPEN",
                tema = listOf("KNA"),
                oppgavetype = listOf("SPM_OG_SVR"),
                tilordnetRessurs = "Z999998",
                status = null,
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
                aktoerId = null,
                journalpostId = null,
                saksreferanse = null,
                opprettetAv = null,
                opprettetAvEnhetsnr = null,
                aktivDatoFom = null,
                aktivDatoTom = null,
                opprettetFom = null,
                opprettetTom = null,
                ferdigstiltFom = null,
                ferdigstiltTom = null,
                fristFom = null,
                fristTom = null,
                orgnr = null,
                sorteringsfelt = null,
                limit = null,
                offset = null
            )
            apiClient.endreOppgave(
                any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format(
                        "--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110"
                    ) + "ny beskrivelse" + "\n\n" + "beskrivelse",
                    temagruppe = Temagruppe.ANSOS.name,
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4110",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = ""
                )
            )
        }

    }

    @Test
    fun `oppgave er ferdigstilt`() {
        every {
            apiClient.hentOppgave(
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgave.toGetOppgaveResponseJsonDTO()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.oppgaveErFerdigstilt(
                "1234"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
        }
    }

    @Test
    fun `skal legge tilbake oppgave med endret temagruppe`() {
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every {
            arbeidsfordelingService.finnBehandlendeEnhetListe(
                any(),
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockAnsattEnhetListe
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                LeggTilbakeOppgaveIGsakRequest()
                    .withSaksbehandlersValgteEnhet("4110")
                    .withOppgaveId("1234")
                    .withBeskrivelse("ny beskrivelse")
                    .withTemagruppe(Temagruppe.ANSOS)
            )
        }

        verify {
            apiClient.endreOppgave(
                any(), any(), PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format(
                        "--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110"
                    ) + "ny beskrivelse" + "\n\n" + "beskrivelse",
                    temagruppe = Temagruppe.ANSOS.name,
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = LocalDate.now(),
                    fristFerdigstillelse = LocalDate.now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4110",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = ""
                )
            )
        }
    }

    @Test
    fun `skal legge tilbake oppgave uten endret temagruppe`() {
        every {
            apiClient.endreOppgave(
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
        every {
            arbeidsfordelingService.finnBehandlendeEnhetListe(
                any(),
                any(),
                any(),
                any()
            )
        } returns RestOppgaveMockFactory.mockAnsattEnhetListe
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                LeggTilbakeOppgaveIGsakRequest()
                    .withSaksbehandlersValgteEnhet("4110")
                    .withOppgaveId("1234")
                    .withBeskrivelse("ny beskrivelse")
            )
        }

        verify {
            apiClient.endreOppgave(
                any(), any(), PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format(
                        "--- %s %s (%s, %s) ---\n",
                        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                        ansattService.hentAnsattNavn("Z999998"),
                        "Z999998",
                        "4110"
                    ) + "ny beskrivelse" + "\n\n" + "beskrivelse",
                    temagruppe = "ARBD_KNA",
                    tema = "KNA",
                    behandlingstema = "",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    aktivDato = LocalDate.now(),
                    fristFerdigstillelse = LocalDate.now(),
                    prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                    endretAvEnhetsnr = "4100",
                    status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                    versjon = 1,
                    tilordnetRessurs = ""
                )
            )
        }
    }

    @Test
    @DisplayName("Sjekker om innlogget saksbehandler er samme som saksbehandler som er ansvarlig for oppgaven")
    fun `skal kaste feil om saksbehandler ikke har tilgang til oppgaven`() {
        assertThrows<ResponseStatusException> {

            every {
                apiClient.endreOppgave(
                    any(),
                    any(),
                    any()
                )
            } returns RestOppgaveMockFactory.mockOppgaveResponse.toPutOppgaveResponseJsonDTO()
            every {
                arbeidsfordelingService.finnBehandlendeEnhetListe(
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns RestOppgaveMockFactory.mockAnsattEnhetListe
            every { ansattService.hentAnsattNavn(any()) } returns ""

            SubjectHandlerUtil.withIdent("Z999997") {
                oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                    LeggTilbakeOppgaveIGsakRequest()
                        .withSaksbehandlersValgteEnhet("4110")
                        .withOppgaveId("1234")
                        .withBeskrivelse("ny beskrivelse")
                        .withTemagruppe(Temagruppe.ANSOS)
                )
            }

            verify {
                apiClient.endreOppgave(
                    any(), any(), PutOppgaveRequestJsonDTO(
                        id = 1234,
                        tildeltEnhetsnr = "4100",
                        aktoerId = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = String.format(
                            "--- %s %s (%s, %s) ---\n",
                            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                            ansattService.hentAnsattNavn("Z999998"),
                            "Z999998",
                            "4110"
                        ) + "ny beskrivelse" + "\n\n" + "beskrivelse",
                        temagruppe = Temagruppe.ANSOS.name,
                        tema = "KNA",
                        behandlingstema = "",
                        oppgavetype = "SPM_OG_SVR",
                        behandlingstype = "",
                        aktivDato = now(),
                        fristFerdigstillelse = now(),
                        prioritet = PutOppgaveRequestJsonDTO.Prioritet.NORM,
                        endretAvEnhetsnr = "4110",
                        status = PutOppgaveRequestJsonDTO.Status.AAPNET,
                        versjon = 1,
                        tilordnetRessurs = ""
                    )
                )
            }

        }
    }
}

private fun <T> withIdent(ident: String, fn: () -> T): T {
    return SubjectHandlerUtil.withIdent(ident, fn)
}
