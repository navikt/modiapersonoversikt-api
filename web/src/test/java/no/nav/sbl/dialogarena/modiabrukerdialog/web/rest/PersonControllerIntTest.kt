package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.kjerneinfo.domain.person.Fodselsnummer
import no.nav.kjerneinfo.domain.person.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentNavnBolk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.Telefonnummer
import org.junit.jupiter.api.Test
import java.time.*
import kotlin.test.assertEquals

internal class PersonControllerIntTest {
    @Test
    fun `verifiserer at informasjon fra pdl blir riktig merget i personcontroller`() {
        val clock = Clock.fixed(Instant.parse("2020-04-20T12:00:00.00Z"), ZoneId.systemDefault())
        val kjerneinfoMock: PersonKjerneinfoServiceBi = mock()
        val pdlMock: PdlOppslagService = mock()
        val standardKodeverk: StandardKodeverk = mock()
        val advokatSomKontakt = HentPerson.KontaktinformasjonForDoedsboAdvokatSomKontakt(
            HentPerson.Personnavn2(
                fornavn = "Ola",
                mellomnavn = null,
                etternavn = "Nordmann"
            ),
            organisasjonsnavn = null, organisasjonsnummer = null
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
                navn = HentPerson.Personnavn2("Fornavn", "Mellomnavn", "Etternavn"),
                motpartsPersonident = null,
                omfang = null,
                omfangetErInnenPersonligOmraade = false
            ),
            folkeregistermetadata = null,
            metadata = HentPerson.Metadata2(null)
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

        whenever(kjerneinfoMock.hentKjerneinformasjon(any())).thenReturn(
            HentKjerneinformasjonResponse()
                .apply {
                    this.person = Person()
                        .apply {
                            this.fodselsnummer = Fodselsnummer("10108000398".trimIndent())
                        }
                }
        )
        whenever(pdlMock.hentNavnBolk(any())).thenReturn(
            mapOf(
                "12345678910" to HentNavnBolk.Navn("Verge", "Vergesen", "Olsen")
            )
        )
        whenever(pdlMock.hentPerson(any())).thenReturn(
            HentPerson.Person(
                navn = emptyList(),
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
                vergemaalEllerFremtidsfullmakt = vergemaalEllerFremtidsfullmakt
            )
        )

        val personController = PersonController(
            kjerneinfoMock,
            mock(),
            TilgangskontrollMock.get(),
            pdlMock,
            standardKodeverk
        )

        val person = personController.hent("10108000398")
        val fornavn = person.deepget("kontaktinformasjonForDoedsbo.0.adressat.advokatSomAdressat.kontaktperson.fornavn")
        val telefonnummer = person.deepget("telefonnummer.0") as Telefonnummer
        val vergeUtenMotpartsIdent = person.deepget("vergemal.0") as PersonController.VergemalDTO
        val vergeMedMotpartsIdent = person.deepget("vergemal.1") as PersonController.VergemalDTO

        assertEquals("Ola", fornavn)
        assertEquals("+47", telefonnummer.retningsnummer?.kodeRef)
        assertEquals("10101010", telefonnummer.identifikator)
        assertEquals("2020-04-20", telefonnummer.sistEndret)
        assertEquals("BRUKER", telefonnummer.sistEndretAv)
        assertEquals("Fornavn Mellomnavn Etternavn", vergeUtenMotpartsIdent.navn?.sammensatt)
        assertEquals(null, vergeUtenMotpartsIdent.ident)
        assertEquals("Verge Vergesen Olsen", vergeMedMotpartsIdent.navn?.sammensatt)
        assertEquals("12345678910", vergeMedMotpartsIdent.ident)
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
