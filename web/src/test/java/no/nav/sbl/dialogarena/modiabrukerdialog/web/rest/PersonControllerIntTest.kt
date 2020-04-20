package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.kjerneinfo.domain.person.Fodselsnummer
import no.nav.kjerneinfo.domain.person.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.Telefonnummer
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*
import kotlin.test.assertEquals

internal class PersonControllerIntTest {
    @Test
    fun test() {
        val clock = Clock.fixed(Instant.parse("2020-04-20T12:00:00.00Z"), ZoneId.systemDefault())
        val kjerneinfoMock : PersonKjerneinfoServiceBi = mock()
        val pdlMock : PdlOppslagService = mock()
        val advokatSomKontakt = PdlDoedsboAdvokatSomKontakt(PdlPersonNavn(fornavn = "Ola",mellomnavn =  null, etternavn =  "Nordmann"), organisasjonsnavn = null, organisasjonsnummer = null)
        val adresse= PdlDoedsboAdresse(adresselinje1 = "testadresse 21", adresselinje2 = null, poststedsnavn = "Fossviken", postnummer = "1234", landkode = null)
        val pdldodsbo =  PdlDoedsbo(adresse = adresse, attestutstedelsesdato = Date(2019-12-12), organisasjonSomKontakt = null, skifteform = "annet", personSomKontakt = null, advokatSomKontakt = advokatSomKontakt   )
        val kontaktiformasjonForDoedsbo: List<PdlDoedsbo> = listOf(pdldodsbo)

        whenever(kjerneinfoMock.hentKjerneinformasjon(any())).thenReturn(HentKjerneinformasjonResponse()
                .apply {
                    this.person = Person()
                            .apply {
                                this.fodselsnummer = Fodselsnummer("10108000398".trimIndent())
                            }
                }
        )
        whenever(pdlMock.hentPerson(any())).thenReturn(PdlPersonResponse(null, PdlHentPerson(
                PdlPerson(
                        navn = emptyList(),
                        kontaktinformasjonForDoedsbo = kontaktiformasjonForDoedsbo,
                        tilrettelagtKommunikasjon = emptyList(),
                        fullmakt = emptyList(),
                        telefonnummer = listOf(PdlTelefonnummer(
                                "+47",
                                "10101010",
                                1,
                                PdlMetadata(listOf(PdlEndringer(
                                        Date.from(clock.instant()),
                                        "BRUKER"
                                )))
                        ))
                )
        )))


        val personController = PersonController(
                kjerneinfoMock,
                mock(),
                TilgangskontrollMock.get(),
                pdlMock
        )

        val person = personController.hent("10108000398")
        val fornavn = person.deepget("kontaktinformasjonForDoedsbo.0.adressat.advokatSomAdressat.kontaktperson.fornavn")
        val telefonnummer = person.deepget("telefonnummer.0") as Telefonnummer

        assertEquals("Ola", fornavn)
        assertEquals("+47", telefonnummer.retningsnummer?.kodeRef)
        assertEquals("10101010", telefonnummer.identifikator)
        assertEquals("2020-04-20", telefonnummer.sistEndret)
        assertEquals("BRUKER", telefonnummer.sistEndretAv)
    }
}


fun Map<String, Any?>.deepget(path: String): Any? {
    var instance: Any? = this
    path.split(".")
            .forEach {
                fragment ->
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


