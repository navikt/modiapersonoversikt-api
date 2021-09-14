package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.dkif.Dkif
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/person/{fnr}/kontaktinformasjon")
class KontaktinformasjonController @Autowired constructor(
    @Qualifier("DkifSoap") private val dkifSoapService: Dkif.Service,
    @Qualifier("DkifRest") private val dkifRestService: Dkif.Service,
    private val tilgangskontroll: Tilgangskontroll
) {

    val dkifExperiment = Scientist.createExperiment<Dkif.DigitalKontaktinformasjon>(
        Scientist.Config(
            name = "dkif",
            experimentRate = 0.0
        )
    )

    @GetMapping
    fun hentKontaktinformasjon(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(READ, Person.Kontaktinformasjon, AuditIdentifier.FNR to fnr)) {
                val response = dkifExperiment.run(
                    control = {
                        dkifSoapService.hentDigitalKontaktinformasjon(fnr)
                    },
                    experiment = {
                        dkifRestService.hentDigitalKontaktinformasjon(fnr)
                    }
                )

                mapOf(
                    "epost" to getEpost(response),
                    "mobiltelefon" to getMobiltelefon(response),
                    "reservasjon" to response.reservasjon
                )
            }
    }

    private fun getEpost(response: Dkif.DigitalKontaktinformasjon): Map<String, Any?>? {
        if (response.epostadresse?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
            "value" to response.epostadresse?.value,
            "sistOppdatert" to response.epostadresse?.sistOppdatert
        )
    }

    private fun getMobiltelefon(response: Dkif.DigitalKontaktinformasjon): Map<String, Any?>? {
        if (response.mobiltelefonnummer?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
            "value" to response.mobiltelefonnummer?.value,
            "sistOppdatert" to response.mobiltelefonnummer?.sistOppdatert
        )
    }
}
