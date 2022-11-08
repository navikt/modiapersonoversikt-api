package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.digdir.DigDir
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/person/{fnr}/kontaktinformasjon")
class KontaktinformasjonController @Autowired constructor(
    private val digDirService: DigDir.Service,
    private val tilgangskontroll: Tilgangskontroll
) {

    @GetMapping
    fun hentKontaktinformasjon(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(READ, Person.Kontaktinformasjon, AuditIdentifier.FNR to fnr)) {
                val response = digDirService.hentDigitalKontaktinformasjon(fnr)

                mapOf(
                    "epost" to getEpost(response),
                    "mobiltelefon" to getMobiltelefon(response),
                    "reservasjon" to response.reservasjon
                )
            }
    }

    private fun getEpost(response: DigDir.DigitalKontaktinformasjon): Map<String, Any?>? {
        if (response.epostadresse?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
            "value" to response.epostadresse?.value,
            "sistOppdatert" to response.epostadresse?.sistOppdatert
        )
    }

    private fun getMobiltelefon(response: DigDir.DigitalKontaktinformasjon): Map<String, Any?>? {
        if (response.mobiltelefonnummer?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
            "value" to response.mobiltelefonnummer?.value,
            "sistOppdatert" to response.mobiltelefonnummer?.sistOppdatert
        )
    }
}
