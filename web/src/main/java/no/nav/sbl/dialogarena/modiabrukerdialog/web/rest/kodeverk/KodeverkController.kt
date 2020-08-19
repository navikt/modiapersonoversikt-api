package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk

import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import org.springframework.beans.factory.annotation.Autowired
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/kodeverk/{kodeverkRef}")
@Produces(APPLICATION_JSON)
class KodeverkController @Autowired constructor(
        private val tilgangskontroll: Tilgangskontroll,
        private val kodeverkManager: KodeverkmanagerBi
) {

    @GET
    @Path("/")
    fun hentKodeverk(@PathParam("kodeverkRef") kodeverkRef: String) =
            tilgangskontroll
                    .check(Policies.tilgangTilModia)
                    .get(Audit.skipAuditLog()) {
                        mapOf(
                                "kodeverk" to kodeverkManager
                                        .getKodeverkList(kodeverkRef, "nb")
                                        .map(::Kode)
                        )
                    }

}

data class Kode(val kodeRef: String, val beskrivelse: String) {
    constructor(kodeverdi: Kodeverdi) : this(kodeverdi.kodeRef, kodeverdi.beskrivelse)
}
