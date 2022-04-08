package no.nav.modiapersonoversikt.service.saker

import com.expediagroup.graphql.types.GraphQLResponse
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import no.nav.common.auth.context.AuthContext
import no.nav.common.auth.context.UserRole
import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.modiapersonoversikt.consumer.saf.generated.Hentbrukerssaker
import no.nav.modiapersonoversikt.legacy.sak.service.saf.SafService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saker.Sak.*
import no.nav.modiapersonoversikt.service.saker.SakerServiceImpl.Companion.leggTilFagsystemNavn
import no.nav.modiapersonoversikt.service.saker.SakerServiceImpl.Companion.leggTilTemaNavn
import no.nav.modiapersonoversikt.testutils.AuthContextExtension
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.EndringsInfo
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Fagomradekode
import no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sakstypekode
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeResponse
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.slf4j.MDC
import java.time.LocalDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.streams.toList

@ExperimentalContracts
class SakerServiceImplTest {
    @MockK
    private lateinit var kodeverk: EnhetligKodeverk.Service

    @MockK
    private lateinit var safService: SafService

    @MockK
    private lateinit var arbeidOgAktivitet: ArbeidOgAktivitet

    @InjectMockKs
    private lateinit var sakerService: SakerServiceImpl

    @BeforeEach
    fun setUp() {
        EnvironmentUtils.setProperty("BISYS_BASEURL", "https://bisys-url", EnvironmentUtils.Type.PUBLIC)
        MockKAnnotations.init(this, relaxUnitFun = true)
        sakerService.setup() // Kaller @PostConstruct manuelt siden vi kjører testen uten spring
        every { arbeidOgAktivitet.hentSakListe(WSHentSakListeRequest()) } returns WSHentSakListeResponse()
        every { kodeverk.hentKodeverk<String, String>(any()) } returns EnhetligKodeverk.Kodeverk("", emptyMap())

        MDC.put(MDCConstants.MDC_CALL_ID, "12345")
    }

    @Test
    fun `transformerer response til saksliste`() {
        every { safService.hentSaker(any()) } returns createSaksliste()
        every { arbeidOgAktivitet.hentSakListe(any()) } returns WSHentSakListeResponse()

        val saksliste: List<Sak> = sakerService.hentSaker(FNR).saker

        assertThat(saksliste[0].saksId, `is`(SakId_1))
        assertThat(saksliste[3].fagsystemKode, `is`(""))
        assertThat(saksliste[saksliste.size - 1].sakstype, `is`(SAKSTYPE_MED_FAGSAK))
        assertThat(saksliste[saksliste.size - 1].temaKode, `is`("BID"))
        assertThat(saksliste[saksliste.size - 1].fagsystemKode, `is`("BISYS"))
    }

