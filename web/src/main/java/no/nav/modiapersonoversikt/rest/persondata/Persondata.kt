package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.service.dkif.Dkif
import java.time.LocalDate
import java.time.LocalDateTime

object Persondata {
    data class Data(
        val feilendeSystemer: List<String>,
        val person: Person
    )

    data class Person(
        val fnr: String,
        val navn: List<Navn>,
        val kjonn: List<KodeBeskrivelse<Kjonn>>,
        val fodselsdato: List<LocalDate>,
        val alder: Int?,
        val dodsdato: List<LocalDate>,
        val bostedAdresse: List<Adresse>,
        val kontaktAdresse: List<Adresse>,
        val oppholdsAdresse: List<Adresse>,
        val navEnhet: Enhet?,
        val statsborgerskap: List<Statsborgerskap>,
        val adressebeskyttelse: List<KodeBeskrivelse<AdresseBeskyttelse>>,
        val sikkerhetstiltak: List<Sikkerhetstiltak>,
        val erEgenAnsatt: EgenAnsatt,
        val personstatus: List<KodeBeskrivelse<PersonStatus>>,
        val sivilstand: List<Sivilstand>,
        val foreldreansvar: List<Foreldreansvar>,
        val deltBosted: List<DeltBosted>,
        val dodsbo: List<Dodsbo>,
        val fullmakt: List<Fullmakt>,
        val vergemal: List<Verge>,
        val tilrettelagtKommunikasjon: TilrettelagtKommunikasjon,
        val telefonnummer: List<Telefon>,
        val kontaktOgReservasjon: Dkif.DigitalKontaktinformasjon?,
        val bankkonto: Bankkonto?,
        val forelderBarnRelasjon: List<ForelderBarnRelasjon>
    )

    data class TredjepartsPerson(
        val fnr: String,
        val navn: List<Navn>,
        val fodselsdato: List<LocalDate>,
        val alder: Int?,
        val kjonn: List<KodeBeskrivelse<Kjonn>>,
        val adressebeskyttelse: List<KodeBeskrivelse<AdresseBeskyttelse>>,
        val bostedAdresse: List<Adresse>,
        val personstatus: List<KodeBeskrivelse<PersonStatus>>
    )

    data class KodeBeskrivelse<T>(
        val kode: T,
        val beskrivelse: String
    )

    data class Navn(
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String
    ) {
        companion object {
            val UKJENT = Navn("", "", "")
        }
    }

    data class Statsborgerskap(
        val land: KodeBeskrivelse<String>,
        val gyldigFraOgMed: LocalDate?,
        val gyldigTilOgMed: LocalDate?
    )

    data class Sivilstand(
        val type: KodeBeskrivelse<SivilstandType>,
        val gyldigFraOgMed: LocalDate?,
        val sivilstandRelasjon: SivilstandRelasjon?
    )

    data class SivilstandRelasjon(
        val fnr: String,
        val navn: List<Navn>,
        val alder: Int?,
        val adressebeskyttelse: List<KodeBeskrivelse<AdresseBeskyttelse>>,
        val harSammeAdresse: Boolean
    )

    data class Sikkerhetstiltak(
        val type: String,
        val beskrivelse: String,
        val gyldigFraOgMed: LocalDate,
        val gyldigTilOgMed: LocalDate
    )

    data class SistEndret(
        val ident: String,
        val tidspunkt: LocalDateTime,
        val system: String
    )

    data class GyldighetsPeriode(
        val gyldigFraOgMed: LocalDate?,
        val gyldigTilOgMed: LocalDate?
    )

    data class Adresse constructor(
        val linje1: String,
        val linje2: String? = null,
        val linje3: String? = null,
        val sistEndret: SistEndret?,
        val gyldighetsPeriode: GyldighetsPeriode? = null
    ) {
        constructor(
            linje1: List<String?>,
            linje2: List<String?>? = null,
            linje3: List<String?>? = null,
            sistEndret: SistEndret?,
            gyldighetsPeriode: GyldighetsPeriode? = null
        ) : this(
            linje1.filterNotNull().joinToString(" "),
            linje2?.filterNotNull()?.joinToString(" "),
            linje3?.filterNotNull()?.joinToString(" "),
            sistEndret,
            gyldighetsPeriode
        )
    }

