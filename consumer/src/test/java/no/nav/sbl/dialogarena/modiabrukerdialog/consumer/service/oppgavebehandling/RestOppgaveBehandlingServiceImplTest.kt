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
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.kodeverksmapper.KodeverksmapperService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.RestOppgaveBehandlingServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.RestOppgaveBehandlingServiceImpl.Companion.KONTAKT_NAV
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.rest.RestOppgaveBehandlingServiceImpl.Companion.SPORSMAL_OG_SVAR
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.junit.jupiter.api.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate.now
import java.util.*

class RestOppgaveBehandlingServiceImplTest {
    private val apiClient: OppgaveApi = mockk()
    private val systemApiClient: OppgaveApi = mockk()
    private val kodeverksmapperService: KodeverksmapperService = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val tilgangskontrollContext: TilgangskontrollContext = mockk()
    private val tilgangskontroll: Tilgangskontroll = Tilgangskontroll(tilgangskontrollContext)
    private val ansattService: AnsattService = mockk()
    private val arbeidsfordelingService: ArbeidsfordelingV1Service = mockk()
    private val stsService: SystemUserTokenProvider = mockk()

    private val oppgaveBehandlingService = RestOppgaveBehandlingServiceImpl(
        kodeverksmapperService,
        pdlOppslagService,
        ansattService,
        arbeidsfordelingService,
        tilgangskontroll,
        stsService,
        apiClient,
        systemApiClient
    )

    @BeforeEach
    fun setupStandardMocker() {
        every { kodeverksmapperService.mapUnderkategori(any()) } returns Optional.empty()
        every { kodeverksmapperService.mapOppgavetype(any()) } returns "SPM_OG_SVR"
        every { pdlOppslagService.hentIdent(any()) } answers {
            val ident = this.args[0] as String
            HentIdent.Identliste(
                listOf(
                    HentIdent.IdentInformasjon("000${ident}000", HentIdent.IdentGruppe.AKTORID),
                    HentIdent.IdentInformasjon("07063000250", HentIdent.IdentGruppe.FOLKEREGISTERIDENT)
                )
            )
        }
    }

    private val dummyOppgave = OppgaveJsonDTO(
        id = 1234,
        aktivDato = now(),
        oppgavetype = "",
        prioritet = OppgaveJsonDTO.Prioritet.HOY,
        status = OppgaveJsonDTO.Status.AAPNET,
        tildeltEnhetsnr = "",
        beskrivelse = "eksisterende beskrivelse",
        versjon = 1
    )

    @Test
    fun `skal opprette oppgave`() {
        every { apiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()

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
                underkategoriKode = "KNA",
                opprettetavenhetsnummer = "4100",
                oppgaveFrist = now(),
                valgtEnhetsId = "",
                behandlingskjedeId = "",
                dagerFrist = 3,
                ansvarligIdent = "",
                ansvarligEnhetId = ""
            )
        )

        assertThat(response.id).isEqualTo("1234")
        verifySequence {
            apiClient.opprettOppgave(
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
            )
        }
    }

    @Test
    fun `skal opprette skjermet oppgave`() {
        every { apiClient.opprettOppgave(any(), any()) } returns dummyOppgave.toPostOppgaveResponseJsonDTO()

        val response = oppgaveBehandlingService.opprettSkjermetOppgave(
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

        assertThat(response.id).isEqualTo("1234")
        verifySequence {
            apiClient.opprettOppgave(
                xminusCorrelationMinusID = any(),
                postOppgaveRequestJsonDTO = PostOppgaveRequestJsonDTO(
                    aktoerId = "00007063000250000",
                    behandlesAvApplikasjon = "FS22",
                    beskrivelse = "beskrivelse",
                    tema = "KNA",
                    oppgavetype = "SPM_OG_SVR",
                    prioritet = PostOppgaveRequestJsonDTO.Prioritet.NORM,
                    opprettetAvEnhetsnr = "4100",
                    aktivDato = now(),
                    fristFerdigstillelse = now()
                )
            )
        }
    }

    @Test
    fun `skal hente og tilordne oppgave, setter 4100 som standard enhet`() {
        every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
        every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

        SubjectHandlerUtil.withIdent("Z999999") {
            oppgaveBehandlingService.tilordneOppgaveIGsak(
                "1234",
                Temagruppe.FMLI,
                "4110"
            )
        }

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
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

        SubjectHandlerUtil.withIdent("Z999999") {
            oppgaveBehandlingService.tilordneOppgaveIGsak(
                "1234",
                Temagruppe.ANSOS,
                "4110"
            )
        }

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                    endretAvEnhetsnr = "4110",
                    tilordnetRessurs = "Z999999"
                )
            )
        }
    }

    @Test
    fun `skal ferdigstille oppgave uten beskrivelse`() {
        every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave.toGetOppgaveResponseJsonDTO()
        every { apiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()
        every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"

        SubjectHandlerUtil.withIdent("Z999999") {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(
                "1234",
                Temagruppe.ANSOS,
                "4110"
            )
        }

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
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

        SubjectHandlerUtil.withIdent("Z999999") {
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
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
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

        SubjectHandlerUtil.withIdent("Z999999") {
            oppgaveBehandlingService.ferdigstillOppgaverIGsak(
                mutableListOf("1234"),
                Optional.of(Temagruppe.ANSOS),
                "4110"
            )
        }

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            apiClient.endreOppgave(
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
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
    fun `systemet legger tilbake oppgave uten endringer`() {
        every { apiClient.hentOppgave(any(), any()) } returns dummyOppgave
            .copy(tilordnetRessurs = "Z999999")
            .toGetOppgaveResponseJsonDTO()
        every { systemApiClient.endreOppgave(any(), any(), any()) } returns dummyOppgave.toPutOppgaveResponseJsonDTO()

        oppgaveBehandlingService.systemLeggTilbakeOppgaveIGsak(
            "1234",
            Temagruppe.ARBD,
            "4100"
        )

        verifySequence {
            apiClient.hentOppgave(any(), 1234)
            systemApiClient.endreOppgave(
                any(), 1234, dummyOppgave.toPutOppgaveRequestJsonDTO().copy(
                    tilordnetRessurs = null,
                    endretAvEnhetsnr = "4100"
                )
            )
        }
    }

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
                xminusCorrelationMinusID = any(),
                statuskategori = "AAPEN",
                tema = listOf(KONTAKT_NAV),
                oppgavetype = listOf(SPORSMAL_OG_SVAR),
                tilordnetRessurs = "Z999999"
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
                xminusCorrelationMinusID = any(),
                statuskategori = "AAPEN",
                tema = listOf(KONTAKT_NAV),
                oppgavetype = listOf(SPORSMAL_OG_SVAR),
                tilordnetRessurs = "Z999999"
            )
            systemApiClient.endreOppgave(
                any(), 1234, henvendelseOppgave.toPutOppgaveRequestJsonDTO().copy(
                    tilordnetRessurs = null,
                    endretAvEnhetsnr = "4100"
                )
            )
        }
    }

    @Test
    fun `skal legge tilbake oppgave`() {
        val testoppgave = dummyOppgave
            .copy(tilordnetRessurs = "Z999999", aktoerId = "00007063000250000")
        every { apiClient.hentOppgave(any(), any()) } returns testoppgave.toGetOppgaveResponseJsonDTO()
        every { apiClient.endreOppgave(any(), any(), any()) } returns testoppgave.toPutOppgaveResponseJsonDTO()
        every { ansattService.hentAnsattNavn(eq("Z999999")) } returns "Fornavn Etternavn"
        every { arbeidsfordelingService.finnBehandlendeEnhetListe(any(), any(), any(), any()) } returns listOf(
            AnsattEnhet("4567", "NAV Mockenhet")
        )

        SubjectHandlerUtil.withIdent("Z999999") {
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
                any(), 1234, testoppgave.toPutOppgaveRequestJsonDTO().copy(
                    tilordnetRessurs = null,
                    beskrivelse = testoppgave.nybeskrivelse(
                        ident = "Z999999",
                        navn = "Fornavn Etternavn",
                        enhet = "4110",
                        tekst = "ny beskrivelse"
                    ),
                    endretAvEnhetsnr = "4110",
                    tildeltEnhetsnr = "4567",
                    temagruppe = "ANSOS"
                )
            )
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

        SubjectHandlerUtil.withIdent("Z999999") {
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
                any(), 1234, testoppgave.toPutOppgaveRequestJsonDTO().copy(
                    tilordnetRessurs = null,
                    beskrivelse = testoppgave.nybeskrivelse(
                        ident = "Z999999",
                        navn = "Fornavn Etternavn",
                        enhet = "4110",
                        tekst = "ny beskrivelse"
                    ),
                    endretAvEnhetsnr = "4110",
                    tildeltEnhetsnr = "4567",
                    temagruppe = "ANSOS"
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

        SubjectHandlerUtil.withIdent("Z999999") {
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
                any(), 1234, testoppgave.toPutOppgaveRequestJsonDTO().copy(
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

private fun <T> withIdent(ident: String, fn: () -> T): T {
    return SubjectHandlerUtil.withIdent(ident, fn)
}

private fun OppgaveJsonDTO.nybeskrivelse(ident: String, navn: String, enhet: String, tekst: String): String {
    val header = String.format(
        "--- %s %s (%s, %s) ---",
        DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print(DateTime.now()),
        navn,
        ident,
        enhet
    )
    return "$header\n$tekst\n\n${this.beskrivelse}"
}
