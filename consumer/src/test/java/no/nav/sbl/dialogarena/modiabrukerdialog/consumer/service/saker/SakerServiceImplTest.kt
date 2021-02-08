package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakResponse
import no.nav.tjeneste.virksomhet.sak.v1.SakV1
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak
import no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSakstyper
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import kotlin.contracts.ExperimentalContracts
import kotlin.streams.toList


@ExperimentalContracts
class SakerServiceImplTest {
    @MockK
    private lateinit var sakV1: SakV1

    @MockK
    private lateinit var behandleSak: BehandleSakV1

    @MockK
    private lateinit var gsakKodeverk: GsakKodeverk

    @MockK
    private lateinit var standardKodeverk: StandardKodeverk

    @MockK
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @MockK
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @MockK
    private lateinit var psakService: PsakService

    @MockK
    private lateinit var unleashService: UnleashService

    @MockK
    private lateinit var sakApiGateway: SakApiGateway

    @MockK
    private lateinit var fodselnummerAktorService: FodselnummerAktorService

    @InjectMockKs
    private lateinit var sakerService: SakerServiceImpl

    @BeforeEach
    fun setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC)
        MockKAnnotations.init(this, relaxUnitFun = true)
        sakerService.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        every { arbeidOgAktivitet.hentSakListe(WSHentSakListeRequest()) } returns WSHentSakListeResponse()
        every { unleashService.isEnabled(String()) } returns false
        every { unleashService.isEnabled(Feature.USE_REST_SAK_IMPL) } returns false
        every { gsakKodeverk.hentFagsystemMapping() } returns emptyMap()
        every { standardKodeverk.getArkivtemaNavn(any()) } returns null
        every { sakV1.finnSak(any()) } returns WSFinnSakResponse().withSakListe(createSaksliste())
        MDC.put(MDCConstants.MDC_CALL_ID, "12345")

    }

    @Test
    fun `transformerer response til saksliste`() {
        val saksliste: List<Sak> = sakerService.hentSammensatteSakerResultat(FNR).saker
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
        val pensjonssaker = listOf(pensjon, ufore)
        every { psakService.hentSakerFor(FNR) } returns pensjonssaker

        val saksliste = sakerService.hentPensjonSaker(FNR)
        assertThat(saksliste.size, `is`(2))
        assertThat(saksliste[0].temaNavn, `is`("PENS"))
        assertThat(saksliste[1].temaNavn, `is`("UFO"))
    }

    @Test
    fun `oppretter ikke generell oppfolgingssak og fjerner generell oppfolgingssak dersom fagsaker inneholder oppfolgingssak`() {
        val wsSaker: List<WSSak> = listOf(
            WSSak()
                .withSakId("4")
                .withFagsystemSakId("44")
                .withFagomraade(WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                .withOpprettelsetidspunkt(DateTime.now())
                .withSakstype(WSSakstyper().withValue("Fag"))
                .withFagsystem(WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER[0])),
            WSSak()
                .withSakId("5")
                .withFagsystemSakId("45")
                .withFagomraade(WSFagomraader().withValue(TEMAKODE_OPPFOLGING))
                .withOpprettelsetidspunkt(DateTime.now())
                .withSakstype(WSSakstyper().withValue(SAKSTYPE_GENERELL))
                .withFagsystem(WSFagsystemer().withValue(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK))
        )

        every { sakV1.finnSak(any()) } returns WSFinnSakResponse().withSakListe(wsSaker)
        val saker = sakerService.hentSammensatteSakerResultat(FNR).saker.stream()
            .filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, not(`is`(Sak.SAKSTYPE_GENERELL)))
    }

    @Test
    fun `oppretter ìkke generell oppfolgingssak dersom denne finnes allerede selv om fagsaker ikke inneholder oppfolgingssak`() {
        val saker =
            sakerService.hentSammensatteSaker(FNR).stream().filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, `is`(Sak.SAKSTYPE_GENERELL))
    }


    @Test
    fun `legger til oppfolgingssak fra Arena dersom denne ikke finnes i gsak`() {
        val saksId = "123456"
        val dato = LocalDate.now().minusDays(1)
        every { arbeidOgAktivitet.hentSakListe(any()) } returns WSHentSakListeResponse().withSakListe(
            no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                .withFagomradeKode(Fagomradekode().withKode(Sak.TEMAKODE_OPPFOLGING))
                .withSaksId(saksId)
                .withEndringsInfo(EndringsInfo().withOpprettetDato(dato))
                .withSakstypeKode(Sakstypekode().withKode("ARBEID"))
        )
        val saker =
            sakerService.hentSammensatteSaker(FNR).stream().filter(Sak.harTemaKode(Sak.TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].saksIdVisning, `is`(saksId))
        assertThat(saker[0].opprettetDato, `is`(dato.toDateTimeAtStartOfDay()))
        assertThat(saker[0].fagsystemKode, `is`(Sak.FAGSYSTEMKODE_ARENA))
        assertThat(saker[0].finnesIGsak, `is`(false))
    }

    @Test
    fun `knytter behandlingskjede til sak uavhengig om den finnesIGsak`() {
        val sak = lagSak()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        every { behandleSak.opprettSak(any()) } returns opprettSakResponse
        sakerService.knyttBehandlingskjedeTilSak(
            FNR,
            BEHANDLINGSKJEDEID,
            sak,
            valgtNavEnhet
        )
        verify(exactly = 1) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                BEHANDLINGSKJEDEID,
                SAKS_ID,
                sak.temaKode,
                valgtNavEnhet
            )
        }
    }


    @Test
    fun `knytter behandlingsKjede til sak uavhengig om den finnesIGsak uten fagsystemId`() {
        val sak = lagSakUtenFagsystemId()
        val valgtNavEnhet = "0219"
        val opprettSakResponse = WSOpprettSakResponse()
        opprettSakResponse.sakId = SAKS_ID
        every { behandleSak.opprettSak(any()) } returns opprettSakResponse
        sakerService.knyttBehandlingskjedeTilSak(
            FNR,
            BEHANDLINGSKJEDEID,
            sak,
            valgtNavEnhet
        )
        verify(exactly = 1) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                BEHANDLINGSKJEDEID,
                SAKS_ID,
                sak.temaKode,
                valgtNavEnhet
            )
        }
    }

    @Test
    fun `knytt behandlingskjede til sak kaller alternativ metode om bidrags hack saken er valgt`() {
        val valgtNavEnhet = "0219"
        val sak = Sak()
        sak.syntetisk = true
        sak.fagsystemKode = Sak.BIDRAG_MARKOR
        sakerService.knyttBehandlingskjedeTilSak(
            FNR,
            BEHANDLINGSKJEDEID,
            sak,
            valgtNavEnhet
        )
        verify(exactly = 0) { behandleSak.opprettSak(WSOpprettSakRequest()) }
        verify(exactly = 0) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                String(),
                String(),
                String(),
                String()
            )
        }
        verify(exactly = 1) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(
                BEHANDLINGSKJEDEID,
                "BID"
            )
        }
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis enhet ikke er satt`() {
        every { behandleSak.opprettSak(WSOpprettSakRequest()) } returns WSOpprettSakResponse().withSakId(
            SAKS_ID
        )
        assertThrows(IllegalArgumentException::class.java) {
            sakerService.knyttBehandlingskjedeTilSak(
                FNR,
                BEHANDLINGSKJEDEID,
                lagSak(),
                ""
            )
        }
    }

    @Test
    fun `knytt behandlingskjede til sak kaster feil hvis behandlingskjede ikke er satt`() {
        every { behandleSak.opprettSak(WSOpprettSakRequest()) } returns WSOpprettSakResponse().withSakId(
            SAKS_ID
        )
        assertThrows(IllegalArgumentException::class.java) {
            sakerService.knyttBehandlingskjedeTilSak(
                FNR,
                null,
                lagSak(),
                "1337"
            )
        }
    }

    @Test
    fun `knytt Behandlingskjede til sak kaster feil hvis FNR ikke er satt`() {
        every { behandleSak.opprettSak(WSOpprettSakRequest()) } returns WSOpprettSakResponse().withSakId(
            SAKS_ID
        )
        assertThrows(IllegalArgumentException::class.java) {
            sakerService.knyttBehandlingskjedeTilSak(
                "",
                BEHANDLINGSKJEDEID,
                lagSak(),
                "1337"
            )
        }
    }


    companion object {
        const val VEDTAKSLOSNINGEN = "FS36"
        val FIRE_DAGER_SIDEN = DateTime.now().minusDays(4) //joda.DateTime
        const val FNR = "fnr"
        const val BEHANDLINGSKJEDEID = "behandlingsKjedeId"
        const val SAKS_ID = "123"
        const val SakId_1 = "1"
        const val FagsystemSakId_1 = "11"
        const val SakId_2 = "2"
        const val FagsystemSakId_2 = "22"
        const val SakId_3 = "3"
        const val FagsystemSakId_3 = "33"
        const val SakId_4 = "4"
        const val FagsystemSakId_4 = "44"

        private fun createSaksliste(): List<WSSak> {
            return listOf(
                WSSak()
                    .withSakId(SakId_1)
                    .withFagsystemSakId(FagsystemSakId_1)
                    .withFagomraade(WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
                    .withOpprettelsetidspunkt(FIRE_DAGER_SIDEN)
                    .withSakstype(WSSakstyper().withValue(SAKSTYPE_GENERELL))
                    .withFagsystem(WSFagsystemer().withValue(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK)),
                WSSak()
                    .withSakId(SakId_2)
                    .withFagsystemSakId(FagsystemSakId_2)
                    .withFagomraade(WSFagomraader().withValue(GODKJENTE_TEMA_FOR_GENERELL_SAK[1]))
                    .withOpprettelsetidspunkt(DateTime.now().minusDays(3))
                    .withSakstype(WSSakstyper().withValue(SAKSTYPE_GENERELL))
                    .withFagsystem(WSFagsystemer().withValue(FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK)),
                WSSak()
                    .withSakId(SakId_3)
                    .withFagsystemSakId(FagsystemSakId_3)
                    .withFagomraade(WSFagomraader().withValue("AAP"))
                    .withOpprettelsetidspunkt(DateTime.now().minusDays(5))
                    .withSakstype(WSSakstyper().withValue("Fag"))
                    .withFagsystem(WSFagsystemer().withValue(GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER[0])),
                WSSak()
                    .withSakId(SakId_4)
                    .withFagsystemSakId(FagsystemSakId_4)
                    .withFagomraade(WSFagomraader().withValue("STO"))
                    .withOpprettelsetidspunkt(DateTime.now().minusDays(5))
                    .withSakstype(WSSakstyper().withValue(SAKSTYPE_GENERELL))
                    .withFagsystem(WSFagsystemer().withValue(""))
            )
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

        fun lagSakUtenFagsystemId(): Sak {
            val sak = Sak()
            sak.temaKode = "STO"
            sak.finnesIGsak = false
            sak.fagsystemKode = ""
            sak.sakstype = Sak.SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }
    }
}
