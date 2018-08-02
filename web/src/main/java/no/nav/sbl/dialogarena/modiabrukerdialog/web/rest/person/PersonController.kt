package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person

import no.nav.kjerneinfo.common.domain.Kodeverdi
import no.nav.kjerneinfo.common.domain.Periode
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest
import no.nav.kjerneinfo.domain.info.BankkontoUtland
import no.nav.kjerneinfo.domain.person.*
import no.nav.kjerneinfo.domain.person.fakta.Sikkerhetstiltak
import no.nav.kjerneinfo.domain.person.fakta.Telefon
import no.nav.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk.Kode
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagPeriode
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.mapOfNotNullOrEmpty
import no.nav.tjeneste.virksomhet.person.v3.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.HentPersonSikkerhetsbegrensning
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType.APPLICATION_JSON

private const val TPS_UKJENT_VERDI = "???"
private const val DATO_TID_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"
private const val TILRETTELAGT_KOMMUNIKASJON_KODEVERKREF = "TilrettelagtKommunikasjon"
private const val TILRETTELAGT_KOMMUNIKASJON_KODEVERKSPRAK = "nb"

@Path("/person/{fnr}")
@Produces(APPLICATION_JSON)
class PersonController @Inject constructor(private val kjerneinfoService: PersonKjerneinfoServiceBi,
                                           private val kodeverk: KodeverkmanagerBi, private val unleashService: UnleashService) {

    @GET
    @Path("/")
    fun hent(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        val person = try {
            kjerneinfoService.hentKjerneinformasjon(HentKjerneinformasjonRequest(fødselsnummer)).person
        } catch (exception: AuthorizationWithSikkerhetstiltakException) {
            return getBegrensetInnsyn(fødselsnummer, exception.message)
        } catch (exception: RuntimeException) {
            when (exception.cause) {
                is HentPersonPersonIkkeFunnet -> throw NotFoundException()
                is HentPersonSikkerhetsbegrensning -> return getBegrensetInnsyn(fødselsnummer, exception.message)
                else -> throw InternalServerErrorException(exception)
            }
        }

        return mapOf(
                "fødselsnummer" to person.fodselsnummer.nummer,
                "alder" to person.fodselsnummer.alder,
                "kjønn" to person.personfakta.kjonn.kodeRef,
                "geografiskTilknytning" to person.personfakta.geografiskTilknytning?.value,
                "navn" to getNavn(person.personfakta.personnavn),
                "diskresjonskode" to person.personfakta.diskresjonskode?.let {Kode(it)},
                "bankkonto" to hentBankkonto(person),
                "tilrettelagtKomunikasjonsListe" to hentTilrettelagtKommunikasjon(person.personfakta.tilrettelagtKommunikasjon),
                "personstatus" to getPersonstatus(person),
                "statsborgerskap" to mapStatsborgerskap(person.personfakta),
                "sivilstand" to mapOf(
                        "kodeRef" to person.personfakta.sivilstand?.kodeRef,
                        "beskrivelse" to person.personfakta.sivilstand?.beskrivelse,
                        "fraOgMed" to person.personfakta.sivilstandFom
                ),
                "familierelasjoner" to getFamilierelasjoner(person),
                "fodselsdato" to person.fodselsnummer.fodselsdato,
                "folkeregistrertAdresse" to person.personfakta.bostedsadresse?.let { hentAdresse(it) },
                "alternativAdresse" to person.personfakta.alternativAdresse?.let { hentAdresse(it) },
                "postadresse" to person.personfakta.postadresse?.let { hentAdresse(it) },
                "sikkerhetstiltak" to person.personfakta.sikkerhetstiltak?.let { hentSikkerhetstiltak(it) },
                "kontaktinformasjon" to getTelefoner(person.personfakta)
        )
    }

    private fun mapStatsborgerskap(personfakta: Personfakta) =
            personfakta.statsborgerskap?.let { if (it.kodeRef == TPS_UKJENT_VERDI) null else Kode(it) }

    private fun getPersonstatus(person: Person) = mapOf(
            "dødsdato" to person.personfakta.doedsdato,
            "bostatus" to person.personfakta.bostatus?.let(::Kode)
    )

    private fun hentTilrettelagtKommunikasjon(tilrettelagtKommunikasjon: List<Kodeverdi>) =
            hentSortertKodeverkslisteForTilrettelagtKommunikasjon()
                    .filter { tilrettelagtKommunikasjon.any { tk -> tk.kodeRef == it.kodeRef } }
                    .map(::Kode)

    private fun getNavn(personnavn: Personnavn) = mapOf(
            "sammensatt" to personnavn.sammensattNavn,
            "fornavn" to personnavn.fornavn,
            "mellomnavn" to (personnavn.mellomnavn ?: ""),
            "etternavn" to personnavn.etternavn
    )

    private fun getFamilierelasjoner(person: Person) = person.personfakta.harFraRolleIList.map {
        mapOf(
                "harSammeBosted" to it.harSammeBosted,
                "tilPerson" to mapOf(
                        "navn" to it.tilPerson.personfakta.personnavn?.let { getNavn(it) },
                        "alder" to it.tilPerson.fodselsnummer.alder,
                        "alderMåneder" to it.tilPerson.fodselsnummer.alderIManeder,
                        "fødselsnummer" to if (it.tilPerson.isHideFodselsnummerOgNavn) null else it.tilPerson.fodselsnummer.nummer,
                        "personstatus" to getPersonstatus(it.tilPerson),
                        "diskresjonskode" to it.tilPerson.personfakta.diskresjonskode?.let { Kode(it) }
                        ),
                "rolle" to it.tilRolle
        )
    }

    private fun hentBankkonto(person: Person) = person.personfakta.let {
        it.bankkonto?.run {
            mapOfNotNullOrEmpty(
                    "kontonummer" to kontonummer,
                    "banknavn" to banknavn,
                    "sistEndret" to endringsinformasjon.sistOppdatert,
                    "sistEndretAv" to endringsinformasjon.endretAv
            ).plus(
                    if (it.isBankkontoIUtland) {
                        (this as BankkontoUtland).let {
                            mapOfNotNullOrEmpty(
                                    "bankkode" to it.bankkode,
                                    "swift" to it.swift,
                                    "landkode" to it.landkode?.let(::Kode),
                                    "adresse" to it.bankadresse?.let {
                                        mapOfNotNullOrEmpty(
                                                "linje1" to it.adresselinje1,
                                                "linje2" to it.adresselinje2,
                                                "linje3" to it.adresselinje3
                                        )
                                    },
                                    "valuta" to it.valuta?.let(::Kode)
                            )
                        }
                    } else {
                        emptyMap<String, Any?>()
                    }
            )
        }
    }

    private fun hentAdresse(adresselinje: Adresselinje) =
            mapOf("endringsinfo" to adresselinje.endringsinformasjon?.let { hentEndringsinformasjon(it) },
                    when (adresselinje) {
                        is Adresse -> "gateadresse" to hentGateAdresse(adresselinje)
                        is Matrikkeladresse -> "matrikkeladresse" to hentMatrikkeladresse(adresselinje)
                        is Postboksadresse -> "postboksadresse" to hentPostboksadresse(adresselinje);
                        is AlternativAdresseUtland -> "utlandsadresse" to hentAlternativAdresseUtland(adresselinje)
                        else -> "ustrukturert" to mapOf("adresselinje" to adresselinje.adresselinje)
                    }
            )

    private fun hentGateAdresse(adresse: Adresse) = adresse.run {
        mapOf(
                "tilleggsadresse" to tilleggsadresse,
                "gatenavn" to gatenavn,
                "husnummer" to gatenummer,
                "postnummer" to postnummer,
                "poststed" to poststednavn,
                "husbokstav" to husbokstav,
                "bolignummer" to bolignummer,
                "periode" to postleveringsPeriode?.let { lagPeriode(it) }
        )
    }

    private fun hentMatrikkeladresse(matrikkeladresse: Matrikkeladresse) = matrikkeladresse.run {
        mapOf(
                "tilleggsadresse" to tilleggsadresseMedType,
                "eiendomsnavn" to eiendomsnavn,
                "postnummer" to postnummer,
                "poststed" to poststed,
                "periode" to postleveringsPeriode?.let { lagPeriode(it) }
        )
    }

    private fun hentPostboksadresse(postboksadresse: Postboksadresse) = postboksadresse.run {
        mapOf(
                "tilleggsadresse" to tilleggsadresseMedType,
                "postboksnummer" to postboksnummer?.trim(),
                "postboksanlegg" to postboksanlegg,
                "poststed" to poststednavn,
                "postnummer" to poststed,
                "periode" to postleveringsPeriode?.let { lagPeriode(it) }
        )
    }

    private fun hentAlternativAdresseUtland(adresseUtland: AlternativAdresseUtland) = adresseUtland.run {
        mapOf(
                "landkode" to Kode(landkode),
                "adresselinjer" to listOfNotNull(adresselinje1, adresselinje2, adresselinje3, adresselinje4),
                "periode" to postleveringsPeriode?.let { lagPeriode(it) }
        )
    }

    private fun hentEndringsinformasjon(endringsinformasjon: Endringsinformasjon) = mapOf(
            "sistEndretAv" to endringsinformasjon.endretAv,
            "sistEndret" to endringsinformasjon.sistOppdatert?.toString(DATO_TID_FORMAT)
    )

    private fun hentSikkerhetstiltak(sikkerhetstiltak: Sikkerhetstiltak) = mapOf(
            "sikkerhetstiltaksbeskrivelse" to sikkerhetstiltak.sikkerhetstiltaksbeskrivelse,
            "sikkerhetstiltakskode" to sikkerhetstiltak.sikkerhetstiltakskode,
            "periode" to sikkerhetstiltak.periode?.let { lagPeriode(it) }
    )

    private fun getTelefoner(personfakta: Personfakta) = personfakta.run {
        mapOf(
                "mobil" to personfakta.mobil.map(::getTelefon).orElse(null),
                "jobbTelefon" to personfakta.jobbTlf.map(::getTelefon).orElse(null),
                "hjemTelefon" to personfakta.hjemTlf.map(::getTelefon).orElse(null)
        )
    }

    private fun getTelefon(telefon: Telefon) = mapOf(
            "retningsnummer" to telefon.retningsnummer?.let(::Kode),
            "identifikator" to telefon.identifikator,
            "sistEndretAv" to telefon.endretAv,
            "sistEndret" to telefon.endringstidspunkt?.toString(DATO_TID_FORMAT)
    )

    private fun getBegrensetInnsyn(fødselsnummer: String, melding: String?) = mapOf(
            "begrunnelse" to melding,
            "sikkerhetstiltak" to kjerneinfoService
                    .hentSikkerhetstiltak(HentSikkerhetstiltakRequest(fødselsnummer))
                    ?.let { hentSikkerhetstiltak(it) }
    )

    private fun hentSortertKodeverkslisteForTilrettelagtKommunikasjon() = try {
        kodeverk.getKodeverkList(TILRETTELAGT_KOMMUNIKASJON_KODEVERKREF, TILRETTELAGT_KOMMUNIKASJON_KODEVERKSPRAK)
    } catch (exception: HentKodeverkKodeverkIkkeFunnet) {
        emptyList<Kodeverdi>()
    }

}