    data class Apningstid(
        val ukedag: String,
        val apningstid: String
    )

    data class Publikumsmottak(
        val besoksadresse: Adresse,
        val apningstider: List<Apningstid>
    )

    data class Enhet(
        val id: String,
        val navn: String,
        val publikumsmottak: List<Publikumsmottak>
    )

    data class Dodsbo(
        val adressat: Adressat,
        val adresse: Adresse,
        val registrert: LocalDate,
        val skifteform: Skifteform,
        val sistEndret: SistEndret?
    )

    data class Adressat(
        val advokatSomAdressat: AdvokatSomAdressat?,
        val personSomAdressat: PersonSomAdressat?,
        val organisasjonSomAdressat: OrganisasjonSomAdressat?
    )

    data class AdvokatSomAdressat(
        val kontaktperson: Navn,
        val organisasjonsnavn: String?,
        val organisasjonsnummer: String?
    )

    data class PersonSomAdressat(
        val fnr: String?,
        val navn: List<Navn>,
        val fodselsdato: LocalDate?
    )

    data class OrganisasjonSomAdressat(
        val kontaktperson: Navn?,
        val organisasjonsnavn: String,
        val organisasjonsnummer: String?
    )

    data class Bankkonto(
        val kontonummer: String,
        val banknavn: String?,
        val sistEndret: SistEndret?,
        val bankkode: String? = null,
        val swift: String? = null,
        val landkode: KodeBeskrivelse<String>? = null,
        val adresse: Adresse? = null,
        val valuta: KodeBeskrivelse<String>? = null
    )

    data class TilrettelagtKommunikasjon(
        val talesprak: List<KodeBeskrivelse<String>>,
        val tegnsprak: List<KodeBeskrivelse<String>>
    )

    data class Fullmakt(
        val motpartsPersonident: String,
        val motpartsPersonNavn: Navn,
        val motpartsRolle: FullmaktsRolle,
        val omrade: List<KodeBeskrivelse<String>>,
        val gyldigFraOgMed: LocalDate,
        val gyldigTilOgMed: LocalDate
    )

    data class Telefon(
        val retningsnummer: KodeBeskrivelse<String>?,
        val identifikator: String,
        val sistEndret: SistEndret?,
        val prioritet: Int = -1
    )

    data class Verge(
        val ident: String?,
        val navn: Navn?,
        val vergesakstype: String,
        val omfang: String,
        val embete: String?,
        val gyldighetstidspunkt: LocalDate?,
        val opphorstidspunkt: LocalDate?
    )

    data class Foreldreansvar(
        val ansvar: String,
        val ansvarlig: Navn?,
        val ansvarsubject: Navn?
    )

    data class DeltBosted(
        val startdatoForKontrakt: LocalDate,
        val sluttdatoForKontrakt: LocalDate?,
        val adresse: Adresse?
    )

    data class ForelderBarnRelasjon(
        val ident: String,
        val rolle: ForelderBarnRelasjonRolle,
        val navn: List<Navn>,
        val fodselsdato: List<LocalDate>,
        val kjonn: List<KodeBeskrivelse<Kjonn>>,
        val alder: Int?,
        val adressebeskyttelse: List<KodeBeskrivelse<AdresseBeskyttelse>>,
        val harSammeAdresse: Boolean,
        val personstatus: List<KodeBeskrivelse<PersonStatus>>
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
        FODSELSREGISTERT("FØDR"),
        UKJENT("UKJENT")
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

    enum class Skifteform {
        OFFENTLIG, ANNET, UKJENT
    }

    enum class FullmaktsRolle {
        FULLMAKTSGIVER,
        FULLMEKTIG,
        UKJENT
    }

    enum class ForelderBarnRelasjonRolle {
        BARN,
        MOR,
        FAR,
        MEDMOR,
        UKJENT
    }
}
