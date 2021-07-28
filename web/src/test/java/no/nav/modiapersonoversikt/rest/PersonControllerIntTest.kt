package no.nav.modiapersonoversikt.rest

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.TilgangskontrollMock
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentNavnBolk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Fodselsnummer
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.Person
import no.nav.modiapersonoversikt.rest.person.PersonController
import no.nav.modiapersonoversikt.rest.person.Telefonnummer
import org.junit.jupiter.api.Test
import java.time.*
import kotlin.test.assertEquals

internal class PersonControllerIntTest {
    @Test
    fun `verifiserer at informasjon fra pdl blir riktig merget i personcontroller`() {
        val clock = Clock.fixed(Instant.parse("2020-04-20T12:00:00.00Z"), ZoneId.systemDefault())
        val kjerneinfoMock: PersonKjerneinfoServiceBi = mockk()
        val pdlMock: PdlOppslagService = mockk()
        val standardKodeverk: StandardKodeverk = mockk()
        val advokatSomKontakt = HentPerson.KontaktinformasjonForDoedsboAdvokatSomKontakt(
            HentPerson.Personnavn(
                fornavn = "Ola",
                mellomnavn = null,
                etternavn = "Nordmann"
            ),
            organisasjonsnavn = null,
            organisasjonsnummer = null
        )
        val adresse = HentPerson.KontaktinformasjonForDoedsboAdresse(
            adresselinje1 = "testadresse 21",
            adresselinje2 = null,
            poststedsnavn = "Fossviken",
            postnummer = "1234",
            landkode = null
        )
        val pdldodsbo = HentPerson.KontaktinformasjonForDoedsbo(
            adresse = adresse,
            attestutstedelsesdato = HentPerson.Date.create("2019-12-12"),
            organisasjonSomKontakt = null,
            skifteform = HentPerson.KontaktinformasjonForDoedsboSkifteform.ANNET,
            personSomKontakt = null,
            advokatSomKontakt = advokatSomKontakt
        )
        val kontaktiformasjonForDoedsbo: List<HentPerson.KontaktinformasjonForDoedsbo> = listOf(pdldodsbo)
        val vergemal = HentPerson.VergemaalEllerFremtidsfullmakt(
            type = null,
            embete = null,
            vergeEllerFullmektig = HentPerson.VergeEllerFullmektig(
                navn = HentPerson.Personnavn("Fornavn", "Mellomnavn", "Etternavn"),
                motpartsPersonident = null,
                omfang = null,
                omfangetErInnenPersonligOmraade = false
            ),
            folkeregistermetadata = null
        )
        val vergemalMedMotpartsIdent = vergemal.copy(
            vergeEllerFullmektig = vergemal.vergeEllerFullmektig.copy(
                motpartsPersonident = "12345678910"
            )
        )
        val vergemaalEllerFremtidsfullmakt: List<HentPerson.VergemaalEllerFremtidsfullmakt> = listOf(
            vergemal,
            vergemalMedMotpartsIdent
        )
        val foreldreansvar = listOf(
            HentPerson.Foreldreansvar(
                ansvar = "felles",
                ansvarlig = null,
                ansvarssubjekt = "12345678910",
                ansvarligUtenIdentifikator = HentPerson.RelatertBiPerson(
                    navn = HentPerson.Personnavn("Fornavn", "Mellomnavn", "Etternavn"),
                    foedselsdato = null,
                    kjoenn = null,
                    statsborgerskap = null
                ),
                metadata = HentPerson.Metadata2(historisk = false)
            )
        )

        every { kjerneinfoMock.hentKjerneinformasjon(any()) } returns HentKjerneinformasjonResponse()
            .apply {
                this.person = Person()
                    .apply {
                        this.fodselsnummer = Fodselsnummer("10108000398".trimIndent())
                    }
            }
        every { pdlMock.hentNavnBolk(any()) } returns mapOf(
            "12345678910" to HentNavnBolk.Navn("Verge", "Vergesen", "Olsen")
        )
        every { pdlMock.hentPerson(any()) } returns HentPerson.Person(
            navn = emptyList(),
            kjoenn = emptyList(),
            kontaktinformasjonForDoedsbo = kontaktiformasjonForDoedsbo,
            tilrettelagtKommunikasjon = emptyList(),
            fullmakt = emptyList(),
            telefonnummer = listOf(
                HentPerson.Telefonnummer(
                    "+47",
                    "10101010",
                    1,
                    HentPerson.Metadata(
                        listOf(
                            HentPerson.Endring(
                                HentPerson.DateTime(LocalDateTime.now(clock)),
                                "BRUKER"
                            )
                        )
                    )
                )
            ),
            vergemaalEllerFremtidsfullmakt = vergemaalEllerFremtidsfullmakt,
            foreldreansvar = foreldreansvar,
            deltBosted = emptyList()
        )

        val personController = PersonController(
            kjerneinfoMock,
            mockk(),
            TilgangskontrollMock.get(),
            pdlMock,
            standardKodeverk
        )

        val person = personController.hent("10108000398")
        val fornavn = person.deepget("kontaktinformasjonForDoedsbo.0.adressat.advokatSomAdressat.kontaktperson.fornavn")
        val telefonnummer = person.deepget("telefonnummer.0") as Telefonnummer
        val vergeUtenMotpartsIdent = person.deepget("vergemal.0") as PersonController.VergemalDTO
        val vergeMedMotpartsIdent = person.deepget("vergemal.1") as PersonController.VergemalDTO
        val foreldreansvarlig = person.deepget("foreldreansvar.0") as PersonController.ForeldreansvarDTO

        assertEquals("Ola", fornavn)
        assertEquals("+47", telefonnummer.retningsnummer?.kodeRef)
        assertEquals("10101010", telefonnummer.identifikator)
        assertEquals("2020-04-20", telefonnummer.sistEndret)
        assertEquals("BRUKER", telefonnummer.sistEndretAv)
        assertEquals("Fornavn Mellomnavn Etternavn", vergeUtenMotpartsIdent.navn?.sammensatt)
        assertEquals(null, vergeUtenMotpartsIdent.ident)
        assertEquals("Verge Vergesen Olsen", vergeMedMotpartsIdent.navn?.sammensatt)
        assertEquals("12345678910", vergeMedMotpartsIdent.ident)
        assertEquals("felles", foreldreansvarlig.ansvar)
    }
}

fun Map<String, Any?>.deepget(path: String): Any? {
    var instance: Any? = this
    path.split(".")
        .forEach { fragment ->
            instance = when (instance) {
                is Map<*, *> -> {
                    (instance as Map<*, *>)[fragment]
                }
                is List<*> -> {
                    (instance as List<*>).get(Integer(fragment).toInt())
                }
                else -> {
                    throw IllegalStateException("Instance er $instance prøver å hente ut $fragment")
                }
            }
        }

    return instance
}
