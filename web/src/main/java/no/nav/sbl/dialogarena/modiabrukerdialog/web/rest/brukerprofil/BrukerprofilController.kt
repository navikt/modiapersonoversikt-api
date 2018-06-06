package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.brukerprofil.domain.Bruker
import no.nav.brukerprofil.domain.adresser.Gateadresse
import no.nav.brukerprofil.domain.adresser.Matrikkeladresse
import no.nav.brukerprofil.domain.adresser.Postboksadresse
import no.nav.brukerprofil.domain.adresser.UstrukturertAdresse
import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.common.domain.Periode
import no.nav.kjerneinfo.consumer.fim.behandleperson.BehandlePersonServiceBi
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.visFeature
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest
import java.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.ok

const val ENDRE_NAVN_ROLLE = "0000-GA-BD06_EndreNavn"
const val ENDRE_ADRESSE_ROLLE = "0000-GA-BD06_EndreKontaktAdresse"

@Path("/brukerprofil/{fnr}")
@Produces(APPLICATION_JSON)
class BrukerprofilController @Inject constructor(private val behandlePersonService: BehandlePersonServiceBi,
                                                 private val behandleBrukerProfilService: BehandleBrukerprofilServiceBi,
                                                 private val kjerneinfoService: PersonKjerneinfoServiceBi,
                                                 private val ldapService: LDAPService) {

    @POST
    @Path("/navn")
    @Consumes(APPLICATION_JSON)
    fun endreNavn(@PathParam("fnr") fødselsnummer: String, endreNavnRequest: EndreNavnRequest): Response {
        check(visFeature(PERSON_REST_API))
        verifyTilgang(ENDRE_NAVN_ROLLE)

        val kjerneinformasjon = kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fødselsnummer))

        if (!kjerneinformasjon.person.kanEndreNavn()) {
            throw ForbiddenException("Det er ikke lovlig å endre navn til person med fødselsnummer: $fødselsnummer")
        }

        behandlePersonService.endreNavn(WSEndreNavnRequest()
                .withFnr(fødselsnummer)
                .withFornavn(endreNavnRequest.fornavn)
                .withMellomnavn(endreNavnRequest.mellomnavn)
                .withEtternavn(endreNavnRequest.etternavn))

        return ok().build()
    }

    @POST
    @Path("/adresse")
    @Consumes(APPLICATION_JSON)
    fun endreAdresse(@PathParam("fnr") fødselsnummer: String, request: EndreAdresseRequest): Response {
        check(visFeature(PERSON_REST_API))
        verifyTilgang(ENDRE_ADRESSE_ROLLE)

        val bruker = kjerneinfoService.hentBrukerprofil(fødselsnummer)

        val adresse = request.norskAdresse ?: request.utenlandskAdresse ?: request.folkeregistrertAdresse
        when (adresse) {
            is EndreAdresseRequest.NorskAdresse -> bruker.setNorskAdresse(adresse)
            is EndreAdresseRequest.UtenlandskAdresse -> bruker.setUtenlandskAdresse(adresse)
            true -> {
                bruker.midlertidigadresseNorge = null
                bruker.midlertidigadresseUtland = null
            }
        }

        behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest(bruker))

        return ok().build()
    }

    private fun verifyTilgang(rolle: String) {
        val consumerId = SubjectHandler.getSubjectHandler().uid
        if (!ldapService.saksbehandlerHarRolle(consumerId, rolle)) {
            throw ForbiddenException("Saksbehandler $consumerId har ikke rollen $rolle og kan derfor ikke endre navn")
        }
    }

}

private fun Bruker.setUtenlandskAdresse(adresse: EndreAdresseRequest.UtenlandskAdresse) {
    midlertidigadresseNorge = null
    midlertidigadresseUtland = UstrukturertAdresse().apply {
        landkode = Kodeverdi().apply { value = adresse.landkode }
        adresselinje1 = adresse.adresselinje1
        adresselinje2 = adresse.adresselinje2
        adresselinje3 = adresse.adresselinje3
        postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
    }
}

private fun Bruker.setNorskAdresse(norskAdresse: EndreAdresseRequest.NorskAdresse) {
    val adresse = norskAdresse.gateadresse ?: norskAdresse.områdeadresse ?: norskAdresse.postboksadresse

    midlertidigadresseUtland = null
    midlertidigadresseNorge = when (adresse) {
        is EndreAdresseRequest.NorskAdresse.Gateadresse -> Gateadresse().apply {
            co = "C/O"
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            coadresse = adresse.co
            poststed = adresse.postnummer
            gatenavn = adresse.gatenavn
            bolignummer = adresse.bolignummer
            husbokstav = adresse.husbokstav
            husnummer = adresse.husnummer
        }
        is EndreAdresseRequest.NorskAdresse.Områdeadresse -> Matrikkeladresse().apply {
            co = "C/O"
            coadresse = adresse.co
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            eiendomsnavn = adresse.områdeadresse
            poststed = adresse.postnummer
        }
        is EndreAdresseRequest.NorskAdresse.Postboksadresse -> Postboksadresse().apply {
            co = "C/O"
            coadresse = adresse.co
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            postboksanlegg = adresse.postboksanleggnavn
            postboksnummer = adresse.postboksnummer
            poststed = adresse.postnummer
        }
        else -> null
    }
}

private fun javaLocalDatetoJoda(dato: LocalDate) = org.joda.time.LocalDate(dato.year, dato.monthValue, dato.dayOfMonth)
