package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.mfn;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person.PersonOppslagService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/person/{fnr}/persondokument")
@Produces(MediaType.APPLICATION_JSON)
class PersondokumentController @Inject constructor(private val persondokumentService: PersonOppslagService) {
    @GET
    @Path("/")
    fun hentPersonDokument(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {

        val response = persondokumentService.hentPersonDokument(fødselsnummer);

        return mapOf(
                "antallUtenlandske" to response.personidenter.utenlandskeIdentifikasjonsnummere.size,
                "antallFolkeregister" to response.personidenter.folkeregisteridenter.size
        )
    }
}