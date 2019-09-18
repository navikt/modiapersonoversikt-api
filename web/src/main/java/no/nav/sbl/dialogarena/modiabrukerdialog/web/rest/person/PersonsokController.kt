package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagXmlGregorianDato
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimAdresseFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimPersonFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimSoekekriterie
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/personsok")
@Produces(MediaType.APPLICATION_JSON)
class PersonsokController @Inject constructor(private val personsokPortType: PersonsokPortType) {

    private val logger = LoggerFactory.getLogger(PersonsokController::class.java)

    @POST
    fun sok(personsokRequest: PersonsokRequest): List<Map<String, Any?>> {
        val response = try {
            personsokPortType.finnPerson(lagPersonsokRequest(personsokRequest))
        } catch(em: FinnPersonForMangeForekomster) {
            throw InternalServerErrorException("Søket gav mer enn 200 treff. Forsøk å begrense søket.")
        } catch(ei: FinnPersonUgyldigInput) {
            logger.warn("Ugyldig input mottat fra personsøk, sjekk validator: " + ei.message)
            throw InternalServerErrorException("Ugyldig input mottatt til søketjeneste")
        } catch (ex: Exception) {
            logger.error("Feil i personsøk.", ex)
            throw InternalServerErrorException(ex)
        }

        if (response.personListe == null) {
            return emptyList()
        }

        return response.personListe.map { lagPersonResponse(it) }
    }
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
    when(adr) {
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