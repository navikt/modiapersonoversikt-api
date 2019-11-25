package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil

import no.nav.behandlebrukerprofil.consumer.BehandleBrukerprofilServiceBi
import no.nav.behandlebrukerprofil.consumer.messages.BehandleBrukerprofilRequest
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
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.AuditResources.Person.Brukerprofil
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.UPDATE
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v2.OppdaterKontaktinformasjonOgPreferanserUgyldigInput
import no.nav.tjeneste.virksomhet.behandleperson.v1.meldinger.WSEndreNavnRequest
import org.slf4j.LoggerFactory
import java.time.LocalDate
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status.*
import javax.ws.rs.core.Response.ok
import javax.ws.rs.core.Response.status

@Path("/brukerprofil/{fnr}")
@Produces(APPLICATION_JSON)
class BrukerprofilController @Inject constructor(private val behandlePersonService: BehandlePersonServiceBi,
                                                 private val behandleBrukerProfilService: BehandleBrukerprofilServiceBi,
                                                 private val kjerneinfoService: PersonKjerneinfoServiceBi,
                                                 private val tilgangskontroll: Tilgangskontroll) {

    private val logger = LoggerFactory.getLogger(BrukerprofilController::class.java)

    @POST
    @Path("/navn")
    @Consumes(APPLICATION_JSON)
    fun endreNavn(@PathParam("fnr") fnr: String, endreNavnRequest: EndreNavnRequest): Response {
        return tilgangskontroll
                .check(Policies.kanEndreNavn)
                .get(Audit.describe(UPDATE, Brukerprofil.Navn, "fnr" to fnr)) {
                    val kjerneinformasjon = kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fnr))

                    if (!kjerneinformasjon.person.kanEndreNavn()) {
                        throw ForbiddenException("Det er ikke lovlig å endre navn til person med fnr: $fnr")
                    }

                    behandlePersonService.endreNavn(WSEndreNavnRequest()
                            .withFnr(fnr)
                            .withFornavn(endreNavnRequest.fornavn)
                            .withMellomnavn(endreNavnRequest.mellomnavn)
                            .withEtternavn(endreNavnRequest.etternavn))

                    ok().build()
                }
    }

    @POST
    @Path("/adresse")
    @Consumes(APPLICATION_JSON)
    fun endreAdresse(@PathParam("fnr") fnr: String, request: EndreAdresseRequest): Response {
        return tilgangskontroll
                .check(Policies.kanEndreAdresse)
                .get(Audit.describe(UPDATE, Brukerprofil.Adresse, "fnr" to fnr)) {
                    val bruker = kjerneinfoService.hentBrukerprofil(fnr)

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

                    skrivBrukerOgLagResponse(bruker)
                }
    }

    @POST
    @Path("/tilrettelagtkommunikasjon")
    @Consumes(APPLICATION_JSON)
    fun endreTilrettelagtKommunikasjon(@PathParam("fnr") fnr: String,
                                       request: EndreTilrettelagtkommunikasjonRequest) =
            tilgangskontroll
                    .check(Policies.tilgangTilBruker.with(fnr))
                    .get(Audit.describe(UPDATE, Brukerprofil.TilrettelagtKommunikasjon, "fnr" to fnr)) {
                        fnr
                                .let(kjerneinfoService::hentBrukerprofil)
                                .apply { tilrettelagtKommunikasjon = request.tilrettelagtKommunikasjon.map { Kodeverdi(it, "") } }
                                .run(::skrivBrukerOgLagResponse)
                    }

    @POST
    @Path("/telefonnummer")
    @Consumes(APPLICATION_JSON)
    fun endreTelefonnummer(@PathParam("fnr") fnr: String, request: EndreTelefonnummerRequest) =
            tilgangskontroll
                    .check(Policies.tilgangTilBruker.with(fnr))
                    .get(Audit.describe(UPDATE, Brukerprofil.Telefonnummer, "fnr" to fnr)) {
                        fnr
                                .let(kjerneinfoService::hentBrukerprofil)
                                .apply {
                                    mobil = request.mobil?.let { mapTelefon(it, "MOBI") }
                                    hjemTlf = request.hjem?.let { mapTelefon(it, "HJET") }
                                    jobbTlf = request.jobb?.let { mapTelefon(it, "ARBT") }
                                }
                                .run(::skrivBrukerOgLagResponse)
                    }

    @POST
    @Path("/kontonummer")
    @Consumes(APPLICATION_JSON)
    fun endreKontonummer(@PathParam("fnr") fnr: String, request: EndreKontonummerRequest) =
            tilgangskontroll
                    .check(Policies.kanEndreKontonummer)
                    .get(Audit.describe(UPDATE, Brukerprofil.Kontonummer, "fnr" to fnr)) {
                        fnr
                                .let(kjerneinfoService::hentBrukerprofil)
                                .apply {
                                    bankkonto = when (request.landkode) {
                                        "NOR", null, "" -> Bankkonto()
                                        else -> BankkontoUtland().apply { populer(request) }
                                    }.apply { kontonummer = request.kontonummer }
                                }
                                .run(::skrivBrukerOgLagResponse)
                    }

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

    private fun skrivBrukerOgLagResponse(bruker: Bruker) = try {
        behandleBrukerProfilService.oppdaterKontaktinformasjonOgPreferanser(BehandleBrukerprofilRequest(bruker))
        status(OK)
    } catch (e: OppdaterKontaktinformasjonOgPreferanserSikkerhetsbegrensning) {
        logger.warn("Saksbehandler har ikke tilgang til å endre brukerprofil for " + bruker.ident, e)
        status(FORBIDDEN)
    } catch (e: OppdaterKontaktinformasjonOgPreferanserPersonIkkeFunnet) {
        logger.warn("Saksbehandler forsøkte å endre brukerprofil til person som ikke ble funnet: " + bruker.ident, e)
        status(NOT_FOUND)
    } catch (e: OppdaterKontaktinformasjonOgPreferanserUgyldigInput) {
        logger.warn("Saksbehandler forsøkte å endre brukerprofil til " + bruker.ident + " med ugyldig input", e)
        status(BAD_REQUEST)
    } catch (e: OppdaterKontaktinformasjonOgPreferanserPersonIdentErUtgaatt) {
        logger.warn("Saksbehandler forsøkte å endre brukerprofil til person som er utgått: " + bruker.ident, e)
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
            tilleggsadresseType = "C/O"
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            tilleggsadresse = adresse.tilleggsadresse
            poststed = adresse.postnummer
            gatenavn = adresse.gatenavn
            bolignummer = adresse.bolignummer
            husbokstav = adresse.husbokstav
            husnummer = adresse.husnummer
        }
        is EndreAdresseRequest.NorskAdresse.Matrikkeladresse -> Matrikkeladresse().apply {
            tilleggsadresseType = "C/O"
            tilleggsadresse = adresse.tilleggsadresse
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            eiendomsnavn = adresse.eiendomsnavn
            poststed = adresse.postnummer
        }
        is EndreAdresseRequest.NorskAdresse.Postboksadresse -> Postboksadresse().apply {
            tilleggsadresseType = "C/O"
            tilleggsadresse = adresse.tilleggsadresse
            postleveringsPeriode = Periode(org.joda.time.LocalDate.now(), javaLocalDatetoJoda(adresse.gyldigTil))
            postboksanlegg = adresse.postboksanlegg
            postboksnummer = adresse.postboksnummer
            poststed = adresse.postnummer
        }
        else -> throw BadRequestException()
    }
}

private fun javaLocalDatetoJoda(dato: LocalDate) = org.joda.time.LocalDate(dato.year, dato.monthValue, dato.dayOfMonth)
