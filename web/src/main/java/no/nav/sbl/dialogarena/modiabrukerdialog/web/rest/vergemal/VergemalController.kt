package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal

import no.nav.kjerneinfo.consumer.fim.person.vergemal.VergemalService
import no.nav.kjerneinfo.consumer.fim.person.vergemal.domain.Verge
import no.nav.kjerneinfo.domain.person.Personnavn
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.AuditResources
import no.nav.sbl.dialogarena.naudit.Audit
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON


@Path("/person/{fnr}/vergemal")
@Produces(APPLICATION_JSON)
class VergemalController @Inject constructor(private val vergemalService: VergemalService, private val tilgangskontroll: Tilgangskontroll) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Vergemal, "fnr" to fnr)) {
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
                    "navn" to it.personnavn?.let { getNavn(it) },
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

    private fun getNavn(personnavn: Personnavn): Map<String, String> {
        return mapOf(
                "sammensatt" to personnavn.sammensattNavn
        )
    }

}
