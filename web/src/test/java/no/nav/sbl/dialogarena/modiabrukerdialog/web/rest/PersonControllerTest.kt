package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.PersonOppslagResponse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person.PersonOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollContextUtenTPS
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollUtenTPS
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk.Kode
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.WSLandkoder
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.feil.Sikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import javax.ws.rs.NotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val FNR = "10108000398"
private const val TOLKEHJELP_KODE = "TOHJ"
private const val TOLKEHJELP_BESKRIVELSE = "Tolkehjelp"
private const val KONTONUMMER = "11111111111"
private const val BANKNAVN = "Pengebingen ASA"
private const val SWIFT = "Taylor"
private const val LANDKODE = "IOT"

internal class PersonControllerTest {

    private val personV3: PersonV3 = mock()
    private val organisasjonenhetV2Service: OrganisasjonEnhetV2Service = mock()
    private val kodeverk: KodeverkmanagerBi = mock()
    private val mapper = KjerneinfoMapper(kodeverk)
    private val oppslag: PersonOppslagService = mock()
    private val unleashService: UnleashService = mock()
    private val tilgangskontrollUtenTPSContext: TilgangskontrollContextUtenTPS = mock()
    private val tilgangskontrollUtenTPS = TilgangskontrollUtenTPS(tilgangskontrollUtenTPSContext)
    private val tilgangskontroll: Tilgangskontroll = TilgangskontrollMock.get()

    private val service = DefaultPersonKjerneinfoService(personV3, mapper, tilgangskontrollUtenTPS, organisasjonenhetV2Service)
    private val controller = PersonController(service, kodeverk, oppslag, unleashService, tilgangskontroll)

    @BeforeEach
    fun before() {
        whenever(organisasjonenhetV2Service.finnNAVKontor(any(), any())).thenReturn(Optional.empty())
        whenever(oppslag.hentPersonDokument(any())).thenReturn(PersonOppslagResponse())
    }

    @Test
    fun `Kaster 404 hvis personen ikke ble funnet`() {
        whenever(personV3.hentPerson(any())).thenThrow(HentPersonPersonIkkeFunnet("", PersonIkkeFunnet()))
        assertFailsWith<NotFoundException> { controller.hent(FNR) }
    }

    @Test
    fun `Returnerer begrenset innsyn object ved ikke tilgang`() {
        whenever(personV3.hentPerson(any())).thenThrow(HentPersonSikkerhetsbegrensning("", Sikkerhetsbegrensning()))
        whenever(personV3.hentSikkerhetstiltak(any()))
                .thenReturn(HentSikkerhetstiltakResponse()
                        .withSikkerhetstiltak(Sikkerhetstiltak()
                                .withSikkerhetstiltaksbeskrivelse("")))
        val o = controller.hent(FNR)
        assertTrue { o.containsKey("begrunnelse") }
    }

