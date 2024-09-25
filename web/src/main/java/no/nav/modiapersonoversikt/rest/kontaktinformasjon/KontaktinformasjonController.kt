package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.krr.Krr
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
class KontaktinformasjonController
    @Autowired
    constructor(
        private val krrService: Krr.Service,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @GetMapping
        fun hentKontaktinformasjon(
            @PathVariable("fnr") fnr: String,
        ): KontaktinformasjonApi.Kontaktinformasjon =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnr)))
                .get(Audit.describe(READ, Person.Kontaktinformasjon, AuditIdentifier.FNR to fnr)) {
                    val response = krrService.hentDigitalKontaktinformasjon(fnr)

                    KontaktinformasjonApi.Kontaktinformasjon(
                        epost = getEpost(response),
                        mobiltelefon = getMobiltelefon(response),
                        reservasjon = response.reservasjon,
                    )
                }

        private fun getEpost(response: Krr.DigitalKontaktinformasjon): KontaktinformasjonApi.Verdi? {
            if (response.epostadresse?.value.isNullOrEmpty()) {
                return null
            }
            return KontaktinformasjonApi.Verdi(
                value = requireNotNull(response.epostadresse?.value),
                sistOppdatert = response.epostadresse?.sistOppdatert,
            )
        }

        private fun getMobiltelefon(response: Krr.DigitalKontaktinformasjon): KontaktinformasjonApi.Verdi? {
            if (response.mobiltelefonnummer?.value.isNullOrEmpty()) {
                return null
            }
            return KontaktinformasjonApi.Verdi(
                value = requireNotNull(response.mobiltelefonnummer?.value),
                sistOppdatert = response.mobiltelefonnummer?.sistOppdatert,
            )
        }
    }
