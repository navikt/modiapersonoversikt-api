package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint
import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.disableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.enableFeature
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentPersonResponse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.WSHentSikkerhetstiltakResponse
import org.junit.jupiter.api.*
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val FNR = "10108000398"
private const val TOLKEHJELP_KODE = "TOHJ"


class PersonControllerTest {

    private val personV3: PersonV3 = mock()
    private val pep: EnforcementPoint = mock()
    private val organisasjonenhetV2Service: OrganisasjonEnhetV2Service = mock()
    private val kodeverk: KodeverkmanagerBi = mock()
    private val mapper = KjerneinfoMapper(kodeverk)
    private val service = DefaultPersonKjerneinfoService(personV3, mapper, pep, organisasjonenhetV2Service)
    private val controller = PersonController(service, kodeverk)

    @BeforeEach
    fun before() {
        whenever(organisasjonenhetV2Service.finnNAVKontor(any(), any())).thenReturn(Optional.empty())
    }

    @Test
    fun `Kaster 404 hvis personen ikke ble funnet`() {
        whenever(personV3.hentPerson(any())).thenThrow(HentPersonPersonIkkeFunnet())
        assertFailsWith<NotFoundException> { controller.hent(FNR) }
    }

    @Test
    fun `Returnerer begrenset innsyn object ved ikke tilgang`() {
        whenever(personV3.hentPerson(any())).thenThrow(HentPersonSikkerhetsbegrensning())
        whenever(personV3.hentSikkerhetstiltak(any()))
                .thenReturn(WSHentSikkerhetstiltakResponse()
                        .withSikkerhetstiltak(WSSikkerhetstiltak()
                                .withSikkerhetstiltaksbeskrivelse("")))
        val o = controller.hent(FNR)
        assertTrue { o.containsKey("begrunnelse") }
    }

    @Test
    fun `Mapper om ukjente statsborgerskap til null`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withStatsborgerskap(WSStatsborgerskap().withLand(WSLandkoder().withValue("???")))
        }
        whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"]

        assertEquals(null, statsborgerskap)
    }

    private fun mockPersonResponse() = WSHentPersonResponse()
            .withPerson(WSBruker()
                    .withPersonnavn(WSPersonnavn())
                    .withKjoenn(WSKjoenn().withKjoenn(WSKjoennstyper().withValue("K")))
                    .withAktoer(WSPersonIdent().withIdent(WSNorskIdent().withIdent(FNR))))

    @Nested
    inner class Kontaktinformasjon {

        private val RETNINGSNUMMER = "46"
        private val TELEFONNUMMER = "10108000398"

        @Test
        fun `Med mobil`() {
            val mockPersonResponse = responseMedMobil()
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil = kontaktinformasjon["mobil"] as Map<*, *>
            val retningsnummer = mobil["retningsnummer"]
            val nummer = mobil["identifikator"]

            assertEquals(TELEFONNUMMER, nummer)
            assertEquals(RETNINGSNUMMER, retningsnummer)
        }

        private fun responseMedMobil() = mockPersonResponse().apply {
            (person as WSBruker)
                    .withKontaktinformasjon(WSTelefonnummer()
                            .withIdentifikator(TELEFONNUMMER)
                            .withRetningsnummer(WSRetningsnumre().withValue(RETNINGSNUMMER))
                            .withType(WSTelefontyper().withValue("MOBI")))
        }

        @Test
        fun `Uten mobil`() {
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse())

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil = kontaktinformasjon["mobil"]

            assertEquals(null, mobil)
        }

    }

    @Nested
    inner class `Tilrettelagt kommunikasjon` {

        @Test
        fun `Mapping`() {
            whenever(kodeverk.getKodeverkList(any(), any())).thenReturn(listOf(Kodeverdi(TOLKEHJELP_KODE, TOLKEHJELP_KODE)))
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse().apply {
                (person as WSBruker)
                        .withTilrettelagtKommunikasjon(
                                WSTilrettelagtKommunikasjonbehov()
                                        .withBehov(TOLKEHJELP_KODE)
                                        .withTilrettelagtKommunikasjon(WSTilrettelagtKommunikasjon()
                                                .withKodeverksRef("TilrettelagtKommunikasjon")
                                                .withValue(TOLKEHJELP_KODE)
                                                .withKodeRef(TOLKEHJELP_KODE))
                        )
            })

            val response = controller.hent(FNR)["tilrettelagtKomunikasjonsListe"] as List<*>
            val tilrettelagtKommunikasjon = response[0] as Map<*, *>

            assertEquals(tilrettelagtKommunikasjon["behovKode"], TOLKEHJELP_KODE)
        }

    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun beforeAll() = enableFeature(PERSON_REST_API)

        @AfterAll
        @JvmStatic
        fun afterAll() = disableFeature(PERSON_REST_API)

    }

}
