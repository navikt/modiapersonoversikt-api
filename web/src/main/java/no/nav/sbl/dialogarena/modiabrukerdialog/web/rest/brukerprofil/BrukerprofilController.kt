package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest
import no.nav.brukerdialog.security.context.SubjectHandler
import no.nav.brukerprofil.domain.Bankkonto
import no.nav.brukerprofil.domain.BankkontoUtland
import no.nav.brukerprofil.domain.Bruker
import no.nav.brukerprofil.domain.Telefon
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
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserUgyldigInput
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest
import java.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status

const val ENDRE_NAVN_ROLLE = "0000-GA-BD06_EndreNavn"
const val ENDRE_ADRESSE_ROLLE = "0000-GA-BD06_EndreKontaktAdresse"
const val ENDRE_KONTONUMMER_ROLLE = "0000-GA-BD06_EndreKontonummer"

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
            else -> throw BadRequestException()
        }

        return skrivBrukerOgLagResponse(bruker)
    }

    @POST
    @Path("/tilrettelagtkommunikasjon")
    @Consumes(APPLICATION_JSON)
    fun endreTilrettelagtKommunikasjon(@PathParam("fnr") fødselsnummer: String,
                                       request: EndreTilrettelagtkommunikasjonRequest) =
            fødselsnummer
                    .also { check(visFeature(PERSON_REST_API)) }
                    .let(kjerneinfoService::hentBrukerprofil)
                    .apply { tilrettelagtKommunikasjon = request.map { Kodeverdi(it, "") } }
                    ?.run(::skrivBrukerOgLagResponse)

    @POST
    @Path("/telefonnummer")
    @Consumes(APPLICATION_JSON)
    fun endreTelefonnummer(@PathParam("fnr") fødselsnummer: String, request: EndreTelefonnummerRequest) =
            fødselsnummer
                    .also { check(visFeature(PERSON_REST_API)) }
                    .let(kjerneinfoService::hentBrukerprofil)
                    .apply {
                        mobil = request.mobil?.let { mapTelefon(it, "MOBI") }
                        hjemTlf = request.hjem?.let { mapTelefon(it, "HJET") }
                        jobbTlf = request.jobb?.let { mapTelefon(it, "ARBT") }
                    }
                    ?.run(::skrivBrukerOgLagResponse)

    @POST
    @Path("/kontonummer")
    @Consumes(APPLICATION_JSON)
    fun endreKontonummer(@PathParam("fnr") fødselsnummer: String, request: EndreKontonummerRequest) =
            fødselsnummer
                    .also { check(visFeature(PERSON_REST_API)) }
                    .also { verifyTilgang(ENDRE_KONTONUMMER_ROLLE) }
                    .let(kjerneinfoService::hentBrukerprofil)
                    .apply {
                        bankkonto = when (request.landkode) {
                            "NOR", null, "" -> Bankkonto()
                            else -> BankkontoUtland().apply { populer(request) }
                        }.apply { kontonummer = request.kontonummer }
                    }
                    ?.run(::skrivBrukerOgLagResponse)

    private fun BankkontoUtland.populer(request: EndreKontonummerRequest) {
        banknavn = request.banknavn
        bankkode = request.bankkode
        swift = request.swift
        landkode = Kodeverdi().apply { kodeRef = request.landkode }
        valuta = Kodeverdi().apply { kodeRef = request.valuta }
        bankadresse = request.bankadresse.let {
            UstrukturertAdresse().apply {
                adresselinje1 = it?.linje1
                adresselinje2 = it?.linje2
                adresselinje3 = it?.linje3
            }
        }
    }

    private fun verifyTilgang(rolle: String) {
        val consumerId = SubjectHandler.getSubjectHandler().uid
        if (!ldapService.saksbehandlerHarRolle(consumerId, rolle)) {
            throw ForbiddenException("Saksbehandler $consumerId har ikke rollen $rolle")
        }
    }

    private fun skrivBrukerOgLagResponse(bruker: Bruker) = try {
        behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest(bruker))
        status(OK)
    } catch (_: OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning) {
        status(FORBIDDEN)
    } catch (_: OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet) {
        status(NOT_FOUND)
    } catch (_: OppdaterKontaktinformasjonOgPreferanserUgyldigInput) {
        status(BAD_REQUEST)
    } catch (_: OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt) {
        status(GONE)
    }.build()

}

private fun mapTelefon(telefonnummer: EndreTelefonnummerRequest.Telefon, telefonType: String) =
        telefonnummer.let {
            Telefon().apply {
                identifikator = it.identifikator
                retningsnummer = Kodeverdi().apply { kodeRef = it.retningsnummer }
                type = Kodeverdi(telefonType, telefonType)
            }
        }

private fun Bruker.setUtenlandskAdresse(adresse: EndreAdresseRequest.UtenlandskAdresse) {
    midlertidigadresseNorge = null
    midlertidigadresseUtland = UstrukturertAdresse().apply {
        landkode = Kodeverdi().apply { kodeRef = adresse.landkode }
        adresselinje1 = adresse.adresselinje1
        adresselinje2 = adresse.adresselinje2
        adresselinje3 = adresse.adresselinje3
        postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
    }
}

private fun Bruker.setNorskAdresse(norskAdresse: EndreAdresseRequest.NorskAdresse) {
    val adresse = norskAdresse.gateadresse ?: norskAdresse.matrikkeladresse ?: norskAdresse.postboksadresse

    midlertidigadresseUtland = null
    midlertidigadresseNorge = when (adresse) {
        is EndreAdresseRequest.NorskAdresse.Gateadresse -> Gateadresse().apply {
            co = "C/O"
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            coadresse = adresse.tilleggsadresse
            poststed = adresse.postnummer
            gatenavn = adresse.gatenavn
            bolignummer = adresse.bolignummer
            husbokstav = adresse.husbokstav
            husnummer = adresse.husnummer
        }
        is EndreAdresseRequest.NorskAdresse.Områdeadresse -> Matrikkeladresse().apply {
            co = "C/O"
            coadresse = adresse.tilleggsadresse
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            eiendomsnavn = adresse.områdeadresse
            poststed = adresse.postnummer
        }
        is EndreAdresseRequest.NorskAdresse.Postboksadresse -> Postboksadresse().apply {
            co = "C/O"
            coadresse = adresse.tilleggsadresse
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            postboksanlegg = adresse.postboksanleggnavn
            postboksnummer = adresse.postboksnummer
            poststed = adresse.postnummer
        }
        else -> throw BadRequestException()
    }
}

private fun javaLocalDatetoJoda(dato: LocalDate) = org.joda.time.LocalDate(dato.year, dato.monthValue, dato.dayOfMonth)
