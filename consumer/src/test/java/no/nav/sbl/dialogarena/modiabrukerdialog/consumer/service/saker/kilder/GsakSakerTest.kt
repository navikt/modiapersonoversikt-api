package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import com.nhaarman.mockitokotlin2.any
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.SakerServiceImpl
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.FodselnummerAktorService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import java.util.*

class GsakSakerTest {
    @Mock
    private val sakV1: SakV1? = null

    @Mock
    private val behandleSak: BehandleSakV1? = null

    @Mock
    private val gsakKodeverk: GsakKodeverk? = null

    @Mock
    private val standardKodeverk: StandardKodeverk? = null

    @Mock
    private val arbeidOgAktivitet: ArbeidOgAktivitet? = null

    @Mock
    private val behandleHenvendelsePortType: BehandleHenvendelsePortType? = null

    @Mock
    private val unleashService: UnleashService? = null

    @Mock
    private val psakService: PsakService? = null

    @Mock
    private val fodselnummerAktorService: FodselnummerAktorService? = null

    @Mock
    private val sakApiGateway: SakApiGateway? = null


    @InjectMocks
    private val sakerService: SakerServiceImpl? = null
    private var sakerListe: List<SakDto>? = null

    @BeforeEach
    @Throws(FinnSakUgyldigInput::class, FinnSakForMangeForekomster::class)
    fun setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC)
        MockitoAnnotations.initMocks(this)
        sakerService!!.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        sakerListe = createSaksliste()
        Mockito.`when`(sakApiGateway!!.hentSaker(any())).thenReturn(sakerListe)
        Mockito.`when`(arbeidOgAktivitet!!.hentSakListe(ArgumentMatchers.any(WSHentSakListeRequest::class.java))).thenReturn(WSHentSakListeResponse())
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
        MatcherAssert.assertThat(sak.saksId, Is.`is`(SakId_1))
        MatcherAssert.assertThat(sak.fagsystemSaksId, Is.`is`(nullValue()))
        MatcherAssert.assertThat(sak.temaKode, Is.`is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        MatcherAssert.assertThat(sak.sakstype, Is.`is`(Sak.SAKSTYPE_GENERELL))
        MatcherAssert.assertThat(sak.fagsystemKode, Is.`is`(Sak.FAGSYSTEMKODE_PSAK))
        MatcherAssert.assertThat(sak.opprettetDato, Is.`is`(FIRE_DAGER_SIDEN))
        MatcherAssert.assertThat(sak.finnesIGsak, Is.`is`(true))
    }

    @Test
    fun `transformasjonen BrukerSaksId For FagsystemId Og MFS Som Sakstype Om Fagsystem Er Vedtakslosningen`() {
        val sakDto = SakDto(id = SakId_1,
                tema = "AAP",
                applikasjon = VEDTAKSLOSNINGEN,
                aktoerId = "123",
                orgnr = null,
                fagsakNr = FagsystemSakId_1,
                opprettetAv = null,
                opprettetTidspunkt = FIRE_DAGER_SIDEN)

        val sak = GsakSaker.TIL_SAK.invoke(sakDto)
        MatcherAssert.assertThat(sak.saksId, Is.`is`(SakId_1))
        MatcherAssert.assertThat(sak.fagsystemSaksId, Is.`is`(SakId_1))
        MatcherAssert.assertThat(sak.temaKode, Is.`is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        MatcherAssert.assertThat(sak.sakstype, Is.`is`(Sak.SAKSTYPE_MED_FAGSAK))
        MatcherAssert.assertThat(sak.fagsystemKode, Is.`is`(VEDTAKSLOSNINGEN))
        MatcherAssert.assertThat(sak.opprettetDato, Is.`is`(FIRE_DAGER_SIDEN))
        MatcherAssert.assertThat(sak.finnesIGsak, Is.`is`(true))
    }

    @Test
    @Throws(Exception::class)
    fun knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsak() {
        val sak = lagSak()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        Mockito.`when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        Mockito.verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet)
    }

    @Test
    @Throws(Exception::class)
    fun knytterBehandlingsKjedeTilSakUavhengigOmDenFinnesIGsakUtenFagsystemId() {
        val sak = lagSakUtenFagsystemId()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        Mockito.`when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        Mockito.verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilSak(BEHANDLINGSKJEDEID, SAKS_ID, sak.temaKode, valgtNavEnhet)
    }

    @Test
    @Throws(Exception::class)
    fun knyttBehandlingskjedeTilSakKallerAlternativMetodeOmBidragsHackSakenErValgt() {
        val valgtNavEnhet = "0219"
        val sak = Sak()
        sak.syntetisk = true
        sak.fagsystemKode = Sak.BIDRAG_MARKOR
        sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        Mockito.verify(behandleSak, Mockito.never())!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))
        Mockito.verify(behandleHenvendelsePortType, Mockito.never())!!.knyttBehandlingskjedeTilSak(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        Mockito.verify(behandleHenvendelsePortType, Mockito.times(1))!!.knyttBehandlingskjedeTilTema(BEHANDLINGSKJEDEID, "BID")
    }

    @Test
    @Throws(OpprettSakUgyldigInput::class, OpprettSakSakEksistererAllerede::class)
    fun knyttBehandlingskjedeTilSakKasterFeilHvisEnhetIkkeErSatt() {
        Mockito.`when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(FNR, BEHANDLINGSKJEDEID, lagSak(), "") }
    }

    @Test
    @Throws(OpprettSakUgyldigInput::class, OpprettSakSakEksistererAllerede::class)
    fun knyttBehandlingskjedeTilSakKasterFeilHvisBehandlingskjedeIkkeErSatt() {
        Mockito.`when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(FNR, null, lagSak(), "1337") }
    }

    @Test
    @Throws(OpprettSakUgyldigInput::class, OpprettSakSakEksistererAllerede::class)
    fun knyttBehandlingskjedeTilSakKasterFeilHvisFnrIkkeErSatt() {
        Mockito.`when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak("", BEHANDLINGSKJEDEID, lagSak(), "1337") }
    }

    @Test
    fun skalHandtereMangledeGagsystemSakId() {
        val sakDto = SakDto()
        Assertions.assertDoesNotThrow { GsakSaker.TIL_SAK.invoke(sakDto) }
    }

    companion object {

        fun createSaksliste(): List<SakDto>? {
            return ArrayList(listOf(
                    SakDto(id = SakId_1,
                            tema = "AAP",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_1,
                            opprettetAv = null,
                            opprettetTidspunkt = FIRE_DAGER_SIDEN),

                    SakDto(id = SakId_2,
                            tema = "AGR",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_2,
                            opprettetAv = null,
                            opprettetTidspunkt = DateTime.now().minusDays(3)),

                    SakDto(id = SakId_3,
                            tema = "AAP",
                            applikasjon = "IT01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = FagsystemSakId_3,
                            opprettetAv = null,
                            opprettetTidspunkt = DateTime.now().minusDays(5)),

                    SakDto(id = SakId_4,
                            tema = "STO",
                            applikasjon = "",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = null,
                            opprettetAv = null,
                            opprettetTidspunkt = DateTime.now().minusDays(5))))
        }

        fun lagSak(): Sak {
            val sak = Sak()
            sak.temaKode = "GEN"
            sak.finnesIGsak = false
            sak.fagsystemKode = Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }

        private fun lagSakUtenFagsystemId(): Sak {
            val sak = Sak()
            sak.temaKode = "STO"
            sak.finnesIGsak = false
            sak.fagsystemKode = ""
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }


        fun createOppfolgingSaksliste(): MutableList<SakDto> {

            return ArrayList(listOf(
                    SakDto(id = "4",
                            tema = "OPP",
                            applikasjon = "AO01",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = "44",
                            opprettetAv = null,
                            opprettetTidspunkt = DateTime()),

                    SakDto(id = "5",
                            tema = "OPP",
                            applikasjon = "FS22",
                            aktoerId = "123",
                            orgnr = null,
                            fagsakNr = null,
                            opprettetAv = null,
                            opprettetTidspunkt = DateTime.now().minusDays(3))))

        }

        private const val VEDTAKSLOSNINGEN = "FS36"
        private val FIRE_DAGER_SIDEN = DateTime.now().minusDays(4)
        private const val FNR = "fnr"
        private const val BEHANDLINGSKJEDEID = "behandlingsKjedeId"
        const val SAKS_ID = "123"
        const val SakId_1 = "1"
        const val FagsystemSakId_1 = "11"
        const val SakId_2 = "2"
        const val FagsystemSakId_2 = "22"
        const val SakId_3 = "3"
        const val FagsystemSakId_3 = "33"
        const val SakId_4 = "4"
        const val FagsystemSakId_4 = "44"
    }


}

