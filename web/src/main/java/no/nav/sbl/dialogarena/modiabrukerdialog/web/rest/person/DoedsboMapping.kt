package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.kjerneinfo.domain.person.Personnavn
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag.*

class DoedsboMapping constructor(private val kjerneinfoService: PersonKjerneinfoServiceBi,
                                 private val doedsbo: List<KontaktiformasjonForDoedsbo>) {


    fun mapKontaktinfoForDoedsbo(): List<Map<String, Any?>> =
            doedsbo.map {
                mapOf(
                        "adressat" to hentAdressat(it.adressat),
                        "adresselinje1" to it.adresselinje1,
                        "adresselinje2" to it.adresselinje2,
                        "postnummer" to it.postnummer,
                        "poststed" to it.poststedsnavn,
                        "landkode" to it.landkode,
                        "master" to it.master,
                        "registrert" to it.registrertINAV
                )
            }

    private fun hentAdressat(adressat: Adressat): Map<String, Any?> =
            mapOf(
                    "advokatSomAdressat" to adressat.advokatSomAdressat?.let { hentAdvokatSomAdressat(it) },
                    "kontaktpersonMedIdNummerSomAdressat" to adressat.kontaktpersonMedIdNummerSomAdressat?.let { hentKontaktpersonMedId(it) },
                    "kontaktpersonUtenIdNummerSomAdressat" to adressat.kontaktpersonUtenIdNummerSomAdressat?.let { hentKontaktpersonUtenId(it) },
                    "organisasjonSomAdressat" to adressat.organisasjonSomAdressat?.let { hentOrganisasjonSomAdressat(it) }
            )


    private fun hentKontaktpersonMedId(adressat: KontaktpersonMedIdNummerSomAdressat): Map<String, Any?> {
        val personnavnV3 = hentPersonnavnFraKjerneinfo(adressat.idNummer.toString())
        val personNavn = personnavnV3?.let { personoppslagAsPersonNavn(it) }

        return mapOf(
                "idNummer" to adressat.idNummer,
                "navn" to personNavn?.let { personNavn(it) }
        )
    }

    private fun hentPersonnavnFraKjerneinfo(idNummer: String): Personnavn? =
            try {
                val kjerneinfo = kjerneinfoService.hentKjerneinformasjon(kjerneinfoRequestMedBegrunnet(idNummer))
                kjerneinfo.person.personfakta.personnavn
            } catch(e: Exception) {
                null
            }

    private fun kjerneinfoRequestMedBegrunnet(ident: String): HentKjerneinformasjonRequest {
        val request = HentKjerneinformasjonRequest(ident)
        request.isBegrunnet = true
        return request
    }

    private fun personoppslagAsPersonNavn(personnavnV3: Personnavn): PersonNavn =
            PersonNavn(personnavnV3.fornavn, personnavnV3.mellomnavn, personnavnV3.etternavn)


    private fun hentAdvokatSomAdressat(adressat: AdvokatSomAdressat): Map<String, Any?> =
            mapOf(
                    "kontaktperson" to personNavn(adressat.kontaktperson),
                    "organisasjonsnavn" to adressat.organisasjonsnavn,
                    "organisasjonsnummer" to adressat.organisasjonsnummer
            )

    private fun hentOrganisasjonSomAdressat(adressat: OrganisasjonSomAdressat): Map<String, Any?> =
            mapOf(
                    "kontaktperson" to adressat.kontaktperson?.let { personNavn(it) },
                    "organisasjonsnavn" to adressat.organisasjonsnavn,
                    "organisasjonsnummer" to adressat.organisasjonsnummer
            )

    private fun hentKontaktpersonUtenId(adressat: KontaktpersonUtenIdNummerSomAdressat): Map<String, Any?> =
            mapOf(
                    "foedselsdato" to adressat.foedselsdato,
                    "navn" to personNavn(adressat.navn)
            )

    private fun personNavn(personNavn: PersonNavn): Map<String, Any?> {
        val sammensatNavn = "${personNavn.fornavn} ${personNavn.mellomnavn.textOrEmpty()} ${personNavn.etternavn}"
        return mapOf("fornavn" to personNavn.fornavn,
                "etternavn" to personNavn.etternavn,
                "mellomnavn" to personNavn.mellomnavn,
                "sammensatt" to sammensatNavn)
    }

    private fun String?.textOrEmpty(): String = this ?: ""

}