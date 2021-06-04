package no.nav.modiapersonoversikt.rest

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.api.service.kodeverk.StandardKodeverk
import no.nav.modiapersonoversikt.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service
import no.nav.modiapersonoversikt.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollContext
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.integration.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper
import no.nav.modiapersonoversikt.rest.kodeverk.Kode
import no.nav.modiapersonoversikt.rest.person.PersonController
import no.nav.modiapersonoversikt.rest.person.TilrettelagtKommunikasjonsbehov
import no.nav.modiapersonoversikt.rest.person.TilrettelagtKommunikasjonsbehovType
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.feil.PersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.feil.Sikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentSikkerhetstiltakResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.util.*

private const val FNR = "10108000398"
private const val KONTONUMMER = "11111111111"
private const val BANKNAVN = "Pengebingen ASA"
private const val SWIFT = "Taylor"
private const val LANDKODE = "IOT"

internal class PersonControllerTest {
    private val personV3: PersonV3 = mockk()
    private val pdlOppslagService: PdlOppslagService = mockk()
    private val organisasjonenhetV2Service: OrganisasjonEnhetV2Service = mockk()
    private val kodeverk: KodeverkmanagerBi = mockk()
    private val mapper = KjerneinfoMapper(kodeverk)
    private val tilgangskontrollUtenTPSContext: TilgangskontrollContext = mockk()
    private val tilgangskontrollUtenTPS = Tilgangskontroll(tilgangskontrollUtenTPSContext)
    private val tilgangskontroll: Tilgangskontroll = TilgangskontrollMock.get()
    private val standardKodeverk: StandardKodeverk = mockk()

    private val service =
        DefaultPersonKjerneinfoService(personV3, mapper, tilgangskontrollUtenTPS, organisasjonenhetV2Service)
    private val controller = PersonController(service, kodeverk, tilgangskontroll, pdlOppslagService, standardKodeverk)

    @BeforeEach
    fun before() {
        every { organisasjonenhetV2Service.finnNAVKontor(any(), any()) } returns Optional.empty()
        every { kodeverk.getBeskrivelseForKode("K", "Kj_c3_b8nnstyper", "nb") } returns "KVINNE"
        every { kodeverk.getBeskrivelseForKode("???", "Landkoder", "nb") } returns "Ukjent"
        every { kodeverk.getBeskrivelseForKode("IOT", "Landkoder", "nb") } returns "Det britiske territoriet i Indiahavet"
        every { kodeverk.getBeskrivelseForKode("SPFO", "Diskresjonskoder", "nb") } returns "Kode7"
        every { pdlOppslagService.hentPerson(any()) } returns null
        every { pdlOppslagService.hentNavnBolk(any()) } returns null
    }

    @Test
    fun `Kaster 404 hvis personen ikke ble funnet`() {
        every { personV3.hentPerson(any()) } throws HentPersonPersonIkkeFunnet("", PersonIkkeFunnet())
        val exception = assertThrows<ResponseStatusException> { controller.hent(FNR) }
        assertEquals(HttpStatus.NOT_FOUND, exception.status)
    }

    @Test
    fun `Returnerer begrenset innsyn object ved ikke tilgang`() {
        every { personV3.hentPerson(any()) } throws HentPersonSikkerhetsbegrensning("", Sikkerhetsbegrensning())
        every { personV3.hentSikkerhetstiltak(any()) } returns HentSikkerhetstiltakResponse()
            .withSikkerhetstiltak(Sikkerhetstiltak().withSikkerhetstiltaksbeskrivelse(""))

        val o = controller.hent(FNR)
        assertTrue { o.containsKey("begrunnelse") }
    }

