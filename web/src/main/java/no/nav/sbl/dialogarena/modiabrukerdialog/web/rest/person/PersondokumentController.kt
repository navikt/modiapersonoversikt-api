package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person.PersonOppslagService
import no.nav.tjenester.person.oppslag.v1.domain.personident.utenlandskidentifikasjonsnummer.UtenlandskIdentifikasjonsnummer
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/person/{fnr}/persondokument")
@Produces(MediaType.APPLICATION_JSON)
class PersondokumentController @Inject constructor(private val persondokumentService: PersonOppslagService) {

    @GET
    @Path("/")
    fun hentPersonDokument(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        try {
            val response = persondokumentService.hentPersonDokument(fødselsnummer)
            return mapOf(
                    "utenlandskeIdentifikasjonsnummere" to hentUtenlandskeIdentifikasjonsNummere(response.personidenter.utenlandskeIdentifikasjonsnummere)
            )
        } catch (exception: NotFoundException) {
           throw NotFoundException(exception)
        }
    }

    private fun hentUtenlandskeIdentifikasjonsNummere(utenlandskeIdentifikasjonsnummere: List<UtenlandskIdentifikasjonsnummer>): List<Map<String, Any?>> {
        return utenlandskeIdentifikasjonsnummere.map {
            mapOf(
                    "idNummer" to it.idNummer,
                    "utstederland" to it.utstederland,
                    "registrertINAV" to it.registrertINAV?.toString(),
                    "master" to it.master,
                    "kilde" to it.kilde,
                    "idNummerType" to it.idNummertype,
                    "systemKilde" to it.systemKilde,
                    "registrertAv" to it.registrertAv,
                    "gyldigFom" to it.gyldigFom?.toString(),
                    "gyldigTom" to it.gyldigTom?.toString()
            )
        }

    }
}