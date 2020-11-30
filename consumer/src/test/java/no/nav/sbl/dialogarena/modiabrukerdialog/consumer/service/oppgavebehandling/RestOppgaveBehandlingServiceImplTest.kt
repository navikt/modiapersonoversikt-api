package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.abac.AbacRequest
import no.nav.sbl.dialogarena.abac.AbacResponse
import no.nav.sbl.dialogarena.abac.Decision
import no.nav.sbl.dialogarena.abac.Response
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.apis.OppgaveApi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaveResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.GetOppgaverResponseJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.OppgaveJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppgave.generated.models.PatchOppgaveRequestJsonDTO
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.RestOppgaveMockFactory.lagOppgave
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling.RestOppgaveMockFactory.mockHentOppgaveResponseMedTilordning
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Collections
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContext
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.slf4j.MDC
import java.util.stream.Collectors

class RestOppgaveBehandlingServiceImplTest {
    val SAKSBEHANDLERS_VALGTE_ENHET = "4100"
    val aktivStatus = "AAPEN"

    @Captor
    var ferdigstillOppgaveBolkRequestCaptor: ArgumentCaptor<WSFerdigstillOppgaveBolkRequest>? = null

    @Captor
    var lagreOppgaveRequestCaptor: ArgumentCaptor<PatchOppgaveRequestJsonDTO>? = null

    @Captor
    var tildelFlereOppgaverRequestCaptor: ArgumentCaptor<WSTildelFlereOppgaverRequest>? = null

    @Captor
    var hentOppgaveRequestCaptor: ArgumentCaptor<OppgaveJsonDTO>? = null

    @Mock
    private val ansatt: AnsattService? = null

    @Mock
    private val oppgave: OppgaveApi? = null

    @Mock
    private val oppgaveJsonDTO: OppgaveJsonDTO? = null

    @Mock
    private val oppgavebehandling: OppgavebehandlingV3? = null

    @Mock
    private val tildelOppgave: TildelOppgaveV1? = null

    // Kan ikke bruke `@Mock` siden vi er avhengig av at verdien er definert ved opprettelsen av `Tilgangskontroll`
    private val tilgangskontrollContext = mock(TilgangskontrollContext::class.java)

    @Spy
    private val tilgangskontroll = Tilgangskontroll(tilgangskontrollContext)

    @InjectMocks
    private val restOppgaveBehandlingService: RestOppgaveBehandlingServiceImpl? = null

    private val OPPGAVE_ID_1 = "123"
    private val OPPGAVE_ID_2 = "456"

