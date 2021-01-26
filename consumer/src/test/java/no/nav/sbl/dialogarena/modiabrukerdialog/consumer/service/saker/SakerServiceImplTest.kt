package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker

import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.createOppfolgingSaksliste
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.createSaksliste
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.LocalDate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.Mockito.*
import org.slf4j.MDC
import java.util.*
import java.util.stream.Collectors
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class SakerServiceImplTest {
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
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @Mock
    private val psakService: PsakService? = null

    @Mock
    private val unleashService: UnleashService? = null

    @Mock
    private val sakApiGateway: SakApiGateway? = null

    @InjectMocks
    private val sakerService: SakerServiceImpl? = null

    @BeforeEach
    fun setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC)
        MockitoAnnotations.initMocks(this)
        sakerService!!.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        `when`(arbeidOgAktivitet!!.hentSakListe(ArgumentMatchers.any(WSHentSakListeRequest::class.java))).thenReturn(WSHentSakListeResponse())
        `when`(unleashService!!.isEnabled(ArgumentMatchers.anyString())).thenReturn(true)
        `when`(unleashService.isEnabled(ArgumentMatchers.any(Feature::class.java))).thenReturn(true)
        MDC.put(MDCConstants.MDC_CALL_ID, "12345")
    }

    @Test
    fun `transformerer response til saksliste`() {
        `when`(sakApiGateway!!.hentSaker(ArgumentMatchers.anyString())).thenReturn(createSaksliste())
        val saksliste: List<Sak> = sakerService!!.hentSammensatteSakerResultat(FNR).saker
        assertThat(saksliste[0].saksId, `is`(SakId_1))
        assertThat(saksliste[3].fagsystemKode, `is`(""))
        assertThat(saksliste[saksliste.size - 1].sakstype, `is`(Sak.SAKSTYPE_MED_FAGSAK))
        assertThat(saksliste[saksliste.size - 1].temaKode, `is`(Sak.BIDRAG_MARKOR))
        assertThat(saksliste[saksliste.size - 1].temaNavn, `is`("Bidrag"))
        assertThat(saksliste[saksliste.size - 1].fagsystemNavn, `is`("Kopiert inn i Bisys"))
    }

    @Test
    fun `transformerer response til saksliste pensjon`() {
        val pensjon = Sak()
        pensjon.temaKode = "PENS"
        val ufore = Sak()
        ufore.temaKode = "UFO"
        val pensjonssaker = Arrays.asList(pensjon, ufore)
        `when`(psakService!!.hentSakerFor(FNR)).thenReturn(pensjonssaker)
        val saksliste = sakerService!!.hentPensjonSaker(FNR)
        assertThat(saksliste.size, `is`(2))
        assertThat(saksliste[0].temaNavn, `is`("PENS"))
        assertThat(saksliste[1].temaNavn, `is`("UFO"))
    }

    @Test
    fun `oppretter ikke generell oppfolgingssak og fjerner generell oppfolgingssak dersom fagsaker inneholder oppfolgingssak`() {
        `when`(sakApiGateway!!.hentSaker(ArgumentMatchers.anyString())).thenReturn(createOppfolgingSaksliste())
        val saker = sakerService!!.hentSammensatteSakerResultat(FNR).saker.stream().filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).collect(Collectors.toList())
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, CoreMatchers.not(`is`(Sak.SAKSTYPE_GENERELL)))
    }

    @Test
    fun `oppretter ìkke generell oppfolgingssak dersom denne finnes allerede selv om fagsaker ikke inneholder oppfolgingssak`() {
        val saker = sakerService!!.hentSammensatteSaker(FNR).stream().filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).collect(Collectors.toList())
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, `is`(Sak.SAKSTYPE_GENERELL))
    }

    @Test
    fun `legger til oppfolgingssak fra Arena dersom denne ikke finnes i gsak`() {
        val saksId = "123456"
        val dato = LocalDate.now().minusDays(1)
        `when`(arbeidOgAktivitet!!.hentSakListe(ArgumentMatchers.any(WSHentSakListeRequest::class.java))).thenReturn(WSHentSakListeResponse().withSakListe(
                no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                        .withFagomradeKode(Fagomradekode().withKode(Sak.TEMAKODE_OPPFOLGING))
                        .withSaksId(saksId)
                        .withEndringsInfo(EndringsInfo().withOpprettetDato(dato))
                        .withSakstypeKode(Sakstypekode().withKode("ARBEID"))
        ))
        val saker = sakerService!!.hentSammensatteSaker(FNR).stream().filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).collect(Collectors.toList())
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].saksIdVisning, `is`(saksId))
        assertThat(saker[0].opprettetDato, `is`(dato.toDateTimeAtStartOfDay()))
        assertThat(saker[0].fagsystemKode, `is`(Sak.FAGSYSTEMKODE_ARENA))
        assertThat(saker[0].finnesIGsak, `is`(false))
    }

    @Test
    fun `knytter behandlingskjede til sak uavhengig om den finnesIGsak`() {
        val sak = SakDataGenerator.lagSak()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SakDataGenerator.SAKS_ID
        `when`(behandleSak?.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(SakDataGenerator.FNR, SakDataGenerator.BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        verify(behandleHenvendelsePortType, times(1)).knyttBehandlingskjedeTilSak(SakDataGenerator.BEHANDLINGSKJEDEID, SakDataGenerator.SAKS_ID, sak.temaKode, valgtNavEnhet)
    }


    @Test
    fun `knytter behandlingsKjede til sak uavhengig om den finnesIGsak uten fagsystemId`() {
        val sak = SakDataGenerator.lagSakUtenFagsystemId()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SakDataGenerator.SAKS_ID
        `when`(behandleSak!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(opprettSakResponse)
        sakerService!!.knyttBehandlingskjedeTilSak(SakDataGenerator.FNR, SakDataGenerator.BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        com.nhaarman.mockitokotlin2.verify(behandleHenvendelsePortType, times(1))!!.knyttBehandlingskjedeTilSak(SakDataGenerator.BEHANDLINGSKJEDEID, SakDataGenerator.SAKS_ID, sak.temaKode, valgtNavEnhet)
    }

    @Test
    fun `knytt behandlingskjede til sak kaller alternativ metode om bidrags hack saken er valgt`() {
        val valgtNavEnhet = "0219"
        val sak = Sak()
        sak.syntetisk = true
        sak.fagsystemKode = Sak.BIDRAG_MARKOR
        sakerService!!.knyttBehandlingskjedeTilSak(SakDataGenerator.FNR, SakDataGenerator.BEHANDLINGSKJEDEID, sak, valgtNavEnhet)
        verify(behandleSak, Mockito.never())!!.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))
        verify(behandleHenvendelsePortType, Mockito.never())!!.knyttBehandlingskjedeTilSak(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())
        verify(behandleHenvendelsePortType, times(1))!!.knyttBehandlingskjedeTilTema(SakDataGenerator.BEHANDLINGSKJEDEID, "BID")
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis enhet ikke er satt`() {
        `when`(behandleSak?.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SakDataGenerator.SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(SakDataGenerator.FNR, SakDataGenerator.BEHANDLINGSKJEDEID, SakDataGenerator.lagSak(), "") }
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis behandlingskjede ikke er satt`() {
        `when`(behandleSak?.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SakDataGenerator.SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak(SakDataGenerator.FNR, null, SakDataGenerator.lagSak(), "1337") }
    }

    @Test
    fun `knytt Behandlingskjede til sak kaster feil hvis FNR ikke er satt`() {
        `when`(behandleSak?.opprettSak(ArgumentMatchers.any(WSOpprettSakRequest::class.java))).thenReturn(WSOpprettSakResponse().withSakId(SakDataGenerator.SAKS_ID))
        Assertions.assertThrows(IllegalArgumentException::class.java) { sakerService!!.knyttBehandlingskjedeTilSak("", SakDataGenerator.BEHANDLINGSKJEDEID, SakDataGenerator.lagSak(), "1337") }
    }


    companion object {
        private const val FNR = "fnr"
        const val SakId_1 = "1"
    }
}
