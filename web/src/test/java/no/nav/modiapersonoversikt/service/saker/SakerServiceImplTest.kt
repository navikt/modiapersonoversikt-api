package no.nav.modiapersonoversikt.service.saker

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.apis.BidragSakControllerApi
import no.nav.modiapersonoversikt.legacy.api.domain.bidragsak.generated.models.BidragSakDto
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak
import no.nav.modiapersonoversikt.legacy.api.domain.saker.Sak.*
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.api.service.psak.PsakService
import no.nav.modiapersonoversikt.legacy.api.service.saker.GsakKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saker.mediation.SakApiGateway
import no.nav.modiapersonoversikt.service.saker.mediation.SakDto
import no.nav.modiapersonoversikt.service.unleash.Feature
import no.nav.modiapersonoversikt.service.unleash.UnleashService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.time.OffsetDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.contracts.ExperimentalContracts
import kotlin.streams.toList

@ExperimentalContracts
class SakerServiceImplTest {

    @MockK
    private lateinit var gsakKodeverk: GsakKodeverk

    @MockK
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @MockK
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @MockK
    private lateinit var behandleHenvendelsePortType: BehandleHenvendelsePortType

    @MockK
    private lateinit var psakService: PsakService

    @MockK
    private lateinit var sakApiGateway: SakApiGateway

    @MockK
    private lateinit var pdlOppslagService: PdlOppslagService

    @MockK
    private lateinit var bidragSakControllerApi: BidragSakControllerApi

    @MockK
    private lateinit var unleashService: UnleashService

    @InjectMockKs
    private lateinit var sakerService: SakerServiceImpl

    @BeforeEach
    fun setUp() {
        EnvironmentUtils.setProperty("SAK_ENDPOINTURL", "https://sak-url", EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty("BISYS_BASEURL", "https://bisys-url", EnvironmentUtils.Type.PUBLIC)
        MockKAnnotations.init(this, relaxUnitFun = true)
        sakerService.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        every { arbeidOgAktivitet.hentSakListe(WSHentSakListeRequest()) } returns WSHentSakListeResponse()
        every { gsakKodeverk.hentFagsystemMapping() } returns emptyMap()
        every { kodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk("", emptyMap())
        every { pdlOppslagService.hentAktorId(any()) } returns "123456789"
        every { bidragSakControllerApi.find(any()) } returns listOf(BidragSakDto(roller = listOf(), saksnummer = "123", erParagraf19 = false))
        every { unleashService.isEnabled(any<Feature>()) } returns false

        mockkStatic(SubjectHandler::class)
        every { SubjectHandler.getSubject() } returns Optional.of(Subject("12345678910", IdentType.EksternBruker, SsoToken.oidcToken("token", HashMap<String, Any?>())))
        every { sakApiGateway.opprettSak(any()) } returns SakDto(id = "123")

        MDC.put(MDCConstants.MDC_CALL_ID, "12345")
    }

    @AfterEach
    fun destroy() {
        unmockkStatic(SubjectHandler::class)
    }

    @Test
    fun `transformerer response til saksliste`() {
        every { sakApiGateway.hentSaker(any()) } returns createSaksliste()
        every { arbeidOgAktivitet.hentSakListe(any()) } returns WSHentSakListeResponse()
        val saksliste: List<Sak> = sakerService.hentSammensatteSakerResultat(FNR).saker
        assertThat(saksliste[0].saksId, `is`(SakId_1))
        assertThat(saksliste[0].saksId, `is`(SakId_1))
        assertThat(saksliste[3].fagsystemKode, `is`(""))
        assertThat(saksliste[saksliste.size - 1].sakstype, `is`(SAKSTYPE_MED_FAGSAK))
        assertThat(saksliste[saksliste.size - 1].temaKode, `is`(BIDRAG_MARKOR))
        assertThat(saksliste[saksliste.size - 1].temaNavn, `is`("Bidrag"))
        assertThat(saksliste[saksliste.size - 1].fagsystemNavn, `is`("Kopiert inn i Bisys"))
    }

    @Test
    fun `transformerer response til saksliste pensjon`() {
        every { sakApiGateway.hentSaker(any()) } returns listOf()
        every { arbeidOgAktivitet.hentSakListe(any()) } returns WSHentSakListeResponse()
        every { bidragSakControllerApi.find(any()) } returns listOf()

        val pensjon = Sak()
        pensjon.temaKode = "PENS"
        val ufore = Sak()
        ufore.temaKode = "UFO"
        val pensjonssaker = listOf(pensjon, ufore)
        every { psakService.hentSakerFor(FNR) } returns pensjonssaker

        val saksliste = sakerService.hentSaker(FNR).saker

        assertThat(saksliste.size, `is`(3))
        assertThat(saksliste[0].temaNavn, `is`("PENS"))
        assertThat(saksliste[1].temaNavn, `is`("UFO"))
    }

    @Test
    @DisplayName("oppretter ikke generell oppfolgingssak og fjerner generell oppfolgingssak dersom fagsaker inneholder oppfolgingssak")
    fun `oppretter ikke generell oppfolgingssak`() {
        every { sakApiGateway.hentSaker(any()) } returns createOppfolgingSaksliste()
        val saker = sakerService.hentSammensatteSakerResultat(FNR).saker.stream()
            .filter(harTemaKode(TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, not(`is`(SAKSTYPE_GENERELL)))
    }

    @Test
    @DisplayName("oppretter ìkke generell oppfolgingssak dersom denne finnes allerede selv om fagsaker ikke inneholder oppfolgingssak")
    fun `oppretter ìkke generell oppfolgingssak dersom denne finnes allerede`() {
        val saker =
            sakerService.hentSaker(FNR).saker.stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, `is`(SAKSTYPE_GENERELL))
    }

    @Test
    fun `legger til oppfolgingssak fra Arena dersom denne ikke finnes i sak`() {
        every { psakService.hentSakerFor(any()) } returns emptyList()
        val saksId = "123456"
        val dato = LocalDate.now().minusDays(1)
        every { arbeidOgAktivitet.hentSakListe(any()) } returns WSHentSakListeResponse().withSakListe(
            no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak()
                .withFagomradeKode(Fagomradekode().withKode(TEMAKODE_OPPFOLGING))
                .withSaksId(saksId)
                .withEndringsInfo(EndringsInfo().withOpprettetDato(dato))
                .withSakstypeKode(Sakstypekode().withKode("ARBEID"))
        )
        val saker =
            sakerService.hentSaker(FNR).saker.stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].saksIdVisning, `is`(saksId))
        assertThat(saker[0].opprettetDato, `is`(dato.toDateTimeAtStartOfDay()))
        assertThat(saker[0].fagsystemKode, `is`(FAGSYSTEMKODE_ARENA))
        assertThat(saker[0].finnesIGsak, `is`(false))
    }