    @Test
    fun `Mapper om ukjente statsborgerskap til null`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withStatsborgerskap(Statsborgerskap().withLand(Landkoder().withValue("???")))
        }
        every { personV3.hentPerson(any()) } returns mockPersonResponse

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"]

        assertEquals(null, statsborgerskap)
    }

    @Test
    fun `Med statsborgerskap`() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withStatsborgerskap(
                Statsborgerskap().withLand(
                    Landkoder()
                        .withValue("NOR")
                        .withKodeverksRef("Land")
                )
            )
        }
        every { kodeverk.getBeskrivelseForKode("NOR", "Land", "nb") } returns "NORGE"
        every { personV3.hentPerson(any()) } returns mockPersonResponse

        val response = controller.hent(FNR)
        val statsborgerskap = response["statsborgerskap"] as Kode

        assertEquals("NORGE", statsborgerskap.beskrivelse)
        assertEquals("NOR", statsborgerskap.kodeRef)
    }

    @Test
    fun Familierelasjoner() {
        val mockPersonResponse = mockPersonResponse().apply {
            person.withHarFraRolleI(
                Familierelasjon()
                    .withTilPerson(
                        Person()
                            .withPersonnavn(Personnavn().withFornavn("Aremark"))
                            .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent("10108000398")))
                    )
            )
        }

        every { personV3.hentPerson(any()) } returns mockPersonResponse

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
        .withPerson(
            Bruker()
                .withPersonnavn(Personnavn())
                .withKjoenn(Kjoenn().withKjoenn(Kjoennstyper().withValue("K")))
                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent(FNR)))
        )

    @Nested
    inner class Diskresjonskode {

        @Test
        fun `Med diskresjonskode`() {
            val mockPersonResponse = mockPersonResponse().apply {
                person.withDiskresjonskode(Diskresjonskoder().withValue("SPFO"))
            }
            every { personV3.hentPerson(any()) } returns mockPersonResponse

            val response = controller.hent(FNR)
            val diskresjonskode = response["diskresjonskode"] as Kode

            assertEquals("SPFO", diskresjonskode.kodeRef)
        }

        @Test
        fun `Uten diskresjonskode`() {
            every { personV3.hentPerson(any()) } returns mockPersonResponse()

            val response = controller.hent(FNR)
            val diskresjonskode = response["diskresjonskode"]

            assertEquals(null, diskresjonskode)
        }

        @Test
        fun `Familiemedlem med diskresjonskode`() {
            val mockPersonResponse = mockPersonResponse().apply {
                person.withHarFraRolleI(
                    Familierelasjon()
                        .withTilPerson(
                            Person()
                                .withDiskresjonskode(Diskresjonskoder().withValue("SPFO"))
                                .withAktoer(PersonIdent().withIdent(NorskIdent().withIdent("10108000398")))
                        )
                )
            }

            every { personV3.hentPerson(any()) } returns mockPersonResponse

            val response = controller.hent(FNR)
            val relasjoner = response["familierelasjoner"] as ArrayList<*>
            val rolle = relasjoner[0] as Map<*, *>
            val relasjon = rolle["tilPerson"] as Map<*, *>
            val harSammeBosted = rolle["harSammeBosted"]
            val fodselsnummer = relasjon["fødselsnummer"]
            val navn = relasjon["navn"]
            val alder = relasjon["alder"]
            val alderIManeder = relasjon["alderMåneder"]

            assertEquals(null, fodselsnummer)
            assertEquals(null, harSammeBosted)
            assertEquals(null, navn)
            assertEquals(null, alder)
            assertEquals(null, alderIManeder)
        }
    }

    @Nested
    inner class Kontaktinformasjon {

        private val RETNINGSNUMMER = "46"
        private val TELEFONNUMMER = "10108000398"

        @Test
        fun `Med mobil`() {
            val mockPersonResponse = responseMedMobil()
            every { personV3.hentPerson(any()) } returns mockPersonResponse

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil =
                kontaktinformasjon["mobil"] as no.nav.modiapersonoversikt.rest.person.Telefonnummer
            val retningsnummer = mobil.retningsnummer
            val nummer = mobil.identifikator

            assertEquals(TELEFONNUMMER, nummer)
            assertEquals(RETNINGSNUMMER, retningsnummer?.kodeRef)
        }

        private fun responseMedMobil() = mockPersonResponse().apply {
            (person as Bruker)
                .withKontaktinformasjon(
                    Telefonnummer()
                        .withIdentifikator(TELEFONNUMMER)
                        .withRetningsnummer(Retningsnumre().withValue(RETNINGSNUMMER))
                        .withEndretAv("BRUKER")
                        .withType(Telefontyper().withValue("MOBI"))
                )
        }

        @Test
        fun `Uten mobil`() {
            every { personV3.hentPerson(any()) } returns mockPersonResponse()

            val response = controller.hent(FNR)
            val kontaktinformasjon = response["kontaktinformasjon"] as Map<*, *>
            val mobil = kontaktinformasjon["mobil"]

            assertEquals(null, mobil)
        }
    }

    @Nested
    inner class `Tilrettelagt kommunikasjon` {

        @Test
        fun Mapping() {
            every { kodeverk.getKodeverkList(any(), any()) } returns listOf(Kodeverdi("SV", "Svensk"))
            every { personV3.hentPerson(any()) } returns mockPersonResponse()
            every { pdlOppslagService.hentPerson(any()) } returns mockPdlPerson().copy(
                tilrettelagtKommunikasjon = listOf(
                    HentPerson.TilrettelagtKommunikasjon(
                        talespraaktolk = HentPerson.Tolk("SV"),
                        tegnspraaktolk = null
                    )
                )
            )

            val response = controller.hent(FNR)["tilrettelagtKomunikasjonsListe"] as List<TilrettelagtKommunikasjonsbehov>
            val tilrettelagtKommunikasjon = response[0]

            assertEquals(TilrettelagtKommunikasjonsbehovType.TALESPRAK, tilrettelagtKommunikasjon.type)
            assertEquals("SV", tilrettelagtKommunikasjon.kodeRef)
            assertEquals("Svensk", tilrettelagtKommunikasjon.beskrivelse)
        }
    }

    fun mockPdlPerson(): HentPerson.Person {
        return HentPerson.Person(
            navn = emptyList(),
            tilrettelagtKommunikasjon = emptyList(),
            fullmakt = emptyList(),
            kontaktinformasjonForDoedsbo = emptyList(),
            telefonnummer = emptyList(),
            vergemaalEllerFremtidsfullmakt = emptyList(),
            foreldreansvar = emptyList(),
            deltBosted = emptyList()
        )
    }

    @Nested
    inner class Bankkonto {

        @Test
        fun `Norsk konto`() {
            every { personV3.hentPerson(any()) } returns mockPersonResponse()
                .apply {
                    (person as Bruker)
                        .withBankkonto(
                            BankkontoNorge()
                                .withBankkonto(
                                    Bankkontonummer()
                                        .withBankkontonummer(KONTONUMMER)
                                        .withBanknavn(BANKNAVN)
                                )
                        )
                }

            val response = controller.hent(FNR)["bankkonto"] as Map<*, *>

            assertEquals(KONTONUMMER, response["kontonummer"])
            assertEquals(BANKNAVN, response["banknavn"])
        }

        @Test
        fun `Utenlandsk konto`() {
            every { personV3.hentPerson(any()) } returns mockPersonResponse()
                .apply {
                    (person as Bruker)
                        .withBankkonto(
                            BankkontoUtland()
                                .withBankkontoUtland(
                                    BankkontonummerUtland()
                                        .withBankkontonummer(KONTONUMMER)
                                        .withBanknavn(BANKNAVN)
                                        .withSwift(SWIFT)
                                        .withLandkode(Landkoder().withValue(LANDKODE))
                                )
                        )
                }

            val response = controller.hent(FNR)["bankkonto"] as Map<*, *>

            assertEquals(KONTONUMMER, response["kontonummer"])
            assertEquals(BANKNAVN, response["banknavn"])
            assertEquals(SWIFT, response["swift"])
            assertEquals(LANDKODE, (response["landkode"] as Kode).kodeRef)
        }
    }
}