    @Test
    fun `Mapper om ukjente statsborgerskap til null`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withStatsborgerskap(Statsborgerskap().withLand(Landkoder().withValue("???")))
        }
        whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"]

        assertEquals(null, statsborgerskap)
    }

    @Test
    fun `Med statsborgerskap`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withStatsborgerskap(Statsborgerskap().withLand(Landkoder()
                    .withValue("NOR")
                    .withKodeverksRef("Land")))
        }
        whenever(kodeverk.getBeskrivelseForKode("NOR", "Land", "nb")).thenReturn("NORGE")
        whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"] as Kode

        assertEquals("NORGE", statsborgerskap.beskrivelse)
        assertEquals("NOR", statsborgerskap.kodeRef)
    }

    @Test
    fun `Familierelasjoner`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withHarFraRolleI(Familierelasjon()
                    .withTilPerson(Person()
                            .withPersonnavn(Personnavn().withFornavn("Aremark"))
                            .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent("10108000398")))
                    ))
        }

        whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

        val response = controller.hent(FNR)
        val relasjoner = response["familierelasjoner"] as ArrayList<*>
        val rolle = relasjoner[0] as Map<*, *>
        val relasjon = rolle["tilPerson"] as Map<*, *>
        val fodselsnummer = relasjon["fødselsnummer"]
        val personnavn = relasjon["navn"] as Map<*, *>
        val fornavn = personnavn["fornavn"]

        assertEquals("10108000398", fodselsnummer)
        assertEquals("Aremark", fornavn)
    }

    private fun mockPersonResponse() = HentPersonResponse()
            .withPerson(Bruker()
                    .withPersonnavn(Personnavn())
                    .withKjoenn(Kjoenn().withKjoenn(Kjoennstyper().withValue("K")))
                    .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent(FNR))))

    @Nested
    inner class Diskresjonskode {

        @Test
        fun `Med diskresjonskode`() {
            val mockPersonResponse = mockPersonResponse().apply {
                person.withDiskresjonskode(Diskresjonskoder().withValue("SPFO"))
            }
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

            val response = controller.hent(FNR)
            val diskresjonskode = response["diskresjonskode"] as Kode

            assertEquals("SPFO", diskresjonskode.kodeRef)
        }

        @Test
        fun `Uten diskresjonskode`() {
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse())

            val response = controller.hent(FNR)
            val diskresjonskode = response["diskresjonskode"]

            assertEquals(null, diskresjonskode)
        }

        @Test
        fun `Familiemedlem med diskresjonskode`() {
            val mockPersonResponse = mockPersonResponse().apply {
                person.withHarFraRolleI(Familierelasjon()
                        .withTilPerson(Person()
                                .withDiskresjonskode(Diskresjonskoder().withValue("SPFO"))
                                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent("10108000398")))
                        ))
            }

            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse)

            val response = controller.hent(FNR)
            val relasjoner = response["familierelasjoner"] as ArrayList<*>
            val rolle = relasjoner[0] as Map<*, *>
            val relasjon = rolle["tilPerson"] as Map<*, *>
            val harSammeBosted = rolle["harSammeBosted"]
            val fodselsnummer = relasjon["fødselsnummer"]
            val navn = relasjon["navn"]
            val alder = relasjon["alder"]
            val alderIMåneder = relasjon["alderMåneder"]

            assertEquals(null, fodselsnummer)
            assertEquals(null, harSammeBosted)
            assertEquals(null, navn)
            assertEquals(null, alder)
            assertEquals(null, alderIMåneder)
        }

    }

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
            val retningsnummer = mobil["retningsnummer"] as Kode
            val nummer = mobil["identifikator"]

            assertEquals(TELEFONNUMMER, nummer)
            assertEquals(RETNINGSNUMMER, retningsnummer.kodeRef)
        }

        private fun responseMedMobil() = mockPersonResponse().apply {
            (person as Bruker)
                    .withKontaktinformasjon(Telefonnummer()
                            .withIdentifikator(TELEFONNUMMER)
                            .withRetningsnummer(Retningsnumre().withValue(RETNINGSNUMMER))
                            .withType(Telefontyper().withValue("MOBI")))
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
            whenever(kodeverk.getKodeverkList(any(), any())).thenReturn(listOf(Kodeverdi(TOLKEHJELP_KODE, TOLKEHJELP_BESKRIVELSE)))
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse().apply {
                (person as Bruker)
                        .withTilrettelagtKommunikasjon(
                                TilrettelagtKommunikasjonbehov()
                                        .withBehov(TOLKEHJELP_BESKRIVELSE)
                                        .withTilrettelagtKommunikasjon(TilrettelagtKommunikasjon()
                                                .withValue(TOLKEHJELP_KODE))
                        )
            })

            val response = controller.hent(FNR)["tilrettelagtKomunikasjonsListe"] as List<*>
            val tilrettelagtKommunikasjon = response[0] as Kode

            assertEquals(TOLKEHJELP_KODE, tilrettelagtKommunikasjon.kodeRef)
        }

    }

    @Nested
    inner class Bankkonto {

        @Test
        fun `Norsk konto`() {
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse().apply {
                (person as Bruker)
                        .withBankkonto(BankkontoNorge()
                                .withBankkonto(Bankkontonummer()
                                        .withBankkontonummer(KONTONUMMER)
                                        .withBanknavn(BANKNAVN)))
            })

            val response = controller.hent(FNR)["bankkonto"] as Map<*, *>

            assertEquals(KONTONUMMER, response["kontonummer"])
            assertEquals(BANKNAVN, response["banknavn"])
        }

        @Test
        fun `Utenlandsk konto`() {
            whenever(personV3.hentPerson(any())).thenReturn(mockPersonResponse().apply {
                (person as Bruker)
                        .withBankkonto(BankkontoUtland()
                                .withBankkontoUtland(BankkontonummerUtland()
                                        .withBankkontonummer(KONTONUMMER)
                                        .withBanknavn(BANKNAVN)
                                        .withSwift(SWIFT)
                                        .withLandkode(Landkoder().withValue(LANDKODE))))
            })

            val response = controller.hent(FNR)["bankkonto"] as Map<*, *>

            assertEquals(KONTONUMMER, response["kontonummer"])
            assertEquals(BANKNAVN, response["banknavn"])
            assertEquals(SWIFT, response["swift"])
            assertEquals(LANDKODE, (response["landkode"] as Kode).kodeRef)
        }

    }
}
