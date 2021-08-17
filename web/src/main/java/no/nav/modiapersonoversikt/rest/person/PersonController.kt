package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.KodeverkmanagerBi
import no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.feil.HentKodeverkKodeverkIkkeFunnet
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit.Action.READ
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.scientist.Scientist
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentIdenter
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Kodeverdi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.exception.AuthorizationWithSikkerhetstiltakException
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.fim.person.to.HentSikkerhetstiltakRequest
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.info.BankkontoUtland
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.*
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Sikkerhetstiltak
import no.nav.modiapersonoversikt.legacy.kjerneinfo.domain.person.fakta.Telefon
import no.nav.modiapersonoversikt.rest.kodeverk.Kode
import no.nav.modiapersonoversikt.rest.lagPeriode
import no.nav.modiapersonoversikt.rest.mapOfNotNullOrEmpty
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentPersonSikkerhetsbegrensning
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

private const val TPS_UKJENT_VERDI = "???"
private const val DATO_TID_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS"
private const val TILRETTELAGT_KOMMUNIKASJON_KODEVERKREF = "TilrettelagtKommunikasjon"
private const val TILRETTELAGT_KOMMUNIKASJON_KODEVERKSPRAK = "nb"

@RestController
@RequestMapping("/rest/person/{fnr}")
class PersonController @Autowired constructor(
    private val kjerneinfoService: PersonKjerneinfoServiceBi,
    private val kodeverk: KodeverkmanagerBi,
    private val tilgangskontroll: Tilgangskontroll,
    private val pdlOppslagService: PdlOppslagService,
    private val standardKodeverk: StandardKodeverk
) {
    private val logger = LoggerFactory.getLogger(PersonController::class.java)
    private val kjoennExperiment = Scientist.createExperiment<String?>(Scientist.Config("PDL-Kjønn", 0.2))

    @GetMapping
    fun hent(@PathVariable("fnr") fodselsnummer: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fodselsnummer))
            .get(Audit.describe(READ, AuditResources.Person.Personalia, AuditIdentifier.FNR to fodselsnummer)) {
                try {
                    val hentKjerneinformasjonRequest = HentKjerneinformasjonRequest(fodselsnummer)
                    hentKjerneinformasjonRequest.isBegrunnet = true
                    val person: Person? = kjerneinfoService.hentKjerneinformasjon(hentKjerneinformasjonRequest).person

                    val pdlPerson: HentPerson.Person? = pdlOppslagService.hentPerson(fodselsnummer)
                    val kontaktinfoForDoedsbo = pdlPerson?.kontaktinformasjonForDoedsbo ?: emptyList()
                    val fullmakt = pdlPerson?.fullmakt ?: emptyList()
                    val vergemal = pdlPerson?.vergemaalEllerFremtidsfullmakt ?: emptyList()
                    val deltBosted = pdlPerson?.deltBosted ?: emptyList()
                    val pdlTelefonnummer = (pdlPerson?.telefonnummer ?: emptyList())
                        .sortedBy { it.prioritet }
                        .map(::getPdlTelefon)
                    val foreldreansvar = pdlPerson?.foreldreansvar ?: emptyList()

                    val kjoenn = kjoennExperiment.run(
                        { person?.personfakta?.kjonn?.kodeRef },
                        { pdlPerson?.kjoenn?.get(0)?.kjoenn?.name }
                    )

                    mapOf(
                        "fødselsnummer" to person?.fodselsnummer?.nummer,
                        "aktorId" to hentAktorId(fodselsnummer),
                        "alder" to person?.fodselsnummer?.alder,
                        "kjønn" to kjoenn,
                        "geografiskTilknytning" to person?.personfakta?.geografiskTilknytning?.value,
                        "navn" to getNavn(person?.personfakta?.personnavn),
                        "diskresjonskode" to person?.personfakta?.diskresjonskode?.let { Kode(it) },
                        "bankkonto" to hentBankkonto(person),
                        "tilrettelagtKomunikasjonsListe" to hentTilrettelagtKommunikasjon(pdlPerson),
                        "personstatus" to getPersonstatus(person),
                        "statsborgerskap" to mapStatsborgerskap(person?.personfakta),
                        "sivilstand" to mapOf(
                            "kodeRef" to person?.personfakta?.sivilstand?.kodeRef,
                            "beskrivelse" to person?.personfakta?.sivilstand?.beskrivelse,
                            "fraOgMed" to person?.personfakta?.sivilstandFom
                        ),
                        "familierelasjoner" to getFamilierelasjoner(person),
                        "fodselsdato" to person?.fodselsnummer?.fodselsdato,
                        "folkeregistrertAdresse" to person?.personfakta?.bostedsadresse?.let { hentAdresse(it) },
                        "alternativAdresse" to person?.personfakta?.alternativAdresse?.let { hentAdresse(it) },
                        "postadresse" to person?.personfakta?.postadresse?.let { hentAdresse(it) },
                        "sikkerhetstiltak" to person?.personfakta?.sikkerhetstiltak?.let { hentSikkerhetstiltak(it) },
                        "kontaktinformasjon" to getTelefoner(person?.personfakta),
                        "telefonnummer" to pdlTelefonnummer,
                        "kontaktinformasjonForDoedsbo" to DoedsboMapping.mapKontaktinfoForDoedsbo(kontaktinfoForDoedsbo),
                        "fullmakt" to hentFullmakter(fullmakt),
                        "vergemal" to hentVergemal(vergemal),
                        "foreldreansvar" to hentForeldreansvar(foreldreansvar),
                        "deltBosted" to hentDeltBosted(deltBosted)
                    )
                } catch (exception: AuthorizationWithSikkerhetstiltakException) {
                    getBegrensetInnsyn(fodselsnummer, exception.message)
                } catch (exception: RuntimeException) {
                    when (exception.cause) {
                        is HentPersonPersonIkkeFunnet -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
                        is HentPersonSikkerhetsbegrensning -> getBegrensetInnsyn(fodselsnummer, exception.message)
                        else -> {
                            logger.error("mapping error personobjekt", exception)
                            throw ResponseStatusException(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "mapping in personobject",
                                exception
                            )
                        }
                    }
                }
            }
    }

    @GetMapping("/identer")
    fun hentIdenter(@PathVariable("fnr") fodselsnummer: String): HentIdenter.Identliste? {
        return pdlOppslagService.hentIdenter(fodselsnummer)
    }

    private fun hentAktorId(fnr: String): String? {
        return pdlOppslagService.hentAktorId(fnr)
    }

    data class VergemalDTO(
        val ident: String?,
        val navn: PersonnavnDTO?,
        val vergesakstype: String?,
        val omfang: String?,
        val embete: String?,
        val gyldighetstidspunkt: String?,
        val opphoerstidspunkt: String?
    )

    data class PersonnavnDTO(
        val fornavn: String,
        val mellomnavn: String?,
        val etternavn: String
    ) {
        val sammensatt = listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(" ")
    }
    data class DeltBostedDTO(
        val startdatoForKontrakt: String?,
        val sluttdatoForKontrakt: String?,
        val adresse: AdresseDTO?,
        val ukjentBosted: UkjentBostedDTO?
    )

    data class UkjentBostedDTO(
        val bostedskommune: String?
    )

    data class AdresseDTO(
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

    data class ForeldreansvarDTO(
        val ansvar: String,
        val ansvarlig: PersonnavnDTO?,
        val ansvarssubjekt: PersonnavnDTO?
    )

    private fun hentDeltBosted(deltBosted: List<HentPerson.DeltBosted>): List<DeltBostedDTO> {
        return deltBosted.map {
            val postnummer = it.vegadresse?.postnummer ?: it.matrikkeladresse?.postnummer
            DeltBostedDTO(
                startdatoForKontrakt = it.startdatoForKontrakt?.value?.format(ISO_DATE),
                sluttdatoForKontrakt = it.sluttdatoForKontrakt?.value?.format(ISO_DATE),
                adresse = AdresseDTO(
                    adressenavn = it.vegadresse?.adressenavn,
                    husbokstav = it.vegadresse?.husbokstav,
                    husnummer = it.vegadresse?.husnummer,
                    bruksenhetsnummer = it.vegadresse?.bruksenhetsnummer ?: it.matrikkeladresse?.bruksenhetsnummer,
                    kommunenummer = it.vegadresse?.kommunenummer ?: it.matrikkeladresse?.kommunenummer,
                    postnummer = postnummer,
                    poststed = hentPoststed(postnummer),
                    bydelsnummer = it.vegadresse?.bydelsnummer,
                    tilleggsnavn = it.vegadresse?.tilleggsnavn ?: it.matrikkeladresse?.tilleggsnavn,
                    coAdressenavn = it.coAdressenavn
                ),

                ukjentBosted = UkjentBostedDTO(
                    bostedskommune = it.ukjentBosted?.bostedskommune
                )
            )
        }
    }

    private fun hentForeldreansvar(foreldreansvar: List<HentPerson.Foreldreansvar>): List<ForeldreansvarDTO> {
        val ansvarlige = foreldreansvar.mapNotNull { it.ansvarlig }
        val ansvarssubjekt = foreldreansvar.mapNotNull { it.ansvarssubjekt }
        val alleNavn = (ansvarlige + ansvarssubjekt)
            .distinct()
            .let { pdlOppslagService.hentNavnBolk(it) }
            ?.filterValues { it != null }
            ?.mapValues {
                val personnavn = it.value!!
                PersonnavnDTO(personnavn.fornavn, personnavn.mellomnavn, personnavn.etternavn)
            }
            ?: emptyMap()
        return foreldreansvar.map {
            val ansvarligUtenIdNavn = it.ansvarligUtenIdentifikator?.navn?.let { person -> PersonnavnDTO(person.fornavn, person.mellomnavn, person.etternavn) }
            val ansvarlig = alleNavn[it.ansvarlig]
            val ansvarssubjekt = alleNavn[it.ansvarssubjekt]
            ForeldreansvarDTO(
                ansvar = it.ansvar ?: "Kunne ikke hente type ansvar",
                ansvarlig = ansvarlig ?: ansvarligUtenIdNavn,
                ansvarssubjekt = ansvarssubjekt
            )
        }
    }

    private fun hentVergemal(vergemal: List<HentPerson.VergemaalEllerFremtidsfullmakt>): List<VergemalDTO> {
        val allenavn: Map<String, PersonnavnDTO> = vergemal
            .mapNotNull { it.vergeEllerFullmektig.motpartsPersonident }
            .let { pdlOppslagService.hentNavnBolk(it) }
            ?.filterValues { it != null }
            ?.mapValues { entry ->
                val personnavn = entry.value!!
                PersonnavnDTO(personnavn.fornavn, personnavn.mellomnavn, personnavn.etternavn)
            }
            ?: emptyMap()

        return vergemal
            .map {
                val motpartsNavn = allenavn[it.vergeEllerFullmektig.motpartsPersonident]
                val navn = it.vergeEllerFullmektig.navn?.let { personnavn ->
                    PersonnavnDTO(personnavn.fornavn, personnavn.mellomnavn, personnavn.etternavn)
                }
                VergemalDTO(
                    ident = it.vergeEllerFullmektig.motpartsPersonident,
                    navn = motpartsNavn ?: navn,
                    vergesakstype = it.type,
                    omfang = it.vergeEllerFullmektig.omfang,
                    embete = it.embete,
                    gyldighetstidspunkt = it.folkeregistermetadata?.gyldighetstidspunkt?.value?.format(ISO_DATE_TIME),
                    opphoerstidspunkt = it.folkeregistermetadata?.opphoerstidspunkt?.value?.format(ISO_DATE_TIME)
                )
            }
    }

    private fun hentFullmakter(fullmakter: List<HentPerson.Fullmakt>?): List<Map<String, Any>>? {
        val allenavn = fullmakter
            ?.map { it.motpartsPersonident }
            ?.let { pdlOppslagService.hentNavnBolk(it) }
            ?: emptyMap()

        return fullmakter
            ?.map {
                val navnObject = allenavn[it.motpartsPersonident]
                val navn: String = navnObject
                    ?.run {
                        listOfNotNull(fornavn, mellomnavn, etternavn).joinToString(" ")
                    }
                    ?: "Fant ikke navn"

                mapOf(
                    "motpartsRolle" to it.motpartsRolle,
                    "motpartsPersonident" to it.motpartsPersonident,
                    "motpartsPersonNavn" to navn,
                    "omraade" to it.omraader.map { omraade -> standardKodeverk.getArkivtemaNavn(omraade) ?: omraade },
                    "gyldigFraOgMed" to formatDate(it.gyldigFraOgMed.value),
                    "gyldigTilOgMed" to formatDate(it.gyldigTilOgMed.value)
                )
            }
    }

    private fun mapStatsborgerskap(personfakta: Personfakta?) =
        personfakta?.statsborgerskap?.let { if (it.kodeRef == TPS_UKJENT_VERDI) null else Kode(it) }

    private fun getPersonstatus(person: Person?) = mapOf(
        "dødsdato" to person?.personfakta?.doedsdato,
        "bostatus" to person?.personfakta?.bostatus?.let(::Kode)
    )

    private fun hentTilrettelagtKommunikasjon(pdlPerson: HentPerson.Person?): List<TilrettelagtKommunikasjonsbehov> {
        val pdlTilrettelagtKommunikasjon: List<HentPerson.TilrettelagtKommunikasjon> = pdlPerson
            ?.tilrettelagtKommunikasjon
            ?: emptyList()
        logger.info("Tilrettelagt: " + pdlTilrettelagtKommunikasjon.toString())

        val out: MutableSet<TilrettelagtKommunikasjonsbehov> = mutableSetOf()
        for (behov in pdlTilrettelagtKommunikasjon) {
            behov
                .tegnspraaktolk
                ?.spraak
                ?.also { sprakRef ->
                    out.add(
                        TilrettelagtKommunikasjonsbehov(
                            TilrettelagtKommunikasjonsbehovType.TEGNSPRAK,
                            sprakRef,
                            hentSprak(sprakRef)
                        )
                    )
                }

            behov
                .talespraaktolk
                ?.spraak
                ?.also { sprakRef ->
                    out.add(
                        TilrettelagtKommunikasjonsbehov(
                            TilrettelagtKommunikasjonsbehovType.TALESPRAK,
                            sprakRef,
                            hentSprak(sprakRef)
                        )
                    )
                }
        }

        return out.toList()
    }

    private fun getNavn(personnavn: Personnavn?) = mapOf(
        "endringsinfo" to personnavn?.sistEndret?.let { hentEndringsinformasjon(it) },
        "sammensatt" to personnavn?.sammensattNavn,
        "fornavn" to (personnavn?.fornavn ?: ""),
        "mellomnavn" to (personnavn?.mellomnavn ?: ""),
        "etternavn" to (personnavn?.etternavn ?: "")
    )

    private fun getFamilierelasjoner(person: Person?) = person?.personfakta?.harFraRolleIList?.map {
        val (alder, alderIManeder) = hentFamilieRelasjonsAlder(it.tilPerson)
        mapOf(
            "harSammeBosted" to if (it.tilPerson.isHideFodselsnummerOgNavn) null else it.harSammeBosted,
            "tilPerson" to mapOf(
                "navn" to it.tilPerson.personfakta.personnavn?.let { navn -> getNavn(navn) },
                "alder" to alder,
                "alderMåneder" to alderIManeder,
                "fødselsnummer" to if (it.tilPerson.isHideFodselsnummerOgNavn) null else it.tilPerson.fodselsnummer.nummer,
                "personstatus" to getPersonstatus(it.tilPerson),
                "diskresjonskode" to it.tilPerson.personfakta.diskresjonskode?.let { kode -> Kode(kode) }
            ),
            "rolle" to it.tilRolle
        )
    }

    private fun hentFamilieRelasjonsAlder(person: Person): Pair<Int?, Int?> {
        if (person.isHideFodselsnummerOgNavn) {
            return Pair(null, null)
        } else if (!person.fodselsnummer.isDnummer && Integer.parseInt(person.fodselsnummer.nummer.substring(6)) < 10) {
//          Samme logikken som man kan finne i kjerneinfo's Fodselsnummer.java som forårsaker feil i ved visse fnr
            return Pair(null, null)
        } else {
            return Pair(person.fodselsnummer.alder, person.fodselsnummer.alderIManeder)
        }
    }

    private fun hentBankkonto(person: Person?) = person?.personfakta.let {
        it?.bankkonto?.run {
            mapOfNotNullOrEmpty(
                "kontonummer" to kontonummer,
                "banknavn" to banknavn,
                "sistEndret" to endringsinformasjon.sistOppdatert,
                "sistEndretAv" to endringsinformasjon.endretAv
            ).plus(
                if (it.isBankkontoIUtland) {
                    (this as BankkontoUtland).let { bankkonto ->
                        mapOfNotNullOrEmpty(
                            "bankkode" to bankkonto.bankkode,
                            "swift" to bankkonto.swift,
                            "landkode" to bankkonto.landkode?.let(::Kode),
                            "adresse" to bankkonto.bankadresse?.let { adresse ->
                                mapOfNotNullOrEmpty(
                                    "linje1" to (adresse.adresselinje1 ?: ""),
                                    "linje2" to (adresse.adresselinje2 ?: ""),
                                    "linje3" to (adresse.adresselinje3 ?: "")
                                )
                            },
                            "valuta" to bankkonto.valuta?.let(::Kode)
                        )
                    }
                } else {
                    emptyMap()
                }
            )
        }
    }

    private fun hentAdresse(adresselinje: Adresselinje) =
        mapOf(
            "endringsinfo" to adresselinje.endringsinformasjon?.let { hentEndringsinformasjon(it) },
            when (adresselinje) {
                is Adresse -> "gateadresse" to hentGateAdresse(adresselinje)
                is Matrikkeladresse -> "matrikkeladresse" to hentMatrikkeladresse(adresselinje)
                is Postboksadresse -> "postboksadresse" to hentPostboksadresse(adresselinje)
                is AlternativAdresseUtland -> "utlandsadresse" to hentAlternativAdresseUtland(adresselinje)
                else -> "ustrukturert" to mapOf("adresselinje" to adresselinje.adresselinje)
            }
        )

    private fun hentGateAdresse(adresse: Adresse) = adresse.run {
        mapOf(
            "tilleggsadresse" to tilleggsadresseMedType,
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

    private fun getTelefoner(personfakta: Personfakta?) = personfakta?.run {
        mapOf(
            "mobil" to personfakta?.mobil?.map(::getTelefon)?.orElse(null),
            "jobbTelefon" to personfakta?.jobbTlf?.map(::getTelefon)?.orElse(null),
            "hjemTelefon" to personfakta?.hjemTlf?.map(::getTelefon)?.orElse(null)
        )
    }

    private fun getTelefon(telefon: Telefon) = Telefonnummer(
        retningsnummer = telefon.retningsnummer?.let(::Kode),
        identifikator = telefon.identifikator ?: "Ukjent",
        sistEndretAv = telefon.endretAv ?: "Ukjent",
        sistEndret = telefon.endringstidspunkt?.toString(DATO_TID_FORMAT)
    )

    private fun getPdlTelefon(telefon: HentPerson.Telefonnummer) = Telefonnummer(
        retningsnummer = Kode(telefon.landskode, "Landskode"),
        identifikator = telefon.nummer,
        sistEndret = formatDate(telefon.metadata.endringer.first().registrert.value),
        sistEndretAv = telefon.metadata.endringer.first().registrertAv,
        prioritet = telefon.prioritet
    )

    private fun getBegrensetInnsyn(fodselsnummer: String?, melding: String?) = mapOf(
        "begrunnelse" to melding,
        "sikkerhetstiltak" to kjerneinfoService
            .hentSikkerhetstiltak(HentSikkerhetstiltakRequest(fodselsnummer))
            ?.let { hentSikkerhetstiltak(it) }
    )

    private fun hentSprak(sprakRef: String): String {
        val sprakKodeverk: List<Kodeverdi> = kodeverk.getKodeverkList("Språk", "nb")
        return sprakKodeverk
            .find { kodeverdi ->
                kodeverdi.kodeRef == sprakRef
            }
            ?.let(Kodeverdi::getBeskrivelse)
            ?: "Ukjent kodeverdi: $sprakRef"
    }

    private fun hentPoststed(postnummer: String?): String {
        if (postnummer.isNullOrBlank()) {
            return ""
        }
        val postKodeverk: List<Kodeverdi> = kodeverk.getKodeverkList("Postnummer", "nb")
        return postKodeverk
            .find { kodeverdi ->
                kodeverdi.kodeRef == postnummer
            }
            ?.let(Kodeverdi::getBeskrivelse)
            ?: "Ukjent kodeverdi: $postnummer"
    }

    private fun hentSortertKodeverkslisteForTilrettelagtKommunikasjon() = try {
        kodeverk.getKodeverkList(TILRETTELAGT_KOMMUNIKASJON_KODEVERKREF, TILRETTELAGT_KOMMUNIKASJON_KODEVERKSPRAK)
    } catch (exception: HentKodeverkKodeverkIkkeFunnet) {
        emptyList<Kodeverdi>()
    }
}

enum class TilrettelagtKommunikasjonsbehovType { TEGNSPRAK, TALESPRAK, UKJENT }
data class TilrettelagtKommunikasjonsbehov(
    val type: TilrettelagtKommunikasjonsbehovType,
    val kodeRef: String,
    val beskrivelse: String
)

data class Telefonnummer(
    val retningsnummer: Kode?,
    val identifikator: String,
    val sistEndretAv: String,
    val sistEndret: String?,
    val prioritet: Int = -1
)
