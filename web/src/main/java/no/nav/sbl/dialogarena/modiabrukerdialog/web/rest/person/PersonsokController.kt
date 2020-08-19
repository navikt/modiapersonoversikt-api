package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagXmlGregorianDato
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.AdresseFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FinnPersonRequest
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.PersonFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.Soekekriterie
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

private enum class OppslagFeil {
    FOR_MANGE, UKJENT
}

@RestController
@RequestMapping("/personsok")
class PersonsokController @Autowired constructor(private val personsokPortType: PersonsokPortType, val tilgangskontroll: Tilgangskontroll) {

    private val logger = LoggerFactory.getLogger(PersonsokController::class.java)
    private val auditDescriptor = Audit.describe<List<Map<String, Any?>>>(Audit.Action.READ, AuditResources.Personsok.Resultat) { resultat ->
        val fnr = resultat.map { it["ident"] }.joinToString(", ")
        listOf(
                AuditIdentifier.FNR to fnr
        )
    }

    @PostMapping
    fun sok(personsokRequest: PersonsokRequest): List<Map<String, Any?>> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(auditDescriptor) {
                    try {
                        val response = personsokPortType.finnPerson(lagPersonsokRequest(personsokRequest))
                        if (response.personListe == null) {
                            emptyList()
                        } else {
                            response.personListe.map { lagPersonResponse(it) }
                        }
                    } catch (ex: Exception) {
                        when (haandterOppslagFeil(ex)) {
                            OppslagFeil.FOR_MANGE -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Søket gav mer enn 200 treff. Forsøk å begrense søket.")
                            else -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Feil fra søketjeneste: ",ex)
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

private fun lagPersonResponse(fimPerson: Person): Map<String, Any?> =
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

private fun lagPostadresse(adr: UstrukturertAdresse): String =
        arrayOf(adr.adresselinje1, adr.adresselinje2, adr.adresselinje3, adr.adresselinje4, adr.landkode?.value).filterNotNull().joinToString(" ")


private fun lagBostedsadresse(adr: StrukturertAdresse): String? =
        when (adr) {
            is Gateadresse -> arrayOf(adr.gatenavn, adr.husnummer, adr.husbokstav, adr.poststed?.value).filterNotNull().joinToString(" ")
            is Matrikkeladresse -> arrayOf(adr.matrikkelnummer.bruksnummer, adr.matrikkelnummer.festenummer, adr.matrikkelnummer.gaardsnummer,
                    adr.matrikkelnummer.seksjonsnummer, adr.matrikkelnummer.undernummer, adr.poststed?.value).filterNotNull().joinToString(" ")
            is StedsadresseNorge -> arrayOf(adr.tilleggsadresse, adr.bolignummer, adr.poststed?.value).filterNotNull().joinToString(" ")
            is PostboksadresseNorsk -> arrayOf(adr.postboksanlegg, adr.poststed?.value).filterNotNull().joinToString(" ")
            else -> null
        }

private fun lagNavn(fimPersonnavn: Personnavn): Map<String, Any?> =
        mapOf(
                "fornavn" to fimPersonnavn.fornavn,
                "etternavn" to fimPersonnavn.etternavn,
                "mellomnavn" to fimPersonnavn.mellomnavn,
                "sammensatt" to fimPersonnavn.sammensattNavn
        )

private fun lagNorskIdent(fimNorskIdent: NorskIdent): Map<String, Any?> =
        mapOf(
                "ident" to fimNorskIdent.ident,
                "type" to fimNorskIdent.type?.let { lagKodeverdi(it) }
        )

private fun lagBrukerinfo(fimPerson: Person): Map<String, Any?>? =
        if (fimPerson is Bruker) {
            mapOf(
                    "gjeldendePostadresseType" to fimPerson.gjeldendePostadresseType?.let { lagKodeverdi(it) },
                    "midlertidigPostadresse" to fimPerson.midlertidigPostadresse?.let { lagMidlertidigAdresse(it) },
                    "ansvarligEnhet" to fimPerson.harAnsvarligEnhet?.enhet?.organisasjonselementID
            )
        } else {
            null
        }


private fun lagMidlertidigAdresse(fimMidlertidigPostadresse: MidlertidigPostadresse): String? =
        when (fimMidlertidigPostadresse) {
            is MidlertidigPostadresseNorge -> lagPostadresse(fimMidlertidigPostadresse.ustrukturertAdresse)
            is MidlertidigPostadresseUtland -> lagPostadresse(fimMidlertidigPostadresse.ustrukturertAdresse)
            else -> null
        }

private fun lagKodeverdi(fimKodeverdi: Kodeverdi): Map<String, Any?> =
        mapOf(
                "kodeRef" to fimKodeverdi.kodeRef,
                "beskrivelse" to fimKodeverdi.value
        )

private fun lagPersonsokRequest(request: PersonsokRequest): FinnPersonRequest =
        FinnPersonRequest()
                .apply {
                    soekekriterie = Soekekriterie()
                            .apply {
                                fornavn = request.fornavn
                                etternavn = request.etternavn
                                gatenavn = request.gatenavn
                                bankkontoNorge = request.kontonummer
                            }
                    personFilter = PersonFilter()
                            .apply {
                                alderFra = request.alderFra
                                alderTil = request.alderTil
                                enhetId = request.kommunenummer
                                foedselsdatoFra = lagXmlGregorianDato(request.fodselsdatoFra)
                                foedselsdatoTil = lagXmlGregorianDato(request.fodselsdatoTil)
                                kjoenn = request.kjonn
                            }
                    adresseFilter = AdresseFilter()
                            .apply {
                                gatenummer = request.husnummer
                                husbokstav = request.husbokstav
                                postnummer = request.postnummer
                            }
                }


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
