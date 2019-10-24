package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagXmlGregorianDato
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimAdresseFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimPersonFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimSoekekriterie
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

private enum class OppslagFeil {
    FOR_MANGE, UKJENT
}

@Path("/personsok")
@Produces(MediaType.APPLICATION_JSON)
class PersonsokController @Inject constructor(private val personsokPortType: PersonsokPortType, val tilgangskontroll: Tilgangskontroll) {

    private val logger = LoggerFactory.getLogger(PersonsokController::class.java)

    @POST
    fun sok(personsokRequest: PersonsokRequest): Response {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get {
                    try {
                        val response = personsokPortType.finnPerson(lagPersonsokRequest(personsokRequest))
                        if (response.personListe == null) {
                            Response.ok(emptyList<Map<String, Any?>>()).build()
                        } else {
                            val liste = response.personListe.map { lagPersonResponse(it) }
                            Response.ok(liste).build()
                        }
                    } catch (ex: Exception) {
                        when (haandterOppslagFeil(ex)) {
                            OppslagFeil.FOR_MANGE -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Søket gav mer enn 200 treff. Forsøk å begrense søket.").build()
                            else -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Feil fra søketjeneste: " + ex.message).build()
                        }
                    }
                }
    }
}

private fun haandterOppslagFeil(ex: Exception): OppslagFeil =
        when (ex.message) {
            "For mange forekomster funnet" -> OppslagFeil.FOR_MANGE
            else -> OppslagFeil.UKJENT
        }

private fun lagPersonResponse(fimPerson: FimPerson): Map<String, Any?> =
        mapOf(
                "diskresjonskode" to fimPerson.diskresjonskode?.let { lagKodeverdi(it) },
                "postadresse" to fimPerson.postadresse?.ustrukturertAdresse?.let { lagPostadresse(it) },
                "bostedsadresse" to fimPerson.bostedsadresse?.strukturertAdresse?.let { lagBostedsadresse(it) },
                "kjonn" to fimPerson.kjoenn?.kjoenn?.let { lagKodeverdi(it) },
                "navn" to fimPerson.personnavn?.let { lagNavn(it) },
                "status" to fimPerson.personstatus?.personstatus?.let { lagKodeverdi(it) },
                "ident" to fimPerson.ident?.let { lagNorskIdent(it) },
                "brukerinfo" to lagBrukerinfo(fimPerson)
        )

private fun lagPostadresse(adr: FimUstrukturertAdresse): String =
        arrayOf(adr.adresselinje1, adr.adresselinje2, adr.adresselinje3, adr.adresselinje4, adr.landkode?.value).filterNotNull().joinToString(" ")


private fun lagBostedsadresse(adr: FimStrukturertAdresse): String? =
        when (adr) {
            is FimGateadresse -> arrayOf(adr.gatenavn, adr.husnummer, adr.husbokstav, adr.poststed?.value).filterNotNull().joinToString(" ")
            is FimMatrikkeladresse -> arrayOf(adr.matrikkelnummer.bruksnummer, adr.matrikkelnummer.festenummer, adr.matrikkelnummer.gaardsnummer,
                    adr.matrikkelnummer.seksjonsnummer, adr.matrikkelnummer.undernummer, adr.poststed?.value).filterNotNull().joinToString(" ")
            is FimStedsadresseNorge -> arrayOf(adr.tilleggsadresse, adr.bolignummer, adr.poststed?.value).filterNotNull().joinToString(" ")
            is FimPostboksadresseNorsk -> arrayOf(adr.postboksanlegg, adr.poststed?.value).filterNotNull().joinToString(" ")
            else -> null
        }

private fun lagNavn(fimPersonnavn: FimPersonnavn): Map<String, Any?> =
        mapOf(
                "fornavn" to fimPersonnavn.fornavn,
                "etternavn" to fimPersonnavn.etternavn,
                "mellomnavn" to fimPersonnavn.mellomnavn,
                "sammensatt" to fimPersonnavn.sammensattNavn
        )

private fun lagNorskIdent(fimNorskIdent: FimNorskIdent): Map<String, Any?> =
        mapOf(
                "ident" to fimNorskIdent.ident,
                "type" to fimNorskIdent.type?.let { lagKodeverdi(it) }
        )

private fun lagBrukerinfo(fimPerson: FimPerson): Map<String, Any?>? =
        if (fimPerson is FimBruker) {
            mapOf(
                    "gjeldendePostadresseType" to fimPerson.gjeldendePostadresseType?.let { lagKodeverdi(it) },
                    "midlertidigPostadresse" to fimPerson.midlertidigPostadresse?.let { lagMidlertidigAdresse(it) },
                    "ansvarligEnhet" to fimPerson.harAnsvarligEnhet?.enhet?.organisasjonselementID
            )
        } else {
            null
        }


private fun lagMidlertidigAdresse(fimMidlertidigPostadresse: FimMidlertidigPostadresse): String? =
        when (fimMidlertidigPostadresse) {
            is FimMidlertidigPostadresseNorge -> lagPostadresse(fimMidlertidigPostadresse.ustrukturertAdresse)
            is FimMidlertidigPostadresseUtland -> lagPostadresse(fimMidlertidigPostadresse.ustrukturertAdresse)
            else -> null
        }

private fun lagKodeverdi(fimKodeverdi: FimKodeverdi): Map<String, Any?> =
        mapOf(
                "kodeRef" to fimKodeverdi.kodeRef,
                "beskrivelse" to fimKodeverdi.value
        )

private fun lagPersonsokRequest(request: PersonsokRequest): FimFinnPersonRequest =
        FimFinnPersonRequest()
                .withSoekekriterie(FimSoekekriterie()
                        .withFornavn(request.fornavn)
                        .withEtternavn(request.etternavn)
                        .withGatenavn(request.gatenavn)
                        .withBankkontoNorge(request.kontonummer))
                .withPersonFilter(FimPersonFilter()
                        .withAlderFra(request.alderFra)
                        .withAlderTil(request.alderTil)
                        .withEnhetId(request.kommunenummer)
                        .withFoedselsdatoFra(lagXmlGregorianDato(request.fodselsdatoFra))
                        .withFoedselsdatoTil(lagXmlGregorianDato(request.fodselsdatoTil))
                        .withKjoenn(request.kjonn))
                .withAdresseFilter(FimAdresseFilter()
                        .withGatenummer(request.husnummer)
                        .withHusbokstav(request.husbokstav)
                        .withPostnummer(request.postnummer))

data class PersonsokRequest(
        val fornavn: String?,
        val etternavn: String?,
        val gatenavn: String?,
        val kontonummer: String?,
        val alderFra: Int?,
        val alderTil: Int?,
        val kommunenummer: String?,
        val fodselsdatoFra: String?,
        val fodselsdatoTil: String?,
        val kjonn: String?,
        val husnummer: Int?,
        val husbokstav: String?,
        val postnummer: String?
)