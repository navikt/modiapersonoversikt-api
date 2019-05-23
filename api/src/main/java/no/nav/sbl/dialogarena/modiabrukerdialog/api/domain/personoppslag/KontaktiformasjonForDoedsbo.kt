package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag

data class KontaktiformasjonForDoedsbo (
        val adressat: Adressat,
        val adresselinje1: String,
        val adresselinje2: String?,
        val gyldigFom: String,
        val gyldigTom: String?,
        val kilde: String?,
        val landkode: String?,
        val master: String,
        val opplysningsId: String,
        val postnummer: String,
        val poststedsnavn: String,
        val registrertAv: String?,
        val registrertINAV: String,
        val skifteform: String,
        val systemKilde: String?,
        val utstedtDato: String
)

data class Adressat (
        val advokatSomAdressat: AdvokatSomAdressat?,
        val kontaktpersonMedIdNummerSomAdressat: KontaktpersonMedIdNummerSomAdressat?,
        val kontaktpersonUtenIdNummerSomAdressat: KontaktpersonUtenIdNummerSomAdressat?,
        val organisasjonSomAdressat: OrganisasjonSomAdressat?
)

data class AdvokatSomAdressat (
        val kontaktperson: PersonNavn,
        val organisasjonsnavn: String?,
        val organisasjonsnummer: Long?
)

data class OrganisasjonSomAdressat (
        val kontaktperson: PersonNavn?,
        val organisasjonsnavn: String,
        val organisasjonsnummer: Long?
)

data class KontaktpersonMedIdNummerSomAdressat (
        val idNummer: Long
)

data class KontaktpersonUtenIdNummerSomAdressat (
        val foedselsdato: String?,
        val navn: PersonNavn
)