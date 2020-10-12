package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.SokPersonUtenlandskID
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.pdl.PdlOppslagService
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

private enum class OppslagFeil {
    FOR_MANGE, UKJENT
}

@RestController
@RequestMapping("/rest/personsok")
class PersonsokController @Autowired constructor(
        private val personsokPortType: PersonsokPortType,
        private val pdlOppslagService: PdlOppslagService,
        val tilgangskontroll: Tilgangskontroll
) {

    private val logger = LoggerFactory.getLogger(PersonsokController::class.java)
    private val auditDescriptor = Audit.describe<List<PersonSokResponsDTO>>(Audit.Action.READ, AuditResources.Personsok.Resultat) { resultat ->
        val fnr = resultat.map { it.ident }.joinToString(", ")
        listOf(
                AuditIdentifier.FNR to fnr
        )
    }

    @PostMapping
    fun sok(@RequestBody personsokRequest: PersonsokRequest): List<PersonSokResponsDTO> {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(auditDescriptor) {
                    try {
                        if (!personsokRequest.utenlandskID.isNullOrBlank()) {
                            pdlOppslagService.sokPersonUtenlandskID(personsokRequest.utenlandskID)
                                    .map(::lagPersonResponse)
                        }
                        val response = personsokPortType.finnPerson(lagPersonsokRequest(personsokRequest))
                        if (response.personListe == null) {
                            emptyList()
                        } else {
                            response.personListe.map { lagPersonResponse(it) }
                        }
                    } catch (ex: Exception) {
                        when (haandterOppslagFeil(ex)) {
                            OppslagFeil.FOR_MANGE -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Søket gav mer enn 200 treff. Forsøk å begrense søket.")
                            else -> throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Feil fra søketjeneste: ", ex)
                        }
                    }
                }
    }
}

fun lagPersonResponse(searchHit: SokPersonUtenlandskID.searchHit): PersonSokResponsDTO {
    val ident = searchHit.person?.folkeregisteridentifikator?.first()
    return PersonSokResponsDTO(
            diskresjonskode = null,
            kjonn = null,
            status = null,
            ident = ident?.let { NorskIdentDTO(it.identifikasjonsnummer, KodeverdiDTO(it.type, null)) },
            navn = hentNavn(searchHit.person),
            postadresse = lagPostadresse(searchHit.person?.kontaktadresse),
            bostedsadresse = lagBostedsadresse(searchHit.person?.bostedsadresse),
            brukerinfo = BrukerinfoDTO(
                    gjeldendePostadresseType = null,
                    midlertidigPostadresse = null,
                    ansvarligEnhet = null
            )
    )
}

private fun lagBostedsadresse(adr: List<SokPersonUtenlandskID.Bostedsadresse>?): String? {
    if (adr.isNullOrEmpty()) {
        return null;
    }
    val adresse = adr.first()
    if (adresse.ukjentBosted != null) {
        return adresse.ukjentBosted!!.bostedskommune
    } else if (adresse.matrikkeladresse != null) {
        return listOfNotNull(
                adresse.matrikkeladresse!!.bruksenhetsnummer,
                adresse.matrikkeladresse!!.tilleggsnavn,
                adresse.matrikkeladresse!!.postnummer,
                adresse.matrikkeladresse!!.kommunenummer
        ).joinToString(" ")
    } else if (adresse.utenlandskAdresse != null) {
        return listOfNotNull(
                adresse.utenlandskAdresse!!.bygningEtasjeLeilighet,
                adresse.utenlandskAdresse!!.adressenavnNummer,
                adresse.utenlandskAdresse!!.regionDistriktOmraade,
                adresse.utenlandskAdresse!!.postboksNummerNavn,
                adresse.utenlandskAdresse!!.postkode,
                adresse.utenlandskAdresse!!.bySted,
                adresse.utenlandskAdresse!!.landkode
        )
                .joinToString(" ")
    } else if (adresse.vegadresse != null) {
        return listOfNotNull(
                adresse.vegadresse!!.adressenavn,
                adresse.vegadresse!!.husnummer,
                adresse.vegadresse!!.husbokstav,
                adresse.vegadresse!!.bruksenhetsnummer,
                adresse.vegadresse!!.postnummer,
                adresse.vegadresse!!.bydelsnummer,
                adresse.vegadresse!!.kommunenummer

        ).joinToString(" ")
    } else  {
        return null
    }
}

