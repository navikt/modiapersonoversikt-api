package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon

import no.nav.dkif.consumer.DkifService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person
import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/person/{fnr}/kontaktinformasjon")
class KontaktinformasjonController @Autowired constructor(private val dkifService: DkifService, private val tilgangskontroll: Tilgangskontroll) {

    @GetMapping
    fun hentKontaktinformasjon(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Person.Kontaktinformasjon, AuditIdentifier.FNR to fnr)) {
                    val response = dkifService.hentDigitalKontaktinformasjon(fnr)

                    mapOf(
                            "epost" to getEpost(response),
                            "mobiltelefon" to getMobiltelefon(response),
                            "reservasjon" to response.digitalKontaktinformasjon.reservasjon
                    )
                }
    }

    private fun getEpost(response: WSHentDigitalKontaktinformasjonResponse): Map<String, Any>? {
        if (response.digitalKontaktinformasjon.epostadresse?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
                "value" to response.digitalKontaktinformasjon.epostadresse.value,
                "sistOppdatert" to response.digitalKontaktinformasjon.epostadresse.sistOppdatert
        )
    }

    private fun getMobiltelefon(response: WSHentDigitalKontaktinformasjonResponse): Map<String, Any>? {
        if (response.digitalKontaktinformasjon.mobiltelefonnummer?.value.isNullOrEmpty()) {
            return null
        }
        return mapOf(
                "value" to response.digitalKontaktinformasjon.mobiltelefonnummer.value,
                "sistOppdatert" to response.digitalKontaktinformasjon.mobiltelefonnummer.sistOppdatert
        )
    }

}
