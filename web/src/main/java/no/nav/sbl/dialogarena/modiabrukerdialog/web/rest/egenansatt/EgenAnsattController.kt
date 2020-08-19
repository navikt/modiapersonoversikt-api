package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt

import no.nav.kjerneinfo.consumer.egenansatt.EgenAnsattService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.*
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Saksbehandler
import org.springframework.beans.factory.annotation.Autowired
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/egenansatt/{fnr}")
class EgenAnsattController @Autowired constructor(
        private val egenAnsattService: EgenAnsattService,
        private val tilgangskontroll: Tilgangskontroll
) {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    fun erEgenAnsatt(@PathParam("fnr") fnr: String): Map<String, Boolean> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(READ, Saksbehandler.EgenAnsatt, AuditIdentifier.FNR to fnr)) {
                    mapOf("erEgenAnsatt" to egenAnsattService.erEgenAnsatt(fnr))
                }
    }
}


