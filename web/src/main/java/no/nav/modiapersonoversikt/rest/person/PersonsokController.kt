package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.consumer.pdl.generated.sokperson.*
import no.nav.modiapersonoversikt.infrastructure.http.GraphQLException
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.PdlFelt
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.PdlKriterie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.LocalDate

@RestController
@RequestMapping("/rest/personsok")
class PersonsokController
    @Autowired
    constructor(
        private val pdlOppslagService: PdlOppslagService,
        val tilgangskontroll: Tilgangskontroll,
    ) {
        private val sokefelterTrace =
            Audit.describe<Pair<String, String>>(Audit.Action.READ, AuditResources.Personsok.Sokefelter) { data ->
                val (enhet, felter) = requireNotNull(data)
                listOf(
                    AuditIdentifier.ENHET_ID to enhet,
                    AuditIdentifier.SOKEFELTER to felter,
                )
            }
        private val auditDescriptor =
            Audit.describe<List<PersonSokResponsDTO>>(Audit.Action.READ, AuditResources.Personsok.Resultat) { resultat ->
                val fnr = resultat?.joinToString(", ") { it.ident.ident } ?: "--"
                listOf(
                    AuditIdentifier.FNR to fnr,
                )
            }

        @PostMapping("/v3")
        fun sokPdlV3(
            @RequestBody personsokRequestV3: PersonsokRequestV3,
        ): List<PersonSokResponsDTO> =
            tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(auditDescriptor) {
                    handterFeil {
                        val enhet = personsokRequestV3.enhet ?: "Ukjent"
                        val pdlKriterier = personsokRequestV3.tilPdlKriterier()
                        val feltnavn =
                            pdlKriterier
                                .filter { it.value.isNullOrEmpty().not() }
                                .joinToString(", ") { it.felt.name }
                        sokefelterTrace.log(enhet to feltnavn)
                        pdlOppslagService
                            .sokPerson(pdlKriterier)
                            .mapNotNull(::lagPersonResponse)
                    }
                }

        private fun <T> handterFeil(block: () -> T): T =
            try {
                block()
            } catch (ex: Exception) {
                when {
                    ex.message == "For mange forekomster funnet" ->
                        throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Søket gav mer enn 200 treff. Forsøk å begrense søket.",
                        )
                    ex is GraphQLException ->
                        throw ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Søket gav feil ved kall til PDL: ${ex.message}",
                            ex,
                        )
                    else ->
                        throw ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Feil fra søketjeneste: ${ex.message}",
                            ex,
                        )
                }
            }
    }

fun lagPersonResponse(searchHit: PersonSearchHit): PersonSokResponsDTO? {
    val ident: Folkeregisteridentifikator = searchHit.person?.folkeregisteridentifikator?.firstOrNull() ?: return null
    val navn: PersonnavnDTO = hentNavn(searchHit.person) ?: return null
    val utenlandskID = searchHit.person?.utenlandskIdentifikasjonsnummer
    return PersonSokResponsDTO(
        diskresjonskode = null,
        kjonn = null,
        status = null,
        ident = NorskIdentDTO(ident.identifikasjonsnummer, KodeverdiDTO(ident.type, null)),
        navn = navn,
        postadresse = lagPostadresse(searchHit.person?.kontaktadresse),
        bostedsadresse = lagBostedsadresse(searchHit.person?.bostedsadresse),
        brukerinfo =
            BrukerinfoDTO(
                gjeldendePostadresseType = null,
                midlertidigPostadresse = null,
                ansvarligEnhet = null,
            ),
        utenlandskID = utenlandskID?.map { UtenlandskIdDTO(it.identifikasjonsnummer, it.utstederland) },
    )
}

