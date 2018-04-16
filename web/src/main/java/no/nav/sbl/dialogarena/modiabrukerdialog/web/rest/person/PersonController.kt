package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON


@Path("/person/{fnr}")
@Produces(APPLICATION_JSON)
class PersonController @Inject constructor(private val kjerneinfoService: PersonKjerneinfoServiceBi) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String): Map<String, Any> {

        check(visFeature(PERSON_REST_API))

        val person = try {
            kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fødselsnummer)).person
        } catch (exception: RuntimeException) {
            when (exception.cause) {
                is HentPersonPersonIkkeFunnet -> throw NotFoundException()
                is HentPersonSikkerhetsbegrensning -> throw NotAuthorizedException("Ingen tilgang til denne brukeren")
                else -> throw InternalServerErrorException()
            }
        }

        return mapOf(
                "fødselsnummer" to person.fodselsnummer.nummer,
                "alder" to person.fodselsnummer.alder,
                "kjønn" to person.personfakta.kjonn.value,
                "geografiskTilknytning" to person.personfakta.geografiskTilknytning.value,
                "navn" to mapOf(
                        "sammensatt" to person.personfakta.personnavn.sammensattNavn,
                        "fornavn" to person.personfakta.personnavn.fornavn,
                        "mellomnavn" to (person.personfakta.personnavn.mellomnavn ?: ""),
                        "etternavn" to person.personfakta.personnavn.etternavn
                ),
                "diskresjonskode" to (person.personfakta.diskresjonskode?.value ?: ""),
                "bankkonto" to mapOf(
                        "erNorskKonto" to person.personfakta.isBankkontoINorge,
                        "kontonummer" to person.personfakta.bankkonto.kontonummer,
                        "bank" to person.personfakta.bankkonto.banknavn,
                        "sistEndret" to person.personfakta.bankkonto.endringsinformasjon.sistOppdatert,
                        "sistEndretAv" to person.personfakta.bankkonto.endringsinformasjon.endretAv
                ),
                "diskresjonskode" to (person.personfakta.diskresjonskode?.value ?: ""),
                "status" to mapOf(
                        "dødsdato" to person.personfakta.doedsdato,
                        "bostatus" to person.personfakta.bostatus?.value
                ),
                "statsborgerskap" to (person.personfakta.statsborgerskap?.beskrivelse?: "")
        )

    }

}
