package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentGt
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPerson.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService
import no.nav.modiapersonoversikt.rest.person.formatDate
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PdlMapper(val pdlOppslagService: PdlOppslagService) {
    private val DATO_TID_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")

    fun from(person: HentPerson.Person): PdlDTO.Person {
        val fnr = hentFodselsnummer(person)
        val gt = pdlOppslagService.hentGeografiskTilknyttning(fnr)
        val relaterteNavn: Map<String, PdlDTO.Navn> = hentRelaterteNavn(person)

        return PdlDTO.Person(
            navn = hentNavn(person),
            kjønn = hentKjonn(person),
            kjonn = hentKjonn(person),
            geografiskTilknytning = hentGeografiskTilknytning(gt),
            fødselsnummer = fnr,
            fodselsnummer = fnr,
            fodselsdato = hentFodselsdato(person),
            alder = hentAlder(person),
            diskresjonskode = hentDiskresjonskode(person),
            tilrettelagtKomunikasjonsListe = hentTilrettelagtKommunikasjonsbehov(person),
            statsborgerskap = hentStatsborgerskap(person),
            folkeregistrertAdresse = hentFolkeregistrertAdresse(person),
            alternativAdresse = hentAlternativAdresse(person),
            postadresse = hentPostadresse(person),
            personstatus = hentBostatus(person),
            sivilstand = hentSivilstand(person),
            familierelasjoner = hentFamilierelasjon(person),
            kontaktinformasjon = hentNavKontaktinformasjon(person),
            kontaktinformasjonForDoedsbo = hentDodsbo(person),
            fullmakt = hentFullmakt(person, relaterteNavn),
            telefonnummer = hentTelefon(person),
            vergemal = hentVerge(person, relaterteNavn),
            foreldreansvar = hentForeldreansvar(person, relaterteNavn),
            deltBosted = hentDeltBosted(person),
            sikkerhetstiltak = hentSikkerhetstiltak(person)
        )
    }

    private fun hentRelaterteNavn(person: HentPerson.Person): Map<String, PdlDTO.Navn> {
        val relevanteIdenter: List<String> = setOf(
            *person.fullmakt.map { it.motpartsPersonident }.toTypedArray(),
            *person.vergemaalEllerFremtidsfullmakt.mapNotNull { it.vergeEllerFullmektig.motpartsPersonident }
                .toTypedArray(),
            *person.foreldreansvar.mapNotNull { it.ansvarlig }.toTypedArray(),
            *person.foreldreansvar.mapNotNull { it.ansvarssubjekt }.toTypedArray()
        ).toList()

        return pdlOppslagService.hentNavnBolk(relevanteIdenter)
            .mapValues {
                PdlDTO.Navn(
                    endringsinfo = null,
                    fornavn = it.value.fornavn,
                    mellomnavn = it.value.mellomnavn,
                    etternavn = it.value.etternavn
                )
            }
    }

    private fun hentNavn(person: HentPerson.Person): PdlDTO.Navn {
        return person.navn.first().let {
            PdlDTO.Navn(
                endringsinfo = null, // Ikke ibruk av frontend, bør fjernes
                fornavn = it.fornavn,
                mellomnavn = it.mellomnavn,
                etternavn = it.etternavn
            )
        }
    }

    private fun hentKjonn(person: HentPerson.Person): PdlDTO.Kjonn {
        return when (person.kjoenn.first().kjoenn) {
            HentPerson.KjoennType.MANN -> PdlDTO.Kjonn.M
            HentPerson.KjoennType.KVINNE -> PdlDTO.Kjonn.K
            else -> PdlDTO.Kjonn.D
        }
    }

    private fun hentGeografiskTilknytning(gt: HentGt.GeografiskTilknytning?): String? {
        return gt?.gtBydel ?: gt?.gtKommune ?: gt?.gtLand
    }

    private fun hentFodselsnummer(person: HentPerson.Person): String {
        return person.folkeregisteridentifikator
            .filter { it.type === "FNR" }
            .first { it.status == "I_BRUK" }
            .identifikasjonsnummer
    }

    private fun hentFodselsdato(person: HentPerson.Person): LocalDate? {
        return person.foedsel
            .first()
            .foedselsdato
            ?.value
    }

    private fun hentAlder(person: HentPerson.Person): Int {
        return ChronoUnit.YEARS.between(
            hentFodselsdato(person),
            LocalDate.now()
        ).toInt()
    }

    private fun hentDiskresjonskode(person: HentPerson.Person): PdlDTO.Kodeverk? {
        val graderinger = person.adressebeskyttelse.associateBy {
            it.gradering
        }
        return when {
            graderinger.containsKey(STRENGT_FORTROLIG_UTLAND) -> PdlDTO.Kodeverk(
                "SPSF",
                "Strengt fortrolig utland adresse"
            )
            graderinger.containsKey(STRENGT_FORTROLIG) -> PdlDTO.Kodeverk("SPSF", "Strengt fortrolig adresse")
            graderinger.containsKey(FORTROLIG) -> PdlDTO.Kodeverk("SPFO", "Fortrolig adresse")
            graderinger.containsKey(UGRADERT) -> null
            else -> null
        }
    }

    private fun hentTilrettelagtKommunikasjonsbehov(person: HentPerson.Person): List<PdlDTO.TilrettelagtKommunikasjonsbehov> {
        return person.tilrettelagtKommunikasjon
            .flatMap {
                val taletolk = it.talespraaktolk?.spraak?.let { sprakRef ->
                    PdlDTO.TilrettelagtKommunikasjonsbehov(
                        kodeRef = sprakRef,
                        beskrivelse = "", // TODO("Må hentes fra kodeverk")
                        type = PdlDTO.TilrettelagtKommunikasjonsbehovType.TALESPRAK
                    )
                }
                val tegntolk = it.tegnspraaktolk?.spraak?.let { sprakRef ->
                    PdlDTO.TilrettelagtKommunikasjonsbehov(
                        kodeRef = sprakRef,
                        beskrivelse = "", // TODO("Må hentes fra kodeverk")
                        type = PdlDTO.TilrettelagtKommunikasjonsbehovType.TEGNSPRAK
                    )
                }

                listOfNotNull(taletolk, tegntolk)
            }
    }

    private fun hentStatsborgerskap(person: HentPerson.Person): PdlDTO.Kodeverk? {
        return when (val landRef: String = person.statsborgerskap.first().land) {
            "XUK" -> null
            "XXX" -> null // XUK == ukjent, XXX == statsløs
            else -> PdlDTO.Kodeverk(
                kodeRef = landRef,
                beskrivelse = "" // TODO("Må hentes fra kodeverk")
            )
        }
    }

    private fun hentFolkeregistrertAdresse(person: HentPerson.Person): PdlDTO.Personadresse? {
        val bostedAdresse = person.bostedsadresse.firstOrNull() ?: return null
        val metadata = bostedAdresse.folkeregistermetadata
        val endringsinfo: PdlDTO.Endringsinfo? = metadata?.let {
            PdlDTO.Endringsinfo(
                sistEndretAv = it.kilde ?: "Ukjent",
                sistEndret = it.ajourholdstidspunkt?.value?.format(DATO_TID_FORMAT) ?: "Ukjent"
            )
        }
        val adresse = PdlDTO.Personadresse(endringsinfo = endringsinfo)
        return when {
            bostedAdresse.vegadresse != null -> with(bostedAdresse.vegadresse!!) {
                adresse.copy(
//                    TODO gateadresse = PdlDTO.Gateadresse(
//                        tilleggsadresse = tilleggsnavn,
//                        gatenavn = bostedAdresse.vegadresse
//                    )
                )
            }
            bostedAdresse.matrikkeladresse != null -> adresse.copy(
//                TODO matrikkeladresse = PdlDTO.Matrikkeladresse()
            )
            bostedAdresse.utenlandskAdresse != null -> adresse.copy(
//                TODO utlandsadresse = PdlDTO.Utlandsadresse()
            )
            bostedAdresse.ukjentBosted != null -> adresse.copy(
//                TODO ustrukturert = PdlDTO.UstrukturertAdresse()
            )
            else -> adresse
        }
    }

    private fun hentAlternativAdresse(person: HentPerson.Person): PdlDTO.Personadresse? {
        TODO("Not yet implemented")
    }

    private fun hentPostadresse(person: HentPerson.Person): PdlDTO.Personadresse? {
        TODO("Not yet implemented")
    }

    private fun hentBostatus(person: HentPerson.Person): PdlDTO.Bostatus {
        val dodsdato: LocalDate? = person.doedsfall.first().doedsdato?.value
        val bostatus = person.folkeregisterpersonstatus.first().status
        val tpsKodeverdi = when (bostatus) {
            "bosatt" -> PdlDTO.Kodeverk("BOSA", "Bosatt")
            "doed" -> PdlDTO.Kodeverk("DØD", "Død")
            "opphoert" -> PdlDTO.Kodeverk("UTPE", "Utgått person")
            "inaktiv" -> PdlDTO.Kodeverk("ADNR", "Inaktiv")
            "midlertidig" -> PdlDTO.Kodeverk("ADNR", "Midlertidig")
            "forsvunnet" -> PdlDTO.Kodeverk("FOSV", "Forsvunnet/savnet")
            "utflyttet" -> PdlDTO.Kodeverk("UTVA", "Utvandret")
            "ikkeBosatt" -> PdlDTO.Kodeverk("UREG", "Uregistrert person")
            "foedselsregistrert" -> PdlDTO.Kodeverk("FØDR", "Fødselsregistrert")
            else -> null
        }

        return PdlDTO.Bostatus(
            dødsdato = dodsdato,
            dodsdato = dodsdato,
            bostatus = tpsKodeverdi
        )
    }

    private val TPSNullDate = LocalDate.of(9999, Month.JANUARY, 1)
    private fun hentSivilstand(person: HentPerson.Person): PdlDTO.Sivilstand {
        val sivilstand = person.sivilstand.first()
        val gyldig = sivilstand.gyldigFraOgMed?.value
        val sivilstandKode = when (sivilstand.type) {
            HentPerson.Sivilstandstype.UOPPGITT -> PdlDTO.Kodeverk("NULL", "Uoppgitt")
            HentPerson.Sivilstandstype.UGIFT -> PdlDTO.Kodeverk("UGIF", "UGift")
            HentPerson.Sivilstandstype.GIFT -> PdlDTO.Kodeverk("GIFT", "Gift")
            HentPerson.Sivilstandstype.ENKE_ELLER_ENKEMANN -> PdlDTO.Kodeverk("ENKE", "Enke/-mann")
            HentPerson.Sivilstandstype.SKILT -> PdlDTO.Kodeverk("SKIL", "Skilt")
            HentPerson.Sivilstandstype.SEPARERT -> PdlDTO.Kodeverk("SEPR", "Separert")
            HentPerson.Sivilstandstype.REGISTRERT_PARTNER -> PdlDTO.Kodeverk("REPA", "Registrert partner")
            HentPerson.Sivilstandstype.SEPARERT_PARTNER -> PdlDTO.Kodeverk("SEPA", "Separert partner")
            HentPerson.Sivilstandstype.SKILT_PARTNER -> PdlDTO.Kodeverk("SKPA", "Skilt partner")
            HentPerson.Sivilstandstype.GJENLEVENDE_PARTNER -> PdlDTO.Kodeverk("GJPA", "Gjenlevende partner")
            HentPerson.Sivilstandstype.__UNKNOWN_VALUE -> throw IllegalStateException("Ukjent sivilstand.type")
        }

        return PdlDTO.Sivilstand(
            kodeRef = sivilstandKode.kodeRef,
            beskrivelse = sivilstandKode.beskrivelse,
            fraOgMed = gyldig ?: TPSNullDate
        )
    }

    private fun hentFamilierelasjon(person: HentPerson.Person): List<PdlDTO.Familierelasjon> {
        return person.forelderBarnRelasjon.map {
            PdlDTO.Familierelasjon(
                harSammeBosted = false, // TODO denne må vi finne ut av selv
                rolle = when (it.relatertPersonsRolle) {
                    HentPerson.ForelderBarnRelasjonRolle.BARN -> PdlDTO.Relasjonstype.BARN
                    HentPerson.ForelderBarnRelasjonRolle.MOR -> PdlDTO.Relasjonstype.MORA
                    HentPerson.ForelderBarnRelasjonRolle.FAR -> PdlDTO.Relasjonstype.FARA
                    HentPerson.ForelderBarnRelasjonRolle.MEDMOR -> PdlDTO.Relasjonstype.MORA
                    HentPerson.ForelderBarnRelasjonRolle.__UNKNOWN_VALUE -> throw IllegalStateException("Ukjent relasjonstype")
                },
                tilPerson = PdlDTO.Familierelasjon.Person(
                    // TODO Informasjon som trengs må hentes ut fra PDL i eget kall
                    navn = PdlDTO.Navn(null, "", "", ""),
                    alder = 0,
                    alderMåneder = 0,
                    alderManeder = 0,
                    fødselsnummer = "",
                    fodselsnummer = "",
                    personstatus = PdlDTO.Bostatus(
                        dødsdato = LocalDate.now(),
                        dodsdato = LocalDate.now(),
                        bostatus = PdlDTO.Kodeverk(kodeRef = "", beskrivelse = "")
                    ),
                    diskresjonskode = PdlDTO.Kodeverk(kodeRef = "", beskrivelse = "")
                )
            )
        }
    }

    private fun hentNavKontaktinformasjon(person: HentPerson.Person): PdlDTO.NavKontaktinformasjon? {
        // TODO Denne informasjonen finnes ikke i PDL
        return null
    }

    private fun hentDodsbo(person: HentPerson.Person): List<PdlDTO.Dodsbo> {
        return person.kontaktinformasjonForDoedsbo.map {
            PdlDTO.Dodsbo(
                adressat = hentDodsboAdressat(it),
                adresselinje1 = it.adresse.adresselinje1,
                adresselinje2 = it.adresse.adresselinje2,
                postnummer = it.adresse.postnummer,
                poststed = it.adresse.poststedsnavn,
                landkode = it.adresse.landkode,
                registrert = it.attestutstedelsesdato.value,
                skifteform = it.skifteform.name
            )
        }
    }

    private fun hentDodsboAdressat(dodsbo: HentPerson.KontaktinformasjonForDoedsbo): PdlDTO.Adressat {
        return PdlDTO.Adressat(
            advokatSomAdressat = hentAdvokatSomAdressat(dodsbo),
            organisasjonSomAdressat = hentOrganisasjonSomAdressat(dodsbo),
            kontaktpersonMedIdNummerSomAdressat = hentkontaktpersonMedIdNummerSomAdressat(dodsbo),
            kontaktpersonUtenIdNummerSomAdressat = hentkontaktpersonUtenIdNummerSomAdressat(dodsbo)
        )
    }

    private fun hentAdvokatSomAdressat(dodsbo: HentPerson.KontaktinformasjonForDoedsbo): PdlDTO.AdvokatSomAdressat? {
        val adressat = dodsbo.advokatSomKontakt ?: return null
        return PdlDTO.AdvokatSomAdressat(
            kontaktperson = PdlDTO.Navn(
                endringsinfo = null,
                fornavn = adressat.personnavn.fornavn,
                mellomnavn = adressat.personnavn.mellomnavn,
                etternavn = adressat.personnavn.etternavn
            ),
            organisasjonsnavn = adressat.organisasjonsnavn,
            organisasjonsnummer = adressat.organisasjonsnummer
        )
    }

    private fun hentOrganisasjonSomAdressat(dodsbo: HentPerson.KontaktinformasjonForDoedsbo): PdlDTO.OrganisasjonSomAdressat? {
        val adressat = dodsbo.organisasjonSomKontakt ?: return null
        return PdlDTO.OrganisasjonSomAdressat(
            kontaktperson = adressat.kontaktperson?.let {
                PdlDTO.Navn(
                    endringsinfo = null,
                    fornavn = it.fornavn,
                    mellomnavn = it.mellomnavn,
                    etternavn = it.etternavn
                )
            },
            organisasjonsnavn = adressat.organisasjonsnavn,
            organisasjonsnummer = adressat.organisasjonsnummer
        )
    }

    private fun hentkontaktpersonMedIdNummerSomAdressat(dodsbo: HentPerson.KontaktinformasjonForDoedsbo): PdlDTO.KontaktpersonMedId? {
        val addresat = dodsbo.personSomKontakt ?: return null
        val identifikasjonsnummer = addresat.identifikasjonsnummer
        return if (identifikasjonsnummer == null) {
            null
        } else {
            PdlDTO.KontaktpersonMedId(
                idNummer = identifikasjonsnummer,
                navn = addresat.personnavn?.let {
                    PdlDTO.Navn(
                        endringsinfo = null,
                        fornavn = it.fornavn,
                        mellomnavn = it.mellomnavn,
                        etternavn = it.etternavn
                    )
                }
            )
        }
    }

    private fun hentkontaktpersonUtenIdNummerSomAdressat(dodsbo: HentPerson.KontaktinformasjonForDoedsbo): PdlDTO.KontaktpersonUtenId? {
        val addresat = dodsbo.personSomKontakt ?: return null
        val identifikasjonsnummer = addresat.identifikasjonsnummer
        return if (identifikasjonsnummer != null) {
            null
        } else {
            PdlDTO.KontaktpersonUtenId(
                foedselsdato = addresat.foedselsdato?.value,
                fodselsdato = addresat.foedselsdato?.value,
                navn = addresat.personnavn?.let { PdlDTO.Navn(null, it.fornavn, it.mellomnavn, it.etternavn) }
            )
        }
    }

    private fun hentFullmakt(
        person: HentPerson.Person,
        relaterteNavn: Map<String, PdlDTO.Navn>
    ): List<PdlDTO.Fullmakt> {
        return person.fullmakt.map {
            val navn: String = relaterteNavn[it.motpartsPersonident]
                ?.sammensatt
                ?: "Fant ikke navn"

            PdlDTO.Fullmakt(
                motpartsRolle = it.motpartsRolle.name,
                motpartsPersonident = it.motpartsPersonident,
                motpartsPersonNavn = navn,
//                omrade = it.omraader.map { omraade -> standardKodeverk.getArkivtemaNavn(omraade) ?: omraade }
                omraade = it.omraader.map { omrade -> omrade }, // TODO her må vi bruke standardkodeverk
                gyldigFraOgMed = formatDate(it.gyldigFraOgMed.value),
                gyldigTilOgMed = formatDate(it.gyldigTilOgMed.value)
            )
        }
    }

    private fun hentTelefon(person: HentPerson.Person): List<PdlDTO.Telefon> {
        return person.telefonnummer
            .sortedBy { it.prioritet }
            .map {
                PdlDTO.Telefon(
                    retningsnummer = PdlDTO.Kodeverk(it.landskode, "Landskode"),
                    identifikator = it.nummer,
                    sistEndret = formatDate(it.metadata.endringer.first().registrert.value),
                    sistEndretAv = it.metadata.endringer.first().registrertAv,
                    prioritet = it.prioritet
                )
            }
    }

    private fun hentVerge(person: HentPerson.Person, relaterteNavn: Map<String, PdlDTO.Navn>): List<PdlDTO.Verge> {
        return person.vergemaalEllerFremtidsfullmakt
            .map {
                val motpartsNavn: PdlDTO.Navn? = relaterteNavn[it.vergeEllerFullmektig.motpartsPersonident]
                val navn = it.vergeEllerFullmektig.navn?.let { navn ->
                    PdlDTO.Navn(
                        endringsinfo = null,
                        fornavn = navn.fornavn,
                        mellomnavn = navn.mellomnavn,
                        etternavn = navn.etternavn
                    )
                }
                PdlDTO.Verge(
                    ident = it.vergeEllerFullmektig.motpartsPersonident,
                    navn = motpartsNavn ?: navn,
                    vergesakstype = it.type,
                    omfang = it.vergeEllerFullmektig.omfang,
                    embete = it.embete,
                    gyldighetstidspunkt = it.folkeregistermetadata?.gyldighetstidspunkt?.value?.format(DateTimeFormatter.ISO_DATE_TIME),
                    opphoerstidspunkt = it.folkeregistermetadata?.opphoerstidspunkt?.value?.format(DateTimeFormatter.ISO_DATE_TIME)
                )
            }
    }

    private fun hentForeldreansvar(
        person: HentPerson.Person,
        relaterteNavn: Map<String, PdlDTO.Navn>
    ): List<PdlDTO.Foreldreansvar> {
        return person.foreldreansvar.map {
            val ansvarligUtenId = it.ansvarligUtenIdentifikator?.navn?.let { navn ->
                PdlDTO.Navn(null, navn.fornavn, navn.mellomnavn, navn.etternavn)
            }
            PdlDTO.Foreldreansvar(
                ansvar = it.ansvar ?: "Kunne ikke hente type ansvar",
                ansvarlig = relaterteNavn[it.ansvarlig] ?: ansvarligUtenId,
                ansvarsubject = relaterteNavn[it.ansvarssubjekt]
            )
        }
    }

    private fun hentDeltBosted(person: HentPerson.Person): List<PdlDTO.DeltBosted> {
        return person.deltBosted.map {
            val postnummer = it.vegadresse?.postnummer ?: it.matrikkeladresse?.postnummer
            PdlDTO.DeltBosted(
                startdatoForKontrakt = it.startdatoForKontrakt.value.format(DateTimeFormatter.ISO_DATE),
                sluttdatoForKontrakt = it.sluttdatoForKontrakt?.value?.format(DateTimeFormatter.ISO_DATE),
                adresse = PdlDTO.DeltBostedAdresse(
                    adressenavn = it.vegadresse?.adressenavn,
                    husbokstav = it.vegadresse?.husbokstav,
                    husnummer = it.vegadresse?.husnummer,
                    bruksenhetsnummer = it.vegadresse?.bruksenhetsnummer ?: it.matrikkeladresse?.bruksenhetsnummer,
                    kommunenummer = it.vegadresse?.kommunenummer ?: it.matrikkeladresse?.kommunenummer,
                    postnummer = postnummer,
                    poststed = "TODO hentPoststed(postnummer)", // TODO Trenger kodeverk
                    bydelsnummer = it.vegadresse?.bydelsnummer,
                    tilleggsnavn = it.vegadresse?.tilleggsnavn ?: it.matrikkeladresse?.tilleggsnavn,
                    coAdressenavn = it.coAdressenavn
                ),
                ukjentBosted = PdlDTO.UkjentBosted(
                    bostedskommune = it.ukjentBosted?.bostedskommune
                )
            )
        }
    }

    private fun hentSikkerhetstiltak(person: HentPerson.Person): PdlDTO.Sikkerhetstiltak? {
        return person.sikkerhetstiltak.firstOrNull()
            ?.let {
                PdlDTO.Sikkerhetstiltak(
                    sikkerhetstiltakskode = it.tiltakstype,
                    sikkerhetstiltaksbeskrivelse = it.beskrivelse,
                    periode = PdlDTO.Periode(
                        fra = it.gyldigFraOgMed.value,
                        til = it.gyldigTilOgMed.value
                    )
                )
            }
    }
}