private fun lagBostedsadresse(adr: List<Bostedsadresse>?): String? {
    if (adr.isNullOrEmpty()) {
        return null
    }
    val adresse = adr.first()
    when {
        adresse.ukjentBosted != null -> {
            return adresse.ukjentBosted!!.bostedskommune
        }
        adresse.matrikkeladresse != null -> {
            return listOfNotNull(
                adresse.matrikkeladresse!!.bruksenhetsnummer,
                adresse.matrikkeladresse!!.tilleggsnavn,
                adresse.matrikkeladresse!!.postnummer,
                adresse.matrikkeladresse!!.kommunenummer,
            ).joinToString(" ")
        }
        adresse.utenlandskAdresse != null -> {
            return listOfNotNull(
                adresse.utenlandskAdresse!!.bygningEtasjeLeilighet,
                adresse.utenlandskAdresse!!.adressenavnNummer,
                adresse.utenlandskAdresse!!.regionDistriktOmraade,
                adresse.utenlandskAdresse!!.postboksNummerNavn,
                adresse.utenlandskAdresse!!.postkode,
                adresse.utenlandskAdresse!!.bySted,
                adresse.utenlandskAdresse!!.landkode,
            ).joinToString(" ")
        }
        adresse.vegadresse != null -> {
            return listOfNotNull(
                adresse.vegadresse!!.adressenavn,
                adresse.vegadresse!!.husnummer,
                adresse.vegadresse!!.husbokstav,
                adresse.vegadresse!!.bruksenhetsnummer,
                adresse.vegadresse!!.postnummer,
                adresse.vegadresse!!.bydelsnummer,
                adresse.vegadresse!!.kommunenummer,
            ).joinToString(" ")
        }
        else -> {
            return null
        }
    }
}

fun lagPostadresse(adr: List<Kontaktadresse>?): String? {
    if (adr.isNullOrEmpty()) {
        return null
    }
    val adresse = adr.first()
    when {
        adresse.postadresseIFrittFormat != null -> {
            return listOfNotNull(
                adresse.postadresseIFrittFormat!!.adresselinje1,
                adresse.postadresseIFrittFormat!!.adresselinje2,
                adresse.postadresseIFrittFormat!!.adresselinje3,
                adresse.postadresseIFrittFormat!!.postnummer,
            ).joinToString(" ")
        }
        adresse.utenlandskAdresseIFrittFormat != null -> {
            return listOfNotNull(
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje1,
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje2,
                adresse.utenlandskAdresseIFrittFormat!!.adresselinje3,
                adresse.utenlandskAdresseIFrittFormat!!.postkode,
                adresse.utenlandskAdresseIFrittFormat!!.byEllerStedsnavn,
                adresse.utenlandskAdresseIFrittFormat!!.landkode,
            ).joinToString(" ")
        }
        adresse.postboksadresse != null -> {
            return listOfNotNull(
                adresse.postboksadresse!!.postbokseier,
                adresse.postboksadresse!!.postboks,
                adresse.postboksadresse!!.postnummer,
            ).joinToString(" ")
        }
        adresse.utenlandskAdresse != null -> {
            return listOfNotNull(
                adresse.utenlandskAdresse!!.bygningEtasjeLeilighet,
                adresse.utenlandskAdresse!!.adressenavnNummer,
                adresse.utenlandskAdresse!!.regionDistriktOmraade,
                adresse.utenlandskAdresse!!.postboksNummerNavn,
                adresse.utenlandskAdresse!!.postkode,
                adresse.utenlandskAdresse!!.bySted,
                adresse.utenlandskAdresse!!.landkode,
            ).joinToString(" ")
        }
        adresse.vegadresse != null -> {
            return listOfNotNull(
                adresse.vegadresse!!.adressenavn,
                adresse.vegadresse!!.husnummer,
                adresse.vegadresse!!.husbokstav,
                adresse.vegadresse!!.bruksenhetsnummer,
                adresse.vegadresse!!.postnummer,
                adresse.vegadresse!!.bydelsnummer,
                adresse.vegadresse!!.kommunenummer,
            ).joinToString(" ")
        }
        else -> {
            return null
        }
    }
}

