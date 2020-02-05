package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonResponse
import no.nav.kjerneinfo.domain.person.Fodselsnummer
import no.nav.kjerneinfo.domain.person.Person
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangskontrollMock
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class PersonControllerIntTest {
    @Test
    fun test() {
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
                                this.fodselsnummer = Fodselsnummer("10108000398")
                            }
                }
        )
        whenever(pdlMock.hentPerson(any())).thenReturn(PdlPersonResponse(null, PdlHentPerson(
                PdlPerson(
                        navn = emptyList(),
                        kontaktinformasjonForDoedsbo = kontaktiformasjonForDoedsbo,
                        tilrettelagtKommunikasjon = emptyList(),
                        fullmakt = emptyList()
                )
        )))


        val personController = PersonController(
                kjerneinfoMock,
                mock(),
                mock(),
                TilgangskontrollMock.get(),
                pdlMock
        )

        val person = personController.hent("10108000398")
        val fornavn = person.deepget("kontaktinformasjonForDoedsbo.0.adressat.advokatSomAdressat.kontaktperson.fornavn")
        assertEquals("Ola", fornavn)
    }

    //{fødselsnummer=10108000398, alder=39, kjønn=null, geografiskTilknytning=null, navn={endringsinfo=null, sammensatt=null, fornavn=, mellomnavn=, etternavn=}, diskresjonskode=null, bankkonto=null, tilrettelagtKomunikasjonsListe=[], personstatus={dødsdato=null, bostatus=null}, statsborgerskap=null, sivilstand={kodeRef=null, beskrivelse=null, fraOgMed=null}, familierelasjoner=null, fodselsdato=1980-10-10, folkeregistrertAdresse=null, alternativAdresse=null, postadresse=null, sikkerhetstiltak=null, kontaktinformasjon=null, kontaktinformasjonForDoedsbo=[{adressat={advokatSomAdressat={kontaktperson={fornavn=Ola, etternavn=Nordmann, mellomnavn=null, sammensatt=Ola  Nordmann}, organisasjonsnavn=null, organisasjonsnummer=null}, organisasjonSomAdressat=null}, adresselinje1=testadresse 21, adresselinje2=null, postnummer=1234, poststed=Fossviken, landkode=null, registrert=1970-01-01}], fullmakt=[]}



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