    @Test
    fun `knytter behandlingskjede til sak uavhengig om den finnesIGsak`() {
        val sak = lagSak()
        val valgtNavEnhet = "0219"
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
        sak.temaKode = BIDRAG_MARKOR
        sakerService.knyttBehandlingskjedeTilSak(
            FNR,
            BEHANDLINGSKJEDEID,
            sak,
            valgtNavEnhet
        )
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
        const val BEHANDLINGSKJEDEID = "behandlingsKjedeId"
        const val SAKS_ID = "123"
        const val FNR = "fnr"
        const val SakId_1 = "1"
        const val FagsystemSakId_1 = "11"
        const val SakId_2 = "2"
        const val FagsystemSakId_2 = "22"
        const val SakId_3 = "3"
        const val FagsystemSakId_3 = "33"
        const val SakId_4 = "4"

        fun lagSak(): Sak {
            val sak = Sak()
            sak.temaKode = "GEN"
            sak.finnesIGsak = false
            sak.fagsystemKode = FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK
            sak.sakstype = SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }

        fun lagSakUtenFagsystemId(): Sak {
            val sak = Sak()
            sak.temaKode = "STO"
            sak.finnesIGsak = false
            sak.fagsystemKode = ""
            sak.sakstype = SAKSTYPE_GENERELL
            sak.opprettetDato = DateTime.now()
            return sak
        }

        fun earlierDateTimeWithOffSet(offset: Long): OffsetDateTime = OffsetDateTime.now().minusDays(offset)

        fun createSaksliste(): List<SakDto> {
            return ArrayList(
                listOf(
                    SakDto(
                        id = SakId_1,
                        tema = "AAP",
                        applikasjon = "IT01",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = FagsystemSakId_1,
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(4)
                    ),

                    SakDto(
                        id = SakId_2,
                        tema = "AGR",
                        applikasjon = "IT01",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = FagsystemSakId_2,
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(3)
                    ),

                    SakDto(
                        id = SakId_3,
                        tema = "AAP",
                        applikasjon = "IT01",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = FagsystemSakId_3,
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(5)
                    ),

                    SakDto(
                        id = SakId_4,
                        tema = "STO",
                        applikasjon = "",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = null,
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(5)
                    )
                )
            )
        }

        fun createOppfolgingSaksliste(): MutableList<SakDto> {
            return ArrayList(
                listOf(
                    SakDto(
                        id = "4",
                        tema = "OPP",
                        applikasjon = "AO01",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = "44",
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(0)
                    ),

                    SakDto(
                        id = "5",
                        tema = "OPP",
                        applikasjon = "FS22",
                        aktoerId = "123",
                        orgnr = null,
                        fagsakNr = null,
                        opprettetAv = null,
                        opprettetTidspunkt = earlierDateTimeWithOffSet(3)
                    )
                )
            )
        }
    }
}