fun hentNavn(person: Person?): PersonnavnDTO? =
    person
        ?.navn
        ?.first()
        ?.let {
            PersonnavnDTO(
                fornavn = it.fornavn,
                etternavn = it.etternavn,
                mellomnavn = it.mellomnavn,
                sammensatt = listOfNotNull(it.fornavn, it.mellomnavn, it.etternavn).joinToString(" "),
            )
        }

data class PersonSokResponsDTO(
    val ident: NorskIdentDTO,
    val navn: PersonnavnDTO,
    val diskresjonskode: KodeverdiDTO?,
    val postadresse: String?,
    val bostedsadresse: String?,
    val kjonn: KodeverdiDTO?,
    val status: KodeverdiDTO?,
    val brukerinfo: BrukerinfoDTO?,
    val utenlandskID: List<UtenlandskIdDTO>?,
)

data class PersonnavnDTO(
    val fornavn: String,
    val etternavn: String,
    val mellomnavn: String?,
    val sammensatt: String,
)

data class UtenlandskIdDTO(
    val identifikasjonsnummer: String,
    val utstederland: String,
)

data class NorskIdentDTO(
    val ident: String,
    val type: KodeverdiDTO?,
)

data class BrukerinfoDTO(
    val gjeldendePostadresseType: KodeverdiDTO?,
    val midlertidigPostadresse: String?,
    val ansvarligEnhet: String?,
)

data class KodeverdiDTO(
    val kodeRef: String?,
    val beskrivelse: String?,
)

data class PersonsokRequestV3(
    val enhet: String?,
    val navn: String?,
    val utenlandskID: String?,
    val alderFra: Int?,
    val alderTil: Int?,
    val fodselsdatoFra: String?,
    val fodselsdatoTil: String?,
    val kjonn: String?,
    val adresse: String?,
    val telefonnummer: String?,
)

fun PersonsokRequestV3.tilPdlKriterier(clock: Clock = Clock.systemDefaultZone()): List<PdlKriterie> {
    val fodselsdatoFra = this.fodselsdatoFra ?: this.alderTil?.let { finnSenesteDatoGittAlder(it, clock) }
    val fodselsdatoTil = this.fodselsdatoTil ?: this.alderFra?.let { finnTidligsteDatoGittAlder(it, clock) }
    val kjonn =
        when (this.kjonn) {
            "M" -> "MANN"
            "K" -> "KVINNE"
            else -> null
        }

    return listOf(
        PdlKriterie(PdlFelt.NAVN, this.navn, searchHistorical = PdlOppslagService.PdlSokeOmfang.HISTORISK_OG_GJELDENDE),
        PdlKriterie(PdlFelt.ADRESSE, this.adresse, searchHistorical = PdlOppslagService.PdlSokeOmfang.GJELDENDE),
        PdlKriterie(PdlFelt.TELEFON_NUMMER, this.telefonnummer, searchHistorical = PdlOppslagService.PdlSokeOmfang.GJELDENDE),
        PdlKriterie(PdlFelt.UTENLANDSK_ID, this.utenlandskID, searchHistorical = PdlOppslagService.PdlSokeOmfang.HISTORISK_OG_GJELDENDE),
        PdlKriterie(PdlFelt.FODSELSDATO_FRA, fodselsdatoFra, searchHistorical = PdlOppslagService.PdlSokeOmfang.GJELDENDE),
        PdlKriterie(PdlFelt.FODSELSDATO_TIL, fodselsdatoTil, searchHistorical = PdlOppslagService.PdlSokeOmfang.GJELDENDE),
        PdlKriterie(PdlFelt.KJONN, kjonn, searchHistorical = PdlOppslagService.PdlSokeOmfang.GJELDENDE),
    )
}

private fun finnSenesteDatoGittAlder(
    alderTil: Int,
    clock: Clock,
): String =
    LocalDate
        .now(clock)
        .minusYears(alderTil.toLong() + 1)
        .plusDays(1)
        .toString()

private fun finnTidligsteDatoGittAlder(
    alderFra: Int,
    clock: Clock,
): String = LocalDate.now(clock).minusYears(alderFra.toLong()).toString()
