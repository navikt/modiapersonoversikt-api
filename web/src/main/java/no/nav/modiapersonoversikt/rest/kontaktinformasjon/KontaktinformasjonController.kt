package no.nav.modiapersonoversikt.rest.kontaktinformasjon

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.*
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources.Person
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/person/kontaktinformasjon")
class KontaktinformasjonController
    @Autowired
    constructor(
        private val krrService: Krr.Service,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @PostMapping
        fun hentKontaktinformasjon(
            @RequestBody fnrRequest: FnrRequest,
        ): KontaktinformasjonApi.Kontaktinformasjon =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(READ, Person.Kontaktinformasjon, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    val response = krrService.hentDigitalKontaktinformasjon(fnrRequest.fnr)

                    KontaktinformasjonApi.Kontaktinformasjon(
                        epost = getEpost(response),
                        mobiltelefon = getMobiltelefon(response),
                        reservasjon = getReservasjon(response),
                    )
                }

        private fun getEpost(response: Krr.DigitalKontaktinformasjon): KontaktinformasjonApi.Verdi<String>? {
            if (response.epostadresse?.value.isNullOrEmpty()) {
                return null
            }
            return KontaktinformasjonApi.Verdi(
                value = requireNotNull(response.epostadresse?.value),
                sistOppdatert = response.epostadresse?.sistOppdatert,
            )
        }

        private fun getMobiltelefon(response: Krr.DigitalKontaktinformasjon): KontaktinformasjonApi.Verdi<String>? {
            if (response.mobiltelefonnummer?.value.isNullOrEmpty()) {
                return null
            }
            return KontaktinformasjonApi.Verdi(
                value = requireNotNull(response.mobiltelefonnummer?.value),
                sistOppdatert = response.mobiltelefonnummer?.sistOppdatert,
            )
        }

        private fun getReservasjon(response: Krr.DigitalKontaktinformasjon): KontaktinformasjonApi.Verdi<Boolean>? {
            if (response.reservasjon?.value == null) {
                return null
            }
            return KontaktinformasjonApi.Verdi(
                value = requireNotNull(response.reservasjon.value),
                sistOppdatert = response.reservasjon.sistOppdatert,
            )
        }
    }
