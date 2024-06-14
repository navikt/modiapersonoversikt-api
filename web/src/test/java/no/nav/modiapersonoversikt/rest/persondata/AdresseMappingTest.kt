package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Postboksadresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Vegadresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.Person
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.Bostedsadresse as TredjepartsBostedsadresse
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.Vegadresse as TredjepartsVegadresse

internal class AdresseMappingTest {
    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = PersondataFletter(kodeverk)

    @BeforeEach
    fun setup() {
        every { kodeverk.hentKodeverk<Any, Any>(any()) } returns
            EnhetligKodeverk.Kodeverk(
                "",
                mapOf(
                    "9750" to "Nordkapp",
                ),
            )
    }

    @Test
    internal fun `skal mappe vegadresse likt for person og tredjepartsperson`() {
        val hovedperson =
            testPerson.copy(
                bostedsadresse =
                    adresse.copy(
                        vegadresse =
                            Vegadresse(
                                matrikkelId = 1234L,
                                husnummer = "12",
                                husbokstav = "A",
                                bruksenhetsnummer = "H0101",
                                adressenavn = "fin veg",
                                kommunenummer = "1234",
                                postnummer = "6789",
                                bydelsnummer = null,
                                tilleggsnavn = null,
                            ),
                    ).asList(),
            )
        val tredjepartsPerson =
            gittTredjepartsperson().copy(
                bostedsadresse =
                    TredjepartsBostedsadresse(
                        folkeregistermetadata = null,
                        vegadresse =
                            TredjepartsVegadresse(
                                husnummer = "12",
                                husbokstav = "A",
                                bruksenhetsnummer = "H0101",
                                adressenavn = "fin veg",
                                kommunenummer = "1234",
                                postnummer = "6789",
                                bydelsnummer = null,
                                tilleggsnavn = null,
                            ),
                        matrikkeladresse = null,
                        utenlandskAdresse = null,
                        ukjentBosted = null,
                    ).asList(),
            )

        val barnFnr = "98765432100"
        val tredjepartsPersonData =
            TredjepartspersonMapper(kodeverk)
                .lagTredjepartsperson(
                    barnFnr,
                    tredjepartsPerson,
                    PersondataService.Tilganger(kode6 = true, kode7 = true),
                    kontaktinformasjonTredjepartsperson,
                )

        val persondata =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = "",
                        persondata = hovedperson,
                        tredjepartsPerson =
                            PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) {
                                mapOf(barnFnr to requireNotNull(tredjepartsPersonData))
                            },
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        val harSammeAdresse = persondata.person.forelderBarnRelasjon.find { it.ident == barnFnr }?.harSammeAdresse
        Assertions.assertTrue(harSammeAdresse ?: false)
    }

    @Test
    internal fun `skal mappe postboksadresse riktig`() {
        val person =
            testPerson.copy(
                kontaktadresse =
                    kontaktadresseData.copy(
                        coAdressenavn = null,
                        vegadresse = null,
                        postboksadresse =
                            Postboksadresse(
                                postbokseier = "C/O Fornavn Etternavn",
                                postboks = "123",
                                postnummer = "9750",
                            ),
                    ).asList(),
            )

        val persondata =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = "",
                        persondata = person,
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        val kontaktAdresse = persondata.person.kontaktAdresse.first()
        Assertions.assertEquals("C/O Fornavn Etternavn", kontaktAdresse.linje1)
        Assertions.assertEquals("Postboks 123", kontaktAdresse.linje2)
        Assertions.assertEquals("9750 Nordkapp", kontaktAdresse.linje3)
    }

    @Test
    internal fun `klarer å håndtere ekstra mellomrom på slutten av adressse`() {
        val hovedperson =
            testPerson.copy(
                bostedsadresse =
                    adresse.copy(
                        vegadresse =
                            Vegadresse(
                                matrikkelId = 1234L,
                                husnummer = "   12",
                                husbokstav = "A    ",
                                bruksenhetsnummer = "  H0101",
                                adressenavn = "fin veg",
                                kommunenummer = "1234",
                                postnummer = "6789",
                                bydelsnummer = null,
                                tilleggsnavn = null,
                            ),
                    ).asList(),
            )
        val tredjepartsPerson =
            gittTredjepartsperson().copy(
                bostedsadresse =
                    TredjepartsBostedsadresse(
                        folkeregistermetadata = null,
                        vegadresse =
                            TredjepartsVegadresse(
                                husnummer = "12",
                                husbokstav = "A",
                                bruksenhetsnummer = "H0101",
                                adressenavn = " fin     veg ",
                                kommunenummer = "1234",
                                postnummer = "6789",
                                bydelsnummer = null,
                                tilleggsnavn = null,
                            ),
                        matrikkeladresse = null,
                        utenlandskAdresse = null,
                        ukjentBosted = null,
                    ).asList(),
            )

        val barnFnr = "98765432100"
        val tredjepartsPersonData =
            TredjepartspersonMapper(kodeverk)
                .lagTredjepartsperson(
                    barnFnr,
                    tredjepartsPerson,
                    PersondataService.Tilganger(kode6 = true, kode7 = true),
                    kontaktinformasjonTredjepartsperson,
                )

        val persondata =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = "",
                        persondata = hovedperson,
                        tredjepartsPerson =
                            PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) {
                                mapOf(barnFnr to requireNotNull(tredjepartsPersonData))
                            },
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        val harSammeAdresse = persondata.person.forelderBarnRelasjon.find { it.ident == barnFnr }?.harSammeAdresse
        Assertions.assertTrue(harSammeAdresse ?: false)
    }

    private fun gittTredjepartsperson() =
        Person(
            navn = emptyList(),
            kjoenn = emptyList(),
            foedselsdato = emptyList(),
            foedested = emptyList(),
            adressebeskyttelse = emptyList(),
            bostedsadresse = emptyList(),
            doedsfall = emptyList(),
        )

    private fun <T : Any> T.asList(): List<T> = listOf(this)
}
