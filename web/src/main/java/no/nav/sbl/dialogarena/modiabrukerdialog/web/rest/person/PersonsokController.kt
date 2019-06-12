package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagXmlGregorianDato
import no.nav.tjeneste.virksomhet.personsoek.v1.PersonsokPortType
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimAdresseFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimFinnPersonRequest
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimPersonFilter
import no.nav.tjeneste.virksomhet.personsoek.v1.meldinger.FimSoekekriterie
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/personsok")
@Produces(MediaType.APPLICATION_JSON)
class PersonsokController @Inject constructor(private val personsokPortType: PersonsokPortType) {

    @POST
    fun sok(personsokRequest: PersonsokRequest): List<Map<String, Any?>> {
        val response = personsokPortType.finnPerson(lagPersonsokRequest(personsokRequest))

        if (response.personListe == null) {
            return emptyList()
        }

        return response.personListe.map { lagPersonResponse(it) }
    }

    private fun lagPersonResponse(fimPerson: FimPerson): Map<String, Any?> {
        return mapOf(
                "diskresjonskode" to fimPerson.diskresjonskode?.let { lagDiskresjonskode(it) },
                "postadresse" to fimPerson.postadresse?.ustrukturertAdresse?.let { lagUstrukturertAdresse(it) },
                "bostedsadresse" to fimPerson.bostedsadresse?.strukturertAdresse?.let { lagStrukturertAdresse(it) },
                "kjonn" to fimPerson.kjoenn?.kjoenn?.let { lagKjonn(it) },
                "navn" to fimPerson.personnavn?.let { lagNavn(it) },
                "status" to fimPerson.personstatus?.personstatus?.let { lagPersonstatus(it) },
                "ident" to fimPerson.ident?.let { lagNorskIdent(it) }
        )
    }

    private fun lagDiskresjonskode(fimDiskresjonskoder: FimDiskresjonskoder): Map<String, Any?> =
            mapOf(
                    "kodeverksRef" to fimDiskresjonskoder.kodeverksRef,
                    *lagKodeverdi(fimDiskresjonskoder)
            )

    private fun lagUstrukturertAdresse(fimUstrukturertAdresse: FimUstrukturertAdresse): Map<String, Any?> =
            mapOf(
                    "adresselinje1" to fimUstrukturertAdresse.adresselinje1,
                    "adresselinje2" to fimUstrukturertAdresse.adresselinje2,
                    "adresselinje3" to fimUstrukturertAdresse.adresselinje3,
                    "adresselinje4" to fimUstrukturertAdresse.adresselinje4
            )

    private fun lagStrukturertAdresse(fimStrukturertAdresse: FimStrukturertAdresse): Map<String, Any?> =
            mapOf(
                    "landkode" to fimStrukturertAdresse.landkode?.let { lagLandkode(it) },
                    "tilleggsadresse" to fimStrukturertAdresse.tilleggsadresse,
                    "tilleggsadresseType" to fimStrukturertAdresse.tilleggsadresseType
            )

    private fun lagLandkode(fimLandkoder: FimLandkoder): Map<String, Any?> =
            mapOf(
                    "kodeverksRef" to fimLandkoder.kodeverksRef,
                    *lagKodeverdi(fimLandkoder)
            )

    private fun lagKjonn(fimKjoennstyper: FimKjoennstyper): Map<String, Any?> =
            mapOf(
                    "kodeverksRef" to fimKjoennstyper.kodeverksRef,
                    *lagKodeverdi(fimKjoennstyper)
            )

    private fun lagNavn(fimPersonnavn: FimPersonnavn): Map<String, Any?> =
            mapOf(
                    "fornavn" to fimPersonnavn.fornavn,
                    "etternavn" to fimPersonnavn.etternavn,
                    "mellomnavn" to fimPersonnavn.mellomnavn,
                    "sammensatt" to fimPersonnavn.sammensattNavn
            )

    private fun lagPersonstatus(fimPersonstatuser: FimPersonstatuser): Map<String, Any?> =
            mapOf(
                    "kodeverksRef" to fimPersonstatuser.kodeverksRef,
                    *lagKodeverdi(fimPersonstatuser)
            )

    private fun lagNorskIdent(fimNorskIdent: FimNorskIdent): Map<String, Any?> =
            mapOf(
                    "ident" to fimNorskIdent.ident,
                    "type" to fimNorskIdent.type?.let { lagPersonident(it) }
            )

    private fun lagPersonident(fimPersonidenter: FimPersonidenter): Map<String, Any?> =
            mapOf(
                    "kodeverksRef" to fimPersonidenter.kodeverksRef,
                    *lagKodeverdi(fimPersonidenter)
            )

    private fun lagKodeverdi(fimKodeverdi: FimKodeverdi): Array<Pair<String, Any?>> =
            arrayOf(Pair("kodeRef", fimKodeverdi.kodeRef), Pair("value", fimKodeverdi.value))

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