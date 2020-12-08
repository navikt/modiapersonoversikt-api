package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.Response
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OpprettOppgaveRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.RestOppgaveBehandlingService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.Test
import java.time.LocalDate.now
import java.util.*

class RestOppgaveBehandlingServiceImplTest {
    val apiClient: OppgaveApi = mockk()
    val kodeverksmapperService: KodeverksmapperService = mockk()
    val pdlOppslagService: PdlOppslagService = mockk()
    val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(RestOppgaveMockFactory.tilgangskontrollContext)
    val ansattService: AnsattService = mockk()
    val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    val stsService: SystemUserTokenProvider = mockk()
    val oppgaveBehandlingService: RestOppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
            apiClient,
            kodeverksmapperService,
            pdlOppslagService,
            tilgangskontroll,
            ansattService,
            arbeidsfordelingService,
            stsService
    )

    @Test
    fun `skal opprette oppgave`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.opprettOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOpprettOppgaveResponse.asPost()
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { pdlOppslagService.hentIdent(any()) } returns HentIdent.Identliste(listOf(HentIdent.IdentInformasjon("07063000250", HentIdent.IdentGruppe.AKTORID)))

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.opprettOppgave(
                    OpprettOppgaveRequest(
                            fnr = "07063000250",
                            behandlesAvApplikasjon = "FS22",
                            beskrivelse = "beskrivelse",
                            temagruppe = "ARBD_KNA",
                            tema = "KNA",
                            oppgavetype = "SPM_OG_SVR",
                            behandlingstype = "",
                            prioritet = OppgaveJsonDTO.Prioritet.NORM.value,
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
        }

        verify {
            apiClient.opprettOppgave(any(), any(), PostOppgaveRequestJsonDTO(
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = "beskrivelse",
                    temagruppe = "ARBD_KNA",
                    tema = "KNA",
                    oppgavetype = "SPM_OG_SVR",
                    behandlingstype = "",
                    prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                    tilordnetRessurs = "",
                    opprettetAvEnhetsnr = "4100",
                    behandlingstema = "",
                    aktivDato = now(),
                    fristFerdigstillelse = now(),
                    tildeltEnhetsnr = ""
            ))
        }

    }

    @Test
    fun `skal opprette skjermet oppgave`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.opprettOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOpprettOppgaveResponseSkjermet.asPost()
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { pdlOppslagService.hentIdent(any()) } returns HentIdent.Identliste(listOf(HentIdent.IdentInformasjon("07063000250", HentIdent.IdentGruppe.AKTORID)))

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.opprettSkjermetOppgave(
                    OpprettOppgaveRequest(
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
                            oppgaveFrist = now(),
                            valgtEnhetsId = "",
                            behandlingskjedeId = "",
                            dagerFrist = 3,
                            ansvarligIdent = "",
                            ansvarligEnhetId = ""
                    )
            )
        }

        verify {
            apiClient.opprettOppgave(any(), any(), PostOppgaveRequestJsonDTO(
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
            ))
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, setter 4100 som standard enhet`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgave.asGetResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgave(
                    "1234",
                    Temagruppe.FMLI,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.patchOppgave(any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4100",
                    tilordnetRessurs = "Z999998"
            ))
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, bruker saksbehandlers valgte enhet for ANSOS etc `() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgave.asGetResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgave

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.tilordneOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.patchOppgave(any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    tilordnetRessurs = "Z999998"
            ))
        }
    }

    @Test
    fun `skal ferdigstille oppgave uten beskrivelse`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveFerdigstiltUtenBeskrivelse
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format("--- %s %s (%s, %s) ---\n",
                            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                            ansattService.hentAnsattNavn("Z999998"),
                            "Z999998",
                            "4110") + "Oppgaven er ferdigstilt i Modia. " + "\n\n" + "beskrivelse",
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
            ))
            apiClient.patchOppgave(any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT
            ))
        }
    }

    @Test
    fun `skal ferdigstille oppgave med beskrivelse`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()
        every { apiClient.patchOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveFerdigstiltMedBeskrivelse
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgave(
                    "1234",
                    Temagruppe.ANSOS,
                    "4110",
                    "ny beskrivelse"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format("--- %s %s (%s, %s) ---\n",
                            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                            ansattService.hentAnsattNavn("Z999998"),
                            "Z999998",
                            "4110") + "Oppgaven er ferdigstilt i Modia. " + "ny beskrivelse" + "\n\n" + "beskrivelse",
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
            ))
            apiClient.patchOppgave(any(), 1234, PatchOppgaveRequestJsonDTO(
                    id = 1234,
                    versjon = 1,
                    endretAvEnhetsnr = "4110",
                    status = PatchOppgaveRequestJsonDTO.Status.FERDIGSTILT
            ))
        }
    }

    @Test
    fun `skal ferdigstille oppgaver`() {
        every { stsService.systemUserToken } returns "DummyToken"
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()
        every { apiClient.patchOppgaver(any(), any()) } returns RestOppgaveMockFactory.mockOppgaverFerdigstilt
        every { ansattService.hentAnsattNavn(any()) } returns ""

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.ferdigstillOppgaver(
                    listOf("1234"),
                    Temagruppe.ANSOS,
                    "4110"
            )
        }

        verify {
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let { apiClient.hentOppgave(any(), it) }
            RestOppgaveMockFactory.mockOppgaverResponse.oppgaver!![0].id?.let {
                apiClient.endreOppgave(any(), it, PutOppgaveRequestJsonDTO(
                        id = it,
                        tildeltEnhetsnr = "4100",
                        aktoerId = "07063000250",
                        behandlesAvApplikasjon = "FS22",
                        beskrivelse = String.format("--- %s %s (%s, %s) ---\n",
                                DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                                ansattService.hentAnsattNavn("Z999998"),
                                "Z999998",
                                "4110") + "Oppgaven er ferdigstilt i Modia. " + "\n\n" + "beskrivelse",
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
                ))
            }
            apiClient.patchOppgaver(any(), PatchOppgaverRequestJsonDTO(
                    oppgaver = listOf(PatchJsonDTO(versjon = 1, id = 1234)),
                    status = PatchOppgaverRequestJsonDTO.Status.FERDIGSTILT,
                    endretAvEnhetsnr = "4110"
            ))
        }
    }

    @Test
    fun `systemet legger tilbake oppgave uten endringer`() {
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.systemLeggTilbakeOppgave(
                    "1234",
                    Temagruppe.ARBD,
                    "4100"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(any(), 1234, PutOppgaveRequestJsonDTO(
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
            ))
        }
    }

    @Test
    fun `skal finne tildelte oppgaver`() {
        every { apiClient.finnOppgaver(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any())
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every { kodeverksmapperService.mapOppgavetype(any()) } returns ""
        every { RestOppgaveMockFactory.tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(listOf(Response(Decision.Permit, emptyList())))

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.finnTildelteOppgaver()
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
        every { apiClient.finnOppgaver(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any())
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asPutResponse()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns ""
        every { RestOppgaveMockFactory.tilgangskontrollContext.checkAbac(any()) } returns AbacResponse(listOf(Response(Decision.Deny, emptyList())))

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.finnTildelteOppgaver()
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
                apiClient.endreOppgave(any(), it, PutOppgaveRequestJsonDTO(
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
                ))
            }
        }
    }

    @Test
    fun `skal legge tilbake oppgave`() {
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgaveResponse.asGetResponse()
        every { apiClient.finnOppgaver(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any())
        } returns RestOppgaveMockFactory.mockOppgaverResponse
        every { ansattService.hentAnsattNavn(any()) } returns ""
        every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns RestOppgaveMockFactory.mockAnsattEnhetListe
        every { apiClient.endreOppgave(any(), any(), any()) } returns RestOppgaveMockFactory.mockLeggTilbakeOppgaveResponse.asPutResponse()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.leggTilbakeOppgave(
                     LeggTilbakeOppgaveRequest(
                             "4110",
                             "1234",
                             "ny beskrivelse",
                             Temagruppe.ANSOS
                     )
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
            apiClient.endreOppgave(any(), 1234, PutOppgaveRequestJsonDTO(
                    id = 1234,
                    tildeltEnhetsnr = "4100",
                    aktoerId = "07063000250",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = String.format("--- %s %s (%s, %s) ---\n",
                            DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
                            ansattService.hentAnsattNavn("Z999998"),
                            "Z999998",
                            "4110") + "ny beskrivelse" + "\n\n" + "beskrivelse",
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
            ))
        }

    }

    @Test
    fun `oppgave er ferdigstilt`() {
        every { apiClient.hentOppgave(any(), any()) } returns RestOppgaveMockFactory.mockOppgave.asGetResponse()

        SubjectHandlerUtil.withIdent("Z999998") {
            oppgaveBehandlingService.oppgaveErFerdigstilt(
                    "1234"
            )
        }

        verify {
            apiClient.hentOppgave(any(), 1234)
        }

    }


}
