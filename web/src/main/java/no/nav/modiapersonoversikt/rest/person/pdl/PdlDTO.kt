package no.nav.modiapersonoversikt.rest.person.pdl

import java.time.LocalDate

object PdlDTO {
    data class Person(
        val navn: Navn,
        val kjønn: Kjonn,
        val kjonn: Kjonn,
        val geografiskTilknytning: String?,
        val fødselsnummer: String,
        val fodselsnummer: String,
        val fodselsdato: LocalDate?,
        val alder: Int,
        val diskresjonskode: Kodeverk?,
        val bankkonto: Nothing = TODO("Bankkonto videreføres ikke - https://navikt.github.io/pdl/#_opplysninger_som_ikke_videref%C3%B8res_i_pdl"),
        val tilrettelagtKomunikasjonsListe: List<TilrettelagtKommunikasjonsbehov>,
        val statsborgerskap: Kodeverk?,
        val folkeregistrertAdresse: Personadresse?,
        val alternativAdresse: Personadresse?,
        val postadresse: Personadresse?,
        val personstatus: Bostatus,
        val sivilstand: Sivilstand,
        val familierelasjoner: List<Familierelasjon>,
        val kontaktinformasjon: NavKontaktinformasjon,
        val kontaktinformasjonForDoedsbo: List<Dodsbo>,
        val fullmakt: List<Fullmakt>,
        val telefonnummer: List<Telefon>,
        val vergemal: List<Verge>,
        val foreldreansvar: List<Foreldreansvar>,
        val deltBosted: List<DeltBosted>,
        val sikkerhetstiltak: Sikkerhetstiltak
    )

    data class Navn(
        val endringsinfo: Endringsinfo?,
        val fornavn: String?,
        val mellomnavn: String?,
        val etternavn: String?
    ) {
        val sammensatt: String = listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(" ")
    }

    enum class Kjonn {
        M, K, D;
    }

    open class Kodeverk(
        val kodeRef: String,
        val beskrivelse: String
    )

    enum class TilrettelagtKommunikasjonsbehovType { TEGNSPRAK, TALESPRAK, UKJENT }
    class TilrettelagtKommunikasjonsbehov(
        kodeRef: String,
        beskrivelse: String,
        val type: TilrettelagtKommunikasjonsbehovType
    ) : Kodeverk(kodeRef, beskrivelse)

    data class Personadresse(
        val endringsinfo: Endringsinfo?,
        val gateadresse: Gateadresse?,
        val postboksadresse: Postboksadresse?,
        val matrikkeladresse: Matrikkeladresse?,
        val utlandsadresse: Utlandsadresse?,
        val ustrukturert: UstrukturertAdresse?
    ) {
        constructor(endringsinfo: Endringsinfo?) : this(endringsinfo, null, null, null, null, null)
    }

    data class Gateadresse(
        val tilleggsadresse: String?,
        val gatenavn: String,
        val husnummer: String,
        val husbokstav: String?,
        val postnummer: String,
        val poststed: String,
        val bolignummer: String?,
        val periode: Periode?
    )

    data class Postboksadresse(
        val postboksnummer: String,
        val postnummer: String,
        val poststed: String,
        val tilleggsadresse: String?,
        val postboksanlegg: String?,
        val periode: Periode?
    )

    data class Matrikkeladresse(
        val tilleggsadresse: String?,
        val eiendomsnavn: String?,
        val postnummer: String,
        val poststed: String,
        val periode: Periode?
    )

    data class Utlandsadresse(
        val landkode: Kodeverk?,
        val adresselinjer: List<String>,
        val periode: Periode?
    )

    data class UstrukturertAdresse(
        val adresselinje: String
    )

    data class Bostatus(
        val dødsdato: LocalDate?, // TODO Denne bør slettes når frontend er migrert
        val dodsdato: LocalDate?,
        val bostatus: Kodeverk?
    )

    class Sivilstand(
        kodeRef: String,
        beskrivelse: String,
        val fraOgMed: LocalDate
    ) : Kodeverk(kodeRef, beskrivelse)

    enum class Relasjonstype {
        BARN, SAMBOER, PARTNER, EKTE, GIFT, MORA, FARA
    }

    data class Familierelasjon(
        val harSammeBosted: Boolean?,
        val rolle: Relasjonstype,
        val tilPerson: Person
    ) {
        data class Person(
            val navn: Navn?,
            val alder: Int?,
            val alderMåneder: Int?,
            val alderManeder: Int?,
            val fødselsnummer: String?,
            val fodselsnummer: String?,
            val personstatus: Bostatus,
            val diskresjonskode: Kodeverk?
        )
    }

    data class NavKontaktinformasjon(
        val mobil: Telefon?,
        val jobbTelefon: Telefon?,
        val hjemTelefon: Telefon?
    )

    data class Telefon(
        val retningsnummer: Kodeverk?,
        val identifikator: String,
        val sistEndret: String,
        val sistEndretAv: String,
        val prioritet: Int = -1
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
        val foedselsdato: String?,
        val fodselsdato: String?,
        val navn: Navn
    )
    data class OrganisasjonSomAdressat(
        val kontaktperson: Navn?,
        val organisasjonsnavn: String,
        val organisasjonsnummer: String?
    )

    data class Fullmakt(
        val motpartsRolle: String,
        val motpartsPersonident: String,
        val motpartsPersonNavn: String,
        val omraade: List<String>,
        val gyldigFraOgMed: String,
        val gyldigTilOgMed: String
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
    data class Sikkerhetstiltak(
        val sikkerhetstiltaksbeskrivelse: String,
        val sikkerhetstiltakskode: String,
        val periode: Periode?
    )

    data class Endringsinfo(
        val sistEndretAv: String,
        val sistEndret: String
    )
    data class Periode(
        val fra: String,
        val til: String
    )
}