fun lagPostadresse(adr: List<SokPersonUtenlandskID.Kontaktadresse>?): String? {
    if (adr.isNullOrEmpty()) {
        return null;
    }
    val adresse = adr.first()
    if (adresse.postadresseIFrittFormat != null) {
        return listOfNotNull(
                adresse.postadresseIFrittFormat!!.adresselinje1,
                adresse.postadresseIFrittFormat!!.adresselinje2,
                adresse.postadresseIFrittFormat!!.adresselinje3,
                adresse.postadresseIFrittFormat!!.postnummer)
                .joinToString(" ")
    } else if (adresse.utenlandskAdresseIFrittFormat != null) {
        return listOfNotNull(
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje1,
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje2,
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje3,
                adresse.utenlandskAdresseIFrittFormat!!.postkode,
                adresse.utenlandskAdresseIFrittFormat!!.byEllerStedsnavn,
                adresse.utenlandskAdresseIFrittFormat!!.landkode)
                .joinToString(" ")
    } else if (adresse.postboksadresse != null) {
        return listOfNotNull(
                adresse.postboksadresse!!.postbokseier,
                adresse.postboksadresse!!.postboks,
                adresse.postboksadresse!!.postnummer
        ).joinToString(" ")
    } else if (adresse.utenlandskAdresse != null) {
        return listOfNotNull(
                adresse.utenlandskAdresse!!.bygningEtasjeLeilighet,
                adresse.utenlandskAdresse!!.adressenavnNummer,
                adresse.utenlandskAdresse!!.regionDistriktOmraade,
                adresse.utenlandskAdresse!!.postboksNummerNavn,
                adresse.utenlandskAdresse!!.postkode,
                adresse.utenlandskAdresse!!.bySted,
                adresse.utenlandskAdresse!!.landkode
        )
                .joinToString(" ")
    } else if (adresse.vegadresse != null) {
        return listOfNotNull(
                adresse.vegadresse!!.adressenavn,
                adresse.vegadresse!!.husnummer,
                adresse.vegadresse!!.husbokstav,
                adresse.vegadresse!!.bruksenhetsnummer,
                adresse.vegadresse!!.postnummer,
                adresse.vegadresse!!.bydelsnummer,
                adresse.vegadresse!!.kommunenummer

        ).joinToString(" ")
    } else {
        return null
    }
}

fun hentNavn(person: SokPersonUtenlandskID.Person?): PersonnavnDTO? {
    return person
            ?.navn
            ?.first()
            ?.let {
                PersonnavnDTO(
                        fornavn = it.fornavn,
                        etternavn = it.etternavn,
                        mellomnavn = it.mellomnavn,
                        sammensatt = listOfNotNull(it.fornavn, it.mellomnavn, it.etternavn).joinToString(" ")
                )
            }
}


private fun haandterOppslagFeil(ex: Exception): OppslagFeil =
        when (ex.message) {
            "For mange forekomster funnet" -> OppslagFeil.FOR_MANGE
            else -> OppslagFeil.UKJENT
        }

data class PersonSokResponsDTO(
        val diskresjonskode: KodeverdiDTO?,
        val postadresse: String?,
        val bostedsadresse: String?,
        val kjonn: KodeverdiDTO?,
        val navn: PersonnavnDTO?,
        val status: KodeverdiDTO?,
        val ident: NorskIdentDTO?,
        val brukerinfo: BrukerinfoDTO?
)

private fun lagPersonResponse(fimPerson: Person) = PersonSokResponsDTO(
        diskresjonskode = fimPerson.diskresjonskode?.let { lagKodeverdi(it) },
        postadresse = fimPerson.postadresse?.ustrukturertAdresse?.let { lagPostadresse(it) },
        bostedsadresse = fimPerson.bostedsadresse?.strukturertAdresse?.let { lagBostedsadresse(it) },
        kjonn = fimPerson.kjoenn?.kjoenn?.let { lagKodeverdi(it) },
        navn = fimPerson.personnavn?.let { lagNavn(it) },
        status = fimPerson.personstatus?.personstatus?.let { lagKodeverdi(it) },
        ident = fimPerson.ident?.let { lagNorskIdent(it) },
        brukerinfo = lagBrukerinfo(fimPerson)
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

data class PersonnavnDTO(
        val fornavn: String,
        val etternavn: String,
        val mellomnavn: String?,
        val sammensatt: String
)

private fun lagNavn(fimPersonnavn: Personnavn) = PersonnavnDTO(
        fornavn = fimPersonnavn.fornavn,
        etternavn = fimPersonnavn.etternavn,
        mellomnavn = fimPersonnavn.mellomnavn,
        sammensatt = fimPersonnavn.sammensattNavn
)

data class NorskIdentDTO(val ident: String, val type: KodeverdiDTO?)

private fun lagNorskIdent(fimNorskIdent: NorskIdent) = NorskIdentDTO(
        fimNorskIdent.ident,
        fimNorskIdent.type?.let { lagKodeverdi(it) }
)

data class BrukerinfoDTO(
        val gjeldendePostadresseType: KodeverdiDTO?,
        val midlertidigPostadresse: String?,
        val ansvarligEnhet: String?
)

private fun lagBrukerinfo(fimPerson: Person): BrukerinfoDTO? =
        if (fimPerson is Bruker) {
            BrukerinfoDTO(
                    gjeldendePostadresseType = fimPerson.gjeldendePostadresseType?.let { lagKodeverdi(it) },
                    midlertidigPostadresse = fimPerson.midlertidigPostadresse?.let { lagMidlertidigAdresse(it) },
                    ansvarligEnhet = fimPerson.harAnsvarligEnhet?.enhet?.organisasjonselementID
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

data class KodeverdiDTO(val kodeRef: String?, val beskrivelse: String?)

private fun lagKodeverdi(fimKodeverdi: Kodeverdi) = KodeverdiDTO(fimKodeverdi.kodeRef, fimKodeverdi.value)

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
        val utenlandskID: String?,
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
