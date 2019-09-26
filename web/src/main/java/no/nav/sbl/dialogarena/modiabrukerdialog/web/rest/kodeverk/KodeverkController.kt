package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk

import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/kodeverk/{kodeverkRef}")
@Produces(APPLICATION_JSON)
class KodeverkController @Inject constructor(private val kodeverkManager: KodeverkmanagerBi) {

    @GET
    @Path("/")
    fun hentKodeverk(@PathParam("kodeverkRef") kodeverkRef: String) = mapOf(
            "kodeverk" to kodeverkManager
                    .getKodeverkList(kodeverkRef, "nb")
                    .map(::Kode)
    )

}

data class Kode(val kodeRef: String, val beskrivelse: String) {
    constructor(kodeverdi: Kodeverdi) : this(kodeverdi.kodeRef, kodeverdi.beskrivelse)
}