    @Test
    @DisplayName("oppretter ikke generell oppfolgingssak og fjerner generell oppfolgingssak dersom fagsaker inneholder oppfolgingssak")
    fun `oppretter ikke generell oppfolgingssak`() {
        every { safService.hentSaker(any()) } returns createOppfolgingSaksliste()
        val saker = sakerService.hentSaker(FNR).saker.stream()
            .filter(harTemaKode(TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, not(`is`(SAKSTYPE_GENERELL)))
    }

    @Test
    @DisplayName("oppretter ikke generell oppfolgingssak dersom denne finnes allerede selv om fagsaker ikke inneholder oppfolgingssak")
    fun `oppretter ikke generell oppfolgingssak dersom denne finnes allerede`() {
        val saker =
            sakerService.hentSaker(FNR).saker.stream().filter(harTemaKode(TEMAKODE_OPPFOLGING)).toList()
        assertThat(saker.size, `is`(1))
        assertThat(saker[0].sakstype, `is`(SAKSTYPE_GENERELL))
    }

    @Test
    fun `legger til oppfolgingssak fra Arena dersom denne ikke finnes i sak`() {
        every { safService.hentSaker(any()) } returns GraphQLResponse()
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
    fun `oversetter fagsystemkode til fagsystemnavn`() {
        val kodeverk = EnhetligKodeverk.Kodeverk(
            "mock",
            mapOf(
                "PP01" to "Pesys"
            )
        )
        val resultat = SakerService.Resultat(
            mutableListOf(
                Sak().apply {
                    fagsystemKode = "PP01"
                }
            )
        )

        resultat.leggTilFagsystemNavn(kodeverk)

        assertThat(resultat.saker.first().fagsystemNavn, `is`("Pesys"))
    }

    @Test
    fun `setter fagsystemnavn til fagsystemkode om mapping ikke finnes`() {
        val kodeverk = EnhetligKodeverk.Kodeverk(
            "mock",
            mapOf(
                "PP01" to "Pesys"
            )
        )
        val resultat = SakerService.Resultat(
            mutableListOf(
                Sak().apply {
                    fagsystemKode = "AO01"
                }
            )
        )

        resultat.leggTilFagsystemNavn(kodeverk)

        assertThat(resultat.saker.first().fagsystemNavn, `is`("AO01"))
    }

    @Test
    fun `oversetter temakode til temanavn`() {
        val kodeverk = EnhetligKodeverk.Kodeverk(
            "mock",
            mapOf(
                "DAG" to "Dagpenger"
            )
        )
        val resultat = SakerService.Resultat(
            mutableListOf(
                Sak().apply {
                    temaKode = "DAG"
                }
            )
        )

        resultat.leggTilTemaNavn(kodeverk)

        assertThat(resultat.saker.first().temaNavn, `is`("Dagpenger"))
    }

    @Test
    fun `setter temanavn til temakode om mapping ikke finnes`() {
        val kodeverk = EnhetligKodeverk.Kodeverk(
            "mock",
            mapOf(
                "DAG" to "Dagpenger"
            )
        )
        val resultat = SakerService.Resultat(
            mutableListOf(
                Sak().apply {
                    temaKode = "AAP"
                }
            )
        )

        resultat.leggTilTemaNavn(kodeverk)

        assertThat(resultat.saker.first().temaNavn, `is`("AAP"))
    }

    companion object {
        @JvmField
        @RegisterExtension
        val subject = AuthContextExtension(
            AuthContext(
                UserRole.INTERN,
                PlainJWT(JWTClaimsSet.Builder().subject("Z999999").build())
            )
        )

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

        fun earlierDateTimeWithOffSet(offset: Long) = Hentbrukerssaker.DateTime(
            LocalDateTime.now().minusDays(offset)
        )

        fun createSaksliste(): GraphQLResponse<Hentbrukerssaker.Result> {
            return GraphQLResponse(
                data = Hentbrukerssaker.Result(
                    saker = listOf(
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = SakId_1,
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(4),
                            fagsakId = FagsystemSakId_1,
                            fagsaksystem = "IT01",
                            sakstype = Hentbrukerssaker.Sakstype.FAGSAK,
                            tema = null
                        ),
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = SakId_2,
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(3),
                            fagsaksystem = "IT01",
                            fagsakId = FagsystemSakId_2,
                            sakstype = Hentbrukerssaker.Sakstype.FAGSAK,
                            tema = null
                        ),
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = SakId_3,
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(5),
                            fagsaksystem = "IT01",
                            fagsakId = FagsystemSakId_3,
                            sakstype = Hentbrukerssaker.Sakstype.FAGSAK,
                            tema = null
                        ),
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = SakId_4,
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(5),
                            fagsaksystem = null,
                            fagsakId = null,
                            sakstype = Hentbrukerssaker.Sakstype.GENERELL_SAK,
                            tema = Hentbrukerssaker.Tema.STO
                        )
                    )
                )
            )
        }

        fun createOppfolgingSaksliste(): GraphQLResponse<Hentbrukerssaker.Result> {
            return GraphQLResponse(
                data = Hentbrukerssaker.Result(
                    saker = listOf(
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = "4",
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(0),
                            fagsaksystem = "AO01",
                            fagsakId = "44",
                            sakstype = Hentbrukerssaker.Sakstype.FAGSAK,
                            tema = Hentbrukerssaker.Tema.OPP
                        ),
                        Hentbrukerssaker.Sak(
                            arkivsaksnummer = "5",
                            arkivsaksystem = null,
                            datoOpprettet = earlierDateTimeWithOffSet(3),
                            fagsaksystem = "FS22",
                            fagsakId = null,
                            sakstype = Hentbrukerssaker.Sakstype.GENERELL_SAK,
                            tema = Hentbrukerssaker.Tema.OPP
                        )
                    )
                )
            )
        }
    }
}
