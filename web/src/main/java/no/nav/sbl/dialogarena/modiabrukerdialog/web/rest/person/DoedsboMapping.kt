package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentPerson

class DoedsboMapping constructor(private val doedsbo: List<HentPerson.KontaktinformasjonForDoedsbo>) {


    fun mapKontaktinfoForDoedsbo(): List<Map<String, Any?>> =
            doedsbo.map {
                mapOf(
                        "adressat" to hentAdressat(it),
                        "adresselinje1" to it.adresse.adresselinje1,
                        "adresselinje2" to it.adresse.adresselinje2,
                        "postnummer" to it.adresse.postnummer,
                        "poststed" to it.adresse.poststedsnavn,
                        "landkode" to it.adresse.landkode,
                        "registrert" to formatDate(it.attestutstedelsesdato.value),
                        "skifteform" to it.skifteform
                )
            }

    private fun hentAdressat(doedsbo: HentPerson.KontaktinformasjonForDoedsbo): Map<String, Any?> =
            mapOf(
                    "advokatSomAdressat" to doedsbo.advokatSomKontakt?.let { hentAdvokatSomAdressat(it) },
                    "organisasjonSomAdressat" to doedsbo.organisasjonSomKontakt?.let { hentOrganisasjonSomAdressat(it) },
                    *kontaktperson(doedsbo.personSomKontakt)
            )

    private fun kontaktperson(person: HentPerson.KontaktinformasjonForDoedsboPersonSomKontakt?): Array<Pair<String, Map<String, Any?>>> {
        if (person == null) {
            return arrayOf()
        }
        return if (person.identifikasjonsnummer == null) {
            arrayOf(Pair("kontaktpersonUtenIdNummerSomAdressat", mapOf(
                    "foedselsdato" to person.foedselsdato?.let { formatDate(it.value) },
                    "navn" to personNavn(person?.personnavn)
            )))
        } else {
            arrayOf(Pair("kontaktpersonMedIdNummerSomAdressat", mapOf(
                    "idNummer" to person.identifikasjonsnummer,
                    "navn" to personNavn(person?.personnavn) // TODO person.personnavn vil aldri eksistere her sånn PDL er satt opp idag.
            )))
        }
    }


    private fun hentAdvokatSomAdressat(adressat: HentPerson.KontaktinformasjonForDoedsboAdvokatSomKontakt): Map<String, Any?> =
            mapOf(
                    "kontaktperson" to personNavn(adressat.personnavn),
                    "organisasjonsnavn" to adressat?.organisasjonsnavn,
                    "organisasjonsnummer" to adressat?.organisasjonsnummer
            )

    private fun hentOrganisasjonSomAdressat(adressat: HentPerson.KontaktinformasjonForDoedsboOrganisasjonSomKontakt): Map<String, Any?> =
            mapOf(
                    "kontaktperson" to adressat.kontaktperson?.let { personNavn(it) },
                    "organisasjonsnavn" to adressat.organisasjonsnavn,
                    "organisasjonsnummer" to adressat.organisasjonsnummer
            )

    private fun personNavn(personNavn: HentPerson.Personnavn2?): Map<String, Any?> {
        return personNavn(HentPerson.Personnavn(
                fornavn = personNavn?.fornavn ?: "",
                mellomnavn = personNavn?.mellomnavn,
                etternavn = personNavn?.etternavn ?: ""
        ))
    }

    private fun personNavn(personNavn: HentPerson.Personnavn?): Map<String, Any?> {
        val sammensatNavn = "${personNavn?.fornavn.textOrEmpty()} ${personNavn?.mellomnavn.textOrEmpty()} ${personNavn?.etternavn.textOrEmpty()}"
        return mapOf("fornavn" to personNavn?.fornavn,
                "etternavn" to personNavn?.etternavn,
                "mellomnavn" to personNavn?.mellomnavn,
                "sammensatt" to sammensatNavn)
    }

    private fun String?.textOrEmpty(): String = this ?: ""
}
