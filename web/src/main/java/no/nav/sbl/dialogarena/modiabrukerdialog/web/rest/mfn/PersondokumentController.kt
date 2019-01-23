package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.mfn;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.person.PersonOppslagService
import no.nav.sbl.dialogarena.utbetaling.domain.util.DateUtils
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

        val utenlandskeIdNummerRootElement = "utenlandskeIdentifikasjonsnummere"
        try {
            val response = persondokumentService.hentPersonDokument(fødselsnummer)
            return mapOf(
                    utenlandskeIdNummerRootElement to hentUtenlandskeIdentifikasjonsNummere(response.personidenter.utenlandskeIdentifikasjonsnummere)
            )
        } catch (exception: Exception) {
            when (exception.cause) {
                is NotFoundException -> throw NotFoundException(exception)
                else -> throw InternalServerErrorException(exception)
            }
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