package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.kjerneinfo.domain.person.*
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON


private const val TPS_UKJENT_VERDI = "???"
private const val DATO_TID_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"

@Path("/person/{fnr}")
@Produces(APPLICATION_JSON)
class PersonController @Inject constructor(private val kjerneinfoService: PersonKjerneinfoServiceBi) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {

        check(visFeature(PERSON_REST_API))

        val person = try {
            kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fødselsnummer)).person
        } catch (exception: RuntimeException) {
            when (exception.cause) {
                is HentPersonPersonIkkeFunnet -> throw NotFoundException()
                is HentPersonSikkerhetsbegrensning -> throw NotAuthorizedException("Ingen tilgang til denne brukeren")
                else -> throw InternalServerErrorException(exception)
            }
        }

        return mapOf(
                "fødselsnummer" to person.fodselsnummer.nummer,
                "alder" to person.fodselsnummer.alder,
                "kjønn" to person.personfakta.kjonn.value,
                "geografiskTilknytning" to person.personfakta.geografiskTilknytning?.value,
                "navn" to mapOf(
                        "sammensatt" to person.personfakta.personnavn.sammensattNavn,
                        "fornavn" to person.personfakta.personnavn.fornavn,
                        "mellomnavn" to (person.personfakta.personnavn.mellomnavn ?: ""),
                        "etternavn" to person.personfakta.personnavn.etternavn
                ),
                "diskresjonskode" to (person.personfakta.diskresjonskode?.value ?: ""),
                "bankkonto" to hentBankkonto(person),
                "status" to mapOf(
                        "dødsdato" to person.personfakta.doedsdato,
                        "bostatus" to person.personfakta.bostatus?.value
                ),
                "statsborgerskap" to getStatsborgerskap(person),
                "sivilstand" to mapOf(
                        "value" to person.personfakta.sivilstand?.value,
                        "beskrivelse" to person.personfakta.sivilstand?.beskrivelse
                ),
                "folkeregistrertAdresse" to person.personfakta.bostedsadresse?.let{ hentAdresse(it) },
                "alternativAdresse" to person.personfakta.alternativAdresse?.let { hentAdresse(it) },
                "postadresse" to person.personfakta.postadresse?.let { hentAdresse(it) }
        )
    }

    private fun getStatsborgerskap(person: Person): String? {
        if (person.personfakta.statsborgerskap?.beskrivelse == TPS_UKJENT_VERDI) {
            return null
        } else {
            return person.personfakta.statsborgerskap?.beskrivelse
        }
    }

    private fun hentBankkonto(person: Person): Map<String, Any>? {
        if (person.personfakta.bankkonto != null) {
            return mapOf(
                    "erNorskKonto" to person.personfakta.isBankkontoINorge,
                    "kontonummer" to person.personfakta.bankkonto.kontonummer,
                    "bank" to person.personfakta.bankkonto.banknavn,
                    "sistEndret" to person.personfakta.bankkonto.endringsinformasjon.sistOppdatert,
                    "sistEndretAv" to person.personfakta.bankkonto.endringsinformasjon.endretAv
            )
        }
        return null
    }

    private fun hentAdresse(adresselinje: Adresselinje): Map<String, Any?> {
        return mapOf("endringsinfo" to adresselinje.endringsinformasjon?.let { hentEndringsinformasjon(it) },
            when(adresselinje) {
                is Adresse -> "gateadresse" to hentGateAdresse(adresselinje)
                is Matrikkeladresse -> "matrikkeladresse" to hentMatrikkeladresse(adresselinje)
                is AlternativAdresseUtland -> "utlandsadresse" to hentAlternativAdresseUtland(adresselinje)
                else -> "ustrukturert" to adresselinje.adresselinje
            }
        )
    }

    private fun hentGateAdresse(adresse: Adresse): Map<String, Any?> {
        return mapOf(
                "tilleggsadresse" to adresse.tilleggsadresse,
                "gatenavn" to adresse.gatenavn,
                "husnummer" to adresse.gatenummer,
                "postnummer" to adresse.postnummer,
                "poststed" to adresse.poststednavn,
                "husbokstav" to adresse.husbokstav,
                "bolignummer" to adresse.bolignummer
        )
    }

    private fun hentMatrikkeladresse(matrikkeladresse: Matrikkeladresse): Map<String, Any?> {
        return mapOf(
                "tilleggsadresse" to matrikkeladresse.tilleggsadresseMedType,
                "eiendomsnavn" to matrikkeladresse.eiendomsnavn,
                "postnummer" to matrikkeladresse.postnummer,
                "poststed" to matrikkeladresse.poststed
        )
    }

    private fun hentAlternativAdresseUtland(alternativAdresseUtland: AlternativAdresseUtland): Map<String, Any?> {
        return mapOf(
                "landkode" to alternativAdresseUtland.landkode.value,
                "adresselinje" to alternativAdresseUtland.adresselinje
        )
    }

    private fun hentEndringsinformasjon(endringsinformasjon: Endringsinformasjon): Map<String, Any?> {
        return mapOf(
                "sistEndretAv" to endringsinformasjon.endretAv,
                "sistEndret" to endringsinformasjon.sistOppdatert?.toString(DATO_TID_FORMAT)
        )
    }
}
