package no.nav.modiapersonoversikt.rest.vergemal

import no.nav.modiapersonoversikt.api.domain.pdl.generated.HentNavnBolk
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/person/{fnr}/vergemal")
class VergemalController @Autowired constructor(private val vergemalService: VergemalService, private val tilgangskontroll: Tilgangskontroll) {

    @GetMapping
    fun hent(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Vergemal, AuditIdentifier.FNR to fnr)) {
                val vergemal = vergemalService.hentVergemal(fnr)

                mapOf(
                    "verger" to getVerger(vergemal)
                )
            }
    }

    private fun getVerger(vergemal: List<Verge>): List<Map<String, Any?>> {
        return vergemal.map {
            mapOf(
                "ident" to it.ident,
                "navn" to it.personnavn?.let { navn -> getNavn(navn) },
                "embete" to it.embete,
                "mandattekst" to it.mandattekst,
                "mandattype" to it.mandattype,
                "vergesakstype" to it.vergesakstype,
                "vergetype" to it.vergetype,
                "virkningsperiode" to mapOf(
                    "fom" to it.virkningsperiode.fom,
                    "tom" to it.virkningsperiode.tom
                )
            )
        }
    }

    private fun getNavn(personnavn: HentNavnBolk.Navn): Map<String, String> {
        return mapOf(
            "sammensatt" to with(personnavn) {
                listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(" ")
            }
        )
    }
}
