package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentTredjepartspersondata
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class AdresseMappingTest {
    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = PersondataFletter(kodeverk)

    @Test
    internal fun `skal mappe vegadresse likt for person og tredjepartsperson`() {
        every { kodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk("", emptyMap())
        val hovedperson = gittPerson().copy(
            bostedsadresse = adresse.copy(
                coAdressenavn = "C/O Her Herrmansen",
                vegadresse = HentPersondata.Vegadresse(
                    matrikkelId = HentPersondata.Long(1234L),
                    husnummer = "12",
                    husbokstav = "A",
                    bruksenhetsnummer = "H0101",
                    adressenavn = "fin veg",
                    kommunenummer = "1234",
                    postnummer = "6789",
                    bydelsnummer = null,
                    tilleggsnavn = null
                )
            ).asList()
        )
        val tredjepartsPerson = gittTredjepartsperson().copy(
            bostedsadresse = HentTredjepartspersondata.Bostedsadresse(
                folkeregistermetadata = null,
                coAdressenavn = "C/O Her Herrmansen",
                vegadresse = HentTredjepartspersondata.Vegadresse(
                    husnummer = "12",
                    husbokstav = "A",
                    bruksenhetsnummer = "H0101",
                    adressenavn = "fin veg",
                    kommunenummer = "1234",
                    postnummer = "6789",
                    bydelsnummer = null,
                    tilleggsnavn = null
                ),
                matrikkeladresse = null,
                utenlandskAdresse = null,
                ukjentBosted = null
            ).asList()
        )

        val barnFnr = "98765432100"
        val tredjepartsPersonData = TredjepartspersonMapper(kodeverk)
            .lagTredjepartsperson(barnFnr, tredjepartsPerson, PersondataService.Tilganger(true, true))

        val persondata = mapper.flettSammenData(
            gittData(
                persondata = hovedperson,
                tredjepartsPerson = PersondataResult.runCatching("tredjepartsperson") {
                    mapOf(barnFnr to requireNotNull(tredjepartsPersonData))
                }
            )
        )

        val harSammeAdresse = persondata.person.forelderBarnRelasjon.find { it.ident == barnFnr }?.harSammeAdresse
        Assertions.assertTrue(harSammeAdresse ?: false)
    }

    private fun gittTredjepartsperson() = HentTredjepartspersondata.Person(
        navn = emptyList(),
        kjoenn = emptyList(),
        foedsel = emptyList(),
        folkeregisterpersonstatus = emptyList(),
        adressebeskyttelse = emptyList(),
        bostedsadresse = emptyList()
    )

    private fun <T : Any> T.asList(): List<T> = listOf(this)
}
