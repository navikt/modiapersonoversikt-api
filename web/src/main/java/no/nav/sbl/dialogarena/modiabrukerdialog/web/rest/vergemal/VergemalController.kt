package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import no.nav.kjerneinfo.consumer.fim.person.vergemal.PdlVergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.PdlVerge
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/person/{fnr}/vergemal")
class VergemalController @Autowired constructor(private val vergemalService: PdlVergemalService, private val tilgangskontroll: Tilgangskontroll) {

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

    private fun getVerger(vergemal: List<PdlVerge>): List<Map<String, Any?>> {
        return vergemal.map {
            mapOf(
                    "ident" to it.getIdent(),
                    "navn" to it.getPersonnavn(),
                    "embete" to it.getEmbete(),
                    "mandattype" to it.getOmfang(),
                    "vergesakstype" to it.getVergesakstype(),
                    "virkningsperiode" to mapOf(
                            "fom" to it.getGyldighetstidspunkt(),
                            "tom" to it.getOpphoerstidspunkt()
                    )
            )
        }
    }
}
