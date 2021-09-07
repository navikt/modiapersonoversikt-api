package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.service.dkif.Dkif
import java.time.LocalDate

object Persondata {
    sealed class Result<T>(val system: String) {
        fun <S> map(newSystem: String = system, block: (t: T) -> S): Result<S> {
            return when (this) {
                is Failure<*> -> this as Result<S>
                is Success<T> -> runCatching(newSystem) {
                    block(this.value)
                }
            }
        }

        fun getOrElse(other: T): T {
            return when (this) {
                is Failure<*> -> other
                is Success<T> -> this.value
            }
        }

        fun getOrNull(): T? {
            return when (this) {
                is Failure<*> -> null
                is Success<T> -> this.value
            }
        }

        class Success<T>(name: String, val value: T) : Result<T>(name)
        class Failure<T>(name: String, val exception: Throwable) : Result<T>(name)
    }

    fun <T> runCatching(system: String, block: () -> T): Result<T> {
        return try {
            Result.Success(system, block())
        } catch (e: Throwable) {
            Result.Failure(system, e)
        }
    }

    data class Data(
        val feilendeSystemer: List<String>,
        val person: Person
    )

    data class Person(
        val fnr: String,
        val navn: Navn,
        val kjonn: KodeBeskrivelse<Kjonn>,
        val fodselsdato: LocalDate?,
        val dodsdato: LocalDate?,
        val bostedAdresse: Adresse?,
        val kontaktAdresse: Adresse?,
        val navEnhet: Enhet?,
        val statsborgerskap: List<Statsborgerskap>,
        val adressebeskyttelse: KodeBeskrivelse<AdresseBeskyttelse>,
        val sikkerhetstiltak: List<Sikkerhetstiltak>,
        val erEgenAnsatt: EgenAnsatt,
        val personstatus: KodeBeskrivelse<PersonStatus>,
        val sivilstand: Sivilstand,
        val foreldreansvar: List<Foreldreansvar>,
        val deltBosted: List<DeltBosted>,
        val dodsbo: List<Dodsbo>,
        val fullmakt: List<Fullmakt>,
        val vergemal: List<Verge>,
        val tilrettelagtKommunikasjon: List<TilrettelagtKommunikasjon>,
        val telefonnummer: List<Telefon>,
        val kontaktOgReservasjon: Dkif.DigitalKontaktinformasjon?,
        val bankkonto: Bankkonto
    )

    data class KodeBeskrivelse<T>(
        val kode: T,
        val beskrivelse: String
    )

    data class Navn(
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String
    )

    data class Statsborgerskap(
        val land: KodeBeskrivelse<String>,
        val gyldigFraOgMed: LocalDate?,
        val gyldigTilOgMed: LocalDate?
    )

    data class Sivilstand(
        val type: KodeBeskrivelse<SivilstandType>,
        val gyldigFraOgMed: LocalDate
    )

    data class Sikkerhetstiltak(
        val type: SikkerhetstiltakType,
        val gyldigFraOgMed: LocalDate,
        val gyldigTilOgMed: LocalDate
    )

    data class Adresse(
        val linje1: String,
        val linje2: String? = null,
        val linje3: String? = null
    )

    data class Enhet(
        val id: String,
        val navn: String
    )

    data class Dodsbo(
        val adressat: Adressat,
        val adresselinje1: String,
        val adresselinje2: String?,
        val postnummer: String,
        val poststed: String,
        val landkode: String?,
        val registrert: LocalDate,
        val skifteform: String
    )

    data class Adressat(
        val advokatSomAdressat: AdvokatSomAdressat?,
        val kontaktpersonMedIdNummerSomAdressat: KontaktpersonMedId?,
        val kontaktpersonUtenIdNummerSomAdressat: KontaktpersonUtenId?,
        val organisasjonSomAdressat: OrganisasjonSomAdressat?
    )
    data class AdvokatSomAdressat(
        val kontaktperson: Navn,
        val organisasjonsnavn: String?,
        val organisasjonsnummer: String?
    )
    data class KontaktpersonMedId(
        val idNummer: String,
        val navn: Navn?
    )
    data class KontaktpersonUtenId(
        val foedselsdato: LocalDate?,
        val fodselsdato: LocalDate?,
        val navn: Navn?
    )
    data class OrganisasjonSomAdressat(
        val kontaktperson: Navn?,
        val organisasjonsnavn: String,
        val organisasjonsnummer: String?
    )

    data class Bankkonto(val nr: String)

    data class TilrettelagtKommunikasjon(
        val talesprak: KodeBeskrivelse<String>,
        val tegnsprak: KodeBeskrivelse<String>
    )
    data class Fullmakt(
        val motpartsRolle: String,
        val motpartsPersonident: String,
        val motpartsPersonNavn: String,
        val omraade: List<String>,
        val gyldigFraOgMed: String,
        val gyldigTilOgMed: String
    )

    data class Telefon(
        val retningsnummer: KodeBeskrivelse<String>?,
        val identifikator: String,
        val sistEndret: String,
        val sistEndretAv: String,
        val prioritet: Int = -1
    )

    data class Verge(
        val ident: String?,
        val navn: Navn?,
        val vergesakstype: String?,
        val omfang: String?,
        val embete: String?,
        val gyldighetstidspunkt: String?,
        val opphoerstidspunkt: String?
    )
    data class Foreldreansvar(
        val ansvar: String,
        val ansvarlig: Navn?,
        val ansvarsubject: Navn?
    )
    data class DeltBosted(
        val startdatoForKontrakt: String?,
        val sluttdatoForKontrakt: String?,
        val adresse: DeltBostedAdresse?,
        val ukjentBosted: UkjentBosted?
    )
    data class DeltBostedAdresse(
        val adressenavn: String?,
        val husnummer: String?,
        val husbokstav: String?,
        val bruksenhetsnummer: String?,
        val kommunenummer: String?,
        val postnummer: String?,
        val poststed: String?,
        val bydelsnummer: String?,
        val tilleggsnavn: String?,
        val coAdressenavn: String?
    )
    data class UkjentBosted(
        val bostedskommune: String?
    )

    enum class Kjonn {
        M, K, U
    }

    enum class AdresseBeskyttelse {
        KODE6, KODE6_UTLAND, KODE7, UGRADERT, UKJENT
    }

    enum class EgenAnsatt {
        JA, NEI, UKJENT
    }

    enum class PersonStatus(val tpsKode: String) {
        BOSATT("BOSA"),
        DOD("DØD"),
        OPPHORT("UTPE"),
        INAKTIV("ADNR"),
        MIDLERTIDIG("ADNR"),
        FORSVUNNET("FOSV"),
        UTFLYTTET("UTVA"),
        IKKE_BOSATT("UREG"),
        FODSELSREGISTERT("FØDR")
    }

    enum class SivilstandType(val tpsKode: String) {
        UOPPGITT("NULL"),
        UGIFT("UGIF"),
        GIFT("GIFT"),
        ENKE_ELLER_ENKEMANN("ENKE"),
        SKILT("SKIL"),
        SEPARERT("SEPR"),
        REGISTRERT_PARTNER("REPA"),
        SEPARERT_PARTNER("SEPA"),
        SKILT_PARTNER("SKPA"),
        GJENLEVENDE_PARTNER("GJPA")
    }

    enum class SikkerhetstiltakType(val beskrivelse: String) {
        FYUS("Fysisk utestengelse"),
        TFUS("Telefonisk utestengelse"),
        FTUS("Fysisk/telefonisk utestengelse"),
        DIUS("Digital utestengelse"),
        TOAN("To ansatte i samtale")
    }
}
