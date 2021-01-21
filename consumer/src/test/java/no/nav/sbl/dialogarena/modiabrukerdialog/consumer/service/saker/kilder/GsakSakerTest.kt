package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import com.nhaarman.mockitokotlin2.any
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.BEHANDLINGSKJEDEID
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.FIRE_DAGER_SIDEN
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.FNR
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.FagsystemSakId_1
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.SAKS_ID
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.SakId_1
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.VEDTAKSLOSNINGEN
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.createSaksliste
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.lagSak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.lagSakUtenFagsystemId
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.verify

class GsakSakerTest {
    @Mock
    private lateinit var sakV1: SakV1

    @Mock
    private lateinit var behandleSak: BehandleSakV1

    @Mock
    private lateinit var gsakKodeverk: GsakKodeverk

    @Mock
    private lateinit var standardKodeverk: StandardKodeverk

    @Mock
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @Mock
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @Mock
    private lateinit var unleashService: UnleashService

    @Mock
    private lateinit var psakService: PsakService

    @Mock
    private lateinit var sakApiGateway: SakApiGateway


    @InjectMocks
    private val sakerService: SakerServiceImpl? = null
    private var sakerListe: List<SakDto>? = null

    @BeforeEach
    fun setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC)
        MockitoAnnotations.initMocks(this)
        sakerService!!.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        sakerListe = createSaksliste()
        Mockito.`when`(sakApiGateway.hentSaker(any())).thenReturn(sakerListe)
        Mockito.`when`(arbeidOgAktivitet.hentSakListe(ArgumentMatchers.any(WSHentSakListeRequest::class.java))).thenReturn(WSHentSakListeResponse())
    }

    @Test
    fun transformasjonenGenerererRelevanteFelter() {
        val sakDto = SakDto(id = SakId_1,
                tema = "AAP",
                applikasjon = "PP01",
                aktoerId = "123",
                orgnr = null,
                fagsakNr = null,
                opprettetAv = null,
                opprettetTidspunkt = FIRE_DAGER_SIDEN)
        val sak = GsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(nullValue()))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_GENERELL))
        assertThat(sak.fagsystemKode, `is`(Sak.FAGSYSTEMKODE_PSAK))
        assertThat(sak.opprettetDato, `is`(FIRE_DAGER_SIDEN))
        assertThat(sak.finnesIGsak, `is`(true))
    }

    @Test
    fun `transformasjonen brukerSaksId for fagsystemId og MFS som sakstype om fagsystem er vedtakslosningen`() {
        val sakDto = SakDto(id = SakId_1,
                tema = "AAP",
                applikasjon = VEDTAKSLOSNINGEN,
                aktoerId = "123",
                orgnr = null,
                fagsakNr = FagsystemSakId_1,
                opprettetAv = null,
                opprettetTidspunkt = FIRE_DAGER_SIDEN)

        val sak = GsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(SakId_1))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_MED_FAGSAK))
        assertThat(sak.fagsystemKode, `is`(VEDTAKSLOSNINGEN))
        assertThat(sak.opprettetDato, `is`(FIRE_DAGER_SIDEN))
        assertThat(sak.finnesIGsak, `is`(true))
    }

    @Test
    fun `knytter behandlingskjede til sak uavhengig om den finnesIGsak`() {
        val sak = lagSak()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        Mockito.`when`(behandleSak.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet)
    }

    @Test
    fun `knytter behandlingsKjede til sak uavhengig om den finnesIGsak uten fagsystemId`() {
        val sak = lagSakUtenFagsystemId()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        Mockito.`when`(behandleSak.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet)
    }

    @Test
    fun `knytt behandlingskjede til sak kaller alternativ metode om bidrags hack saken er valgt`() {
        val valgtNavEnhet = "0219"
        val sak = Sak()
        sak.syntetisk = true
        sak.fagsystemKode = Sak.BIDRAG_MARKOR
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        verify(behandleSak, Mockito.never())!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))
        verify(behandleHenvendelsePortType, Mockito.never())!!.knyttBehandlingskjedeTilSak(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilTema(BEHANDLINGSKJEDEID, "BID")
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis enhet ikke er satt`() {
        Mockito.`when`(behandleSak.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, lagSak(), "") }
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis behandlingskjede ikke er satt`() {
        Mockito.`when`(behandleSak.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(FNR, null, lagSak(), "1337") }
    }

    @Test
    fun `knytt Behandlingskjede til sak kaster feil hvis FNR ikke er satt`() {
        Mockito.`when`(behandleSak.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak("", BEHANDLINGSKJEDEID, lagSak(), "1337") }
    }

    @Test
    fun `skal handtere manglede fagsystemSakId`() {
        val sakDto = SakDto()
        Assertions.assertDoesNotThrow { GsakSaker.TIL_SAK.invoke(sakDto) }
    }


}