    @BeforeEach
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class, LagreOppgaveOppgaveIkkeFunnet::class, LagreOppgaveOptimistiskLasing::class)
    fun skalHenteSporsmaalOgTilordneOppgave() {
        `when`(oppgave!!.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveJsonDTO?.id.toString().toLong()
        )).thenReturn(mockHentOppgaveResponse())

        SubjectHandlerUtil.withIdent("Z999999") {
            restOppgaveBehandlingService!!.tilordneOppgave(
                    "oppgaveid",
                    Temagruppe.ARBD,
                    SAKSBEHANDLERS_VALGTE_ENHET
            )
        }

        verify<OppgaveApi>(OppgaveApi()).patchOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveJsonDTO?.id.toString().toLong(),
                patchOppgaveRequestJsonDTO = lagreOppgaveRequestCaptor!!.capture()
        )
        val request: PatchOppgaveRequestJsonDTO? = lagreOppgaveRequestCaptor!!.value
        Assert.assertThat(request?.tilordnetRessurs, Matchers.`is`("Z999999"))
        Assert.assertThat(request?.endretAvEnhetsnr, Matchers.`is`(RestOppgaveBehandlingServiceImpl.DEFAULT_ENHET.toString()))
    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class)
    fun skalPlukkeOppgaver() {
        val tildelFlereOppgaverResponse = WSTildelFlereOppgaverResponse()
                .withOppgaveIder(Integer.valueOf(OPPGAVE_ID_1), Integer.valueOf(OPPGAVE_ID_2))

        val lagOppgave1 = lagOppgave().copy(id = OPPGAVE_ID_1.toLong())

        val hentOppgaveResponse1: GetOppgaveResponseJsonDTO = OppgaveApi().hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = lagOppgave1.id.toString().toLong()
        )

        val lagOppgave2 = lagOppgave().copy(id = OPPGAVE_ID_2.toLong())
        val hentOppgaveResponse2: GetOppgaveResponseJsonDTO = OppgaveApi().hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = lagOppgave2.id.toString().toLong()
        )

        `when`<WSTildelFlereOppgaverResponse>(tildelOppgave?.tildelFlereOppgaver(ArgumentMatchers.any(WSTildelFlereOppgaverRequest::class.java)))
                .thenReturn(tildelFlereOppgaverResponse)
        `when`<GetOppgaveResponseJsonDTO>(oppgave!!.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = ArgumentMatchers.any(oppgaveJsonDTO?.id.toString().toLong()::class.java)))
                .thenReturn(hentOppgaveResponse1, hentOppgaveResponse2)

        SubjectHandlerUtil.withIdent<List<OppgaveResponse>>("Z999999"
        ) { restOppgaveBehandlingService!!.plukkOppgaver(Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET) }

        verify(tildelOppgave)!!.tildelFlereOppgaver(tildelFlereOppgaverRequestCaptor!!.capture())

        verify(oppgave, times(2))!!.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = hentOppgaveRequestCaptor!!.capture().id!!.toLong())

        Assert.assertThat(hentOppgaveRequestCaptor!!.allValues.stream()
                .map { obj: OppgaveJsonDTO -> obj.id.toString() }
                .collect(Collectors.toSet()),
                Matchers.`is`(Collections.asSet(OPPGAVE_ID_1, OPPGAVE_ID_2)))
        Assert.assertNotNull(tildelFlereOppgaverRequestCaptor!!.value)
        Assert.assertThat(tildelFlereOppgaverRequestCaptor!!.value.fagomrade, Matchers.`is`("KNA"))
        Assert.assertThat(tildelFlereOppgaverRequestCaptor!!.value.oppgavetype, Matchers.`is`("SPM_OG_SVR"))
    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class)
    fun skalFerdigstilleOppgaver() {
        `when`(oppgave!!.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = ArgumentMatchers.any(oppgaveJsonDTO!!.id!!.toLong()::class.java))
        ).thenReturn(mockHentOppgaveResponse())
        `when`(ansatt!!.hentAnsattNavn(anyString())).thenReturn("")

        SubjectHandlerUtil.withIdent("Z999999"
        ) { restOppgaveBehandlingService!!.ferdigstillOppgave("1", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET) }

        verify(oppgavebehandling)!!.ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequestCaptor!!.capture())
        Assert.assertThat(ferdigstillOppgaveBolkRequestCaptor!!.value.oppgaveIdListe[0], Matchers.`is`("1"))
    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class, LagreOppgaveOptimistiskLasing::class, LagreOppgaveOppgaveIkkeFunnet::class)
    fun systemetLeggerTilbakeOppgaveIGsakUtenEndringer() {
        `when`<GetOppgaveResponseJsonDTO>(oppgave!!.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = ArgumentMatchers.any(oppgaveJsonDTO?.id.toString().toLong()::class.java))
        ).thenReturn(mockHentOppgaveResponseMedTilordning().toGetOppgaveResponseJsonDTO())
        restOppgaveBehandlingService!!.systemLeggTilbakeOppgave("1", Temagruppe.ARBD, SAKSBEHANDLERS_VALGTE_ENHET)
        verify<OppgaveJsonDTO>(OppgaveApi().patchOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = lagreOppgaveRequestCaptor!!.value.id,
                patchOppgaveRequestJsonDTO = lagreOppgaveRequestCaptor!!.capture()
        ))
        val endreOppgave: OppgaveJsonDTO = OppgaveApi().patchOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = lagreOppgaveRequestCaptor!!.value.id,
                patchOppgaveRequestJsonDTO = lagreOppgaveRequestCaptor!!.value
        )
        Assert.assertThat(endreOppgave.beskrivelse, Matchers.`is`(mockHentOppgaveResponseMedTilordning().beskrivelse))
    }

    private fun mockHentOppgaveResponse(): GetOppgaveResponseJsonDTO {
        return lagOppgave().toGetOppgaveResponseJsonDTO()
    }

    @Test
    fun skalKonvertereFraOppgaveJsonDTOTilEndreOppgave() {
        val oppgaveJsonDTO = lagOppgave()

        val endreOppgave = RestOppgaveBehandlingServiceImpl.endreOppgave(oppgaveJsonDTO)

        Assert.assertThat(endreOppgave.id, Matchers.`is`<Long>(oppgaveJsonDTO.id))
        Assert.assertThat(endreOppgave.tildeltEnhetsnr, Matchers.`is`<String>(oppgaveJsonDTO.tildeltEnhetsnr))
        Assert.assertThat(endreOppgave.aktoerId, Matchers.`is`<String>(oppgaveJsonDTO.aktoerId))
        Assert.assertThat(endreOppgave.behandlesAvApplikasjon, Matchers.`is`<String>(oppgaveJsonDTO.behandlesAvApplikasjon))
        Assert.assertThat(endreOppgave.beskrivelse, Matchers.`is`<String>(oppgaveJsonDTO.beskrivelse))
        Assert.assertThat(endreOppgave.temagruppe, Matchers.`is`<String>(oppgaveJsonDTO.temagruppe))
        Assert.assertThat(endreOppgave.tema, Matchers.`is`<String>(oppgaveJsonDTO.tema))
        Assert.assertThat(endreOppgave.behandlingstema, Matchers.`is`<String>(oppgaveJsonDTO.behandlingstema))
        Assert.assertThat(endreOppgave.oppgavetype, Matchers.`is`<String>(oppgaveJsonDTO.oppgavetype))
        Assert.assertThat(endreOppgave.behandlingstype, Matchers.`is`<String>(oppgaveJsonDTO.behandlingstype))
        Assert.assertThat(endreOppgave.aktivDato, Matchers.`is`<java.time.LocalDate>(oppgaveJsonDTO.aktivDato))
        Assert.assertThat(endreOppgave.fristFerdigstillelse, Matchers.`is`<java.time.LocalDate>(oppgaveJsonDTO.fristFerdigstillelse))
        Assert.assertThat(endreOppgave.prioritet.value, Matchers.`is`<String>(oppgaveJsonDTO.prioritet.value))
        Assert.assertThat(endreOppgave.endretAvEnhetsnr, Matchers.`is`<String>(oppgaveJsonDTO.endretAvEnhetsnr))
        Assert.assertThat(endreOppgave.status.value, Matchers.`is`<String>(oppgaveJsonDTO.status.value))
        Assert.assertThat(endreOppgave.versjon, Matchers.`is`<Int>(oppgaveJsonDTO.versjon))
        Assert.assertThat(endreOppgave.tilordnetRessurs, Matchers.`is`<String>(oppgaveJsonDTO.tilordnetRessurs))

    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class)
    fun skalFinneTilordnaOppgave() {
        val oppgaveliste: List<OppgaveJsonDTO> = listOf(
                lagOppgave().copy(id = "1".toLong(), aktoerId = "10108000398"),
                lagOppgave().copy(id = "2".toLong(), aktoerId = "10108000398")
        )

        `when`(oppgave!!.finnOppgaver(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                aktoerId = oppgaveliste.mapNotNull { oppgave -> oppgave.aktoerId },
                statuskategori = aktivStatus,
                status = null,
                tema = null,
                oppgavetype = null,
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                tilordnetRessurs = null,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
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
        )).thenReturn(GetOppgaverResponseJsonDTO(oppgaveliste.size.toLong(), oppgaveliste))

        `when`(oppgave.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveJsonDTO?.id.toString().toLong()
        )).thenReturn(mockHentOppgaveResponseMedTilordning().toGetOppgaveResponseJsonDTO())

        `when`(tilgangskontrollContext.checkAbac(any(AbacRequest::class.java))).thenReturn(
                AbacResponse(listOf(Response(Decision.Permit, emptyList())))
        )

        val resultat = SubjectHandlerUtil.withIdent<List<OppgaveResponse>>("Z999999") { restOppgaveBehandlingService!!.finnTildelteOppgaver() }

        Assert.assertThat(resultat.size, Matchers.`is`(oppgaveliste.size))
        Assert.assertThat(resultat[0].oppgaveId, Matchers.`is`(oppgaveliste[0].id.toString()))
        Assert.assertThat(resultat[1].oppgaveId, Matchers.`is`(oppgaveliste[1].id.toString()))

    }

    @Test
    @Throws(HentOppgaveOppgaveIkkeFunnet::class)
    fun skalLeggeTilbakeTilordnetOppgaveUtenTilgang() {
        val oppgaveliste : List<OppgaveJsonDTO> = listOf(
                lagOppgave().copy(
                        id = "1".toLong(),
                        aktoerId = "10108000398"
                ),
                lagOppgave().copy(
                        id = "2".toLong(),
                        aktoerId = "10108000398"
                )
        )

        `when`(tilgangskontrollContext.checkAbac(any(AbacRequest::class.java))).thenReturn(
                AbacResponse(listOf(Response(Decision.Deny, emptyList())))
        )
        `when`(oppgave!!.finnOppgaver(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                aktoerId = oppgaveliste.mapNotNull { oppgave -> oppgave.aktoerId },
                statuskategori = aktivStatus,
                status = null,
                tema = null,
                oppgavetype = null,
                tildeltEnhetsnr = null,
                tildeltRessurs = null,
                tilordnetRessurs = null,
                behandlingstema = null,
                behandlingstype = null,
                erUtenMappe = null,
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
        )).thenReturn(GetOppgaverResponseJsonDTO(oppgaveliste.size.toLong(), oppgaveliste))

        `when`<GetOppgaveResponseJsonDTO>(oppgave.hentOppgave(
                xminusCorrelationMinusID = MDC.get(MDCConstants.MDC_CALL_ID),
                id = oppgaveJsonDTO?.id.toString().toLong()
        )).thenReturn(mockHentOppgaveResponseMedTilordning().toGetOppgaveResponseJsonDTO())

        val resultat = SubjectHandlerUtil.withIdent<List<OppgaveResponse>>("Z999999") { restOppgaveBehandlingService!!.finnTildelteOppgaver() }

        Assert.assertThat(resultat.size, Matchers.`is`(0))
    }

    fun OppgaveJsonDTO.toGetOppgaveResponseJsonDTO() : GetOppgaveResponseJsonDTO = GetOppgaveResponseJsonDTO(
            tildeltEnhetsnr = tildeltEnhetsnr,
            oppgavetype = oppgavetype,
            versjon = versjon,
            prioritet = GetOppgaveResponseJsonDTO.Prioritet.valueOf(OppgaveJsonDTO.Prioritet.valueOf(prioritet.value).value),
            status = GetOppgaveResponseJsonDTO.Status.valueOf(OppgaveJsonDTO.Status.valueOf(status.value).value),
            aktivDato = aktivDato,
            id = id,
            endretAvEnhetsnr = endretAvEnhetsnr,
            journalpostId = journalpostId,
            journalpostkilde = journalpostkilde,
            behandlesAvApplikasjon = behandlesAvApplikasjon,
            saksreferanse = saksreferanse,
            bnr = bnr,
            samhandlernr = samhandlernr,
            aktoerId = aktoerId,
            identer = identer,
            orgnr = orgnr,
            tilordnetRessurs = tilordnetRessurs,
            beskrivelse = beskrivelse,
            temagruppe = temagruppe,
            tema = tema,
            behandlingstema = behandlingstema,
            behandlingstype = behandlingstype,
            mappeId = mappeId,
            opprettetAv = opprettetAv,
            endretAv = endretAv,
            metadata = metadata,
            fristFerdigstillelse = fristFerdigstillelse,
            opprettetTidspunkt = opprettetTidspunkt,
            ferdigstiltTidspunkt = ferdigstiltTidspunkt,
            endretTidspunkt = endretTidspunkt
    )
}