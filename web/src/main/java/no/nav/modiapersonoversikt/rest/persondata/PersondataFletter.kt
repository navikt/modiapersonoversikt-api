package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.ANNET
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.OFFENTLIG
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import no.nav.modiapersonoversikt.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.rest.enhet.model.Gateadresse
import no.nav.modiapersonoversikt.rest.enhet.model.Klokkeslett
import no.nav.modiapersonoversikt.rest.enhet.model.Publikumsmottak
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import java.time.LocalDate
import java.time.Period
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig as Kodeverk

class PersondataFletter(val kodeverk: EnhetligKodeverk.Service) {
    data class Data(
        val persondata: HentPersondata.Person,
        val geografiskeTilknytning: PersondataResult<String?>,
        val erEgenAnsatt: PersondataResult<Boolean>,
        val navEnhet: PersondataResult<EnhetKontaktinformasjon>?,
        val dkifData: PersondataResult<Dkif.DigitalKontaktinformasjon>,
        val bankkonto: PersondataResult<HentPersonResponse>,
        val tredjepartsPerson: PersondataResult<Map<String, Persondata.TredjepartsPerson>>
    ) {
        private val ekstraDatapunker = listOf(
            geografiskeTilknytning,
            erEgenAnsatt,
            navEnhet,
            dkifData,
            bankkonto,
            tredjepartsPerson
        )

        fun feilendeSystemer(): List<String> {
            return ekstraDatapunker.mapNotNull {
                if (it is PersondataResult.Failure<*>) {
                    TjenestekallLogger.logger.error("Persondata feilet system: ${it.system}", it.exception)
                    it.system
                } else {
                    null
                }
            }
        }
    }

    fun flettSammenData(data: Data): Persondata.Data {
        return Persondata.Data(
            feilendeSystemer = data.feilendeSystemer(),
            person = Persondata.Person(
                fnr = hentFnr(data),
                navn = hentNavn(data),
                kjonn = hentKjonn(data),
                fodselsdato = hentFodselsdato(data),
                alder = hentAlder(data),
                dodsdato = hentDodsdato(data),
                bostedAdresse = hentBostedAdresse(data),
                kontaktAdresse = hentKontaktAdresse(data),
                navEnhet = hentNavEnhet(data),
                statsborgerskap = hentStatsborgerskap(data),
                adressebeskyttelse = hentAdressebeskyttelse(data),
                sikkerhetstiltak = hentSikkerhetstiltak(data),
                erEgenAnsatt = hentErEgenAnsatt(data),
                personstatus = hentPersonstatus(data),
                sivilstand = hentSivilstand(data),
                foreldreansvar = hentForeldreansvar(data),
                deltBosted = hentDeltBosted(data),
                dodsbo = hentDodsbo(data),
                fullmakt = hentFullmakt(data),
                vergemal = hentVergemal(data),
                tilrettelagtKommunikasjon = hentTilrettelagtKommunikasjon(data),
                telefonnummer = hentTelefonnummer(data),
                kontaktOgReservasjon = hentKontaktOgReservasjon(data),
                bankkonto = hentBankkonto(data),
                forelderBarnRelasjon = hentForelderBarnRelasjon(data)
            )
        )
    }

    private fun hentFnr(data: Data): String {
        return data.persondata.folkeregisteridentifikator
            .filter { it.type == "FNR" }
            .first { it.status == "I_BRUK" }
            .identifikasjonsnummer
    }

    private fun hentNavn(data: Data): List<Persondata.Navn> {
        return data.persondata.navn.map(::hentNavn)
    }

    private fun hentNavn(navn: HentPersondata.Navn): Persondata.Navn {
        return Persondata.Navn(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn
        )
    }

    private fun hentNavn(navn: HentPersondata.Personnavn): Persondata.Navn {
        return Persondata.Navn(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn
        )
    }

    private fun hentKjonn(data: Data): List<Persondata.KodeBeskrivelse<Persondata.Kjonn>> {
        return data.persondata.kjoenn.map { kjonn ->
            when (kjonn.kjoenn) {
                HentPersondata.KjoennType.MANN -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.M)
                HentPersondata.KjoennType.KVINNE -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.K)
                else -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.U)
            }
        }
    }

    private fun hentFodselsdato(data: Data): List<LocalDate> {
        return data.persondata.foedsel.mapNotNull { it.foedselsdato?.value }
    }

    private fun hentDodsdato(data: Data): List<LocalDate> {
        return data.persondata.doedsfall.mapNotNull { it.doedsdato?.value }
    }

    private fun hentBostedAdresse(data: Data): List<Persondata.Adresse> {
        return data.persondata.bostedsadresse.mapNotNull { adresse ->
            when {
                adresse.vegadresse != null -> lagAdresseFraVegadresse(
                    adresse = adresse.vegadresse!!,
                    sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
                )
                adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(
                    adresse = adresse.matrikkeladresse!!,
                    sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
                )
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(
                    adresse = adresse.utenlandskAdresse!!,
                    sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
                )
                adresse.ukjentBosted != null -> Persondata.Adresse(
                    linje1 = adresse.ukjentBosted?.bostedskommune ?: "Ukjent kommune",
                    sistEndret = null
                )
                else -> {
                    TjenestekallLogger.warn(
                        "PersondataFletter",
                        mapOf(
                            "fnr" to hentFnr(data),
                            "feil" to "Ukjent bostedsadresse struktur",
                            "addresse" to adresse
                        )
                    )
                    null
                }
            }
        }
    }

    private fun hentKontaktAdresse(data: Data): List<Persondata.Adresse> {
        return data.persondata.kontaktadresse.mapNotNull { adresse ->
            when {
                adresse.coAdressenavn != null -> Persondata.Adresse(
                    linje1 = adresse.coAdressenavn ?: "Ukjent kommune",
                    sistEndret = hentSisteEndringFraMetadata(adresse.metadata)
                )
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(
                    adresse = adresse.utenlandskAdresse!!,
                    sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
                )
                adresse.vegadresse != null -> lagAdresseFraVegadresse(
                    adresse = adresse.vegadresse!!,
                    sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
                )
                else -> {
                    TjenestekallLogger.warn(
                        "PersondataFletter",
                        mapOf(
                            "fnr" to hentFnr(data),
                            "feil" to "Ukjent kontaktadresse struktur",
                            "addresse" to adresse
                        )
                    )
                    null
                }
            }
        }
    }

    private fun hentSisteEndringFraMetadata(metadata: HentPersondata.Metadata): Persondata.SistEndret? {
        return metadata.endringer.maxBy { it.registrert.value }
            ?.let {
                Persondata.SistEndret(
                    ident = it.registrertAv,
                    tidspunkt = it.registrert.value,
                    system = it.systemkilde
                )
            }
    }

    private fun lagAdresseFraMatrikkeladresse(
        adresse: HentPersondata.Matrikkeladresse,
        sisteEndring: Persondata.SistEndret? = null
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.bruksenhetsnummer,
            adresse.tilleggsnavn
        ),
        linje2 = listOf(
            adresse.postnummer,
            adresse.kommunenummer
        ),
        sistEndret = sisteEndring
    )

    private fun lagAdresseFraUtenlandskAdresse(
        adresse: HentPersondata.UtenlandskAdresse,
        sisteEndring: Persondata.SistEndret? = null
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.postboksNummerNavn,
            adresse.adressenavnNummer,
            adresse.bygningEtasjeLeilighet
        ),
        linje2 = listOf(
            adresse.postkode,
            adresse.bySted,
            adresse.regionDistriktOmraade
        ),
        linje3 = listOf(
            kodeverk.hentKodeverk(Kodeverk.LAND).hentBeskrivelse(adresse.landkode)
        ),
        sistEndret = sisteEndring
    )

    private fun lagAdresseFraBesoksadresse(
        adresse: Gateadresse
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.gatenavn,
            adresse.husnummer,
            adresse.husbokstav
        ),
        linje2 = listOf(
            adresse.postnummer,
            adresse.poststed
        ),
        sistEndret = null
    )

    private fun lagAdresseFraVegadresse(
        adresse: HentPersondata.Vegadresse,
        sisteEndring: Persondata.SistEndret? = null
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.adressenavn,
            adresse.husnummer,
            adresse.husbokstav,
            adresse.bruksenhetsnummer
        ),
        linje2 = listOf(
            adresse.postnummer,
            adresse.postnummer?.let { kodeverk.hentKodeverk(Kodeverk.POSTNUMMER).hentBeskrivelse(it) }
        ),
        linje3 = listOf(
            adresse.bydelsnummer,
            adresse.kommunenummer
        ),
        sistEndret = sisteEndring
    )

    private fun hentNavEnhet(data: Data): Persondata.Enhet? {
        return data.navEnhet
            ?.map { Persondata.Enhet(it.enhetId, it.enhetNavn, hentPublikumsmottak(it.publikumsmottak)) }
            ?.getOrNull()
    }

    private fun hentPublikumsmottak(publikumsmottak: List<Publikumsmottak>): List<Persondata.Publikumsmottak> {
        return publikumsmottak.map {
            Persondata.Publikumsmottak(
                besoksadresse = lagAdresseFraBesoksadresse(it.besoksadresse),
                apningstider = it.apningstider.map { apningstid ->
                    Persondata.Apningstid(
                        ukedag = apningstid.ukedag,
                        apningstid = lagApningstid(apningstid.apentFra, apningstid.apentTil)
                    )
                }
            )
        }
    }

    private fun lagApningstid(apentFra: Klokkeslett, apentTil: Klokkeslett): String {
        return "${lagTidspunkt(apentFra)} - ${lagTidspunkt(apentTil)}"
    }

    private fun lagTidspunkt(tid: Klokkeslett): String {
        return if (tid.time == null || tid.minutt == null) {
            "Ukjent"
        } else {
            "${tid.time.padStart(2, '0')}.${tid.minutt.padStart(2, '0')}"
        }
    }

    private fun hentStatsborgerskap(data: Data): List<Persondata.Statsborgerskap> {
        return data.persondata.statsborgerskap.map {
            val land = when (it.land) {
                "XUK" -> Persondata.KodeBeskrivelse("XUK", "Ukjent")
                else -> kodeverk.hentKodeBeskrivelse(Kodeverk.LAND, it.land)
            }
            Persondata.Statsborgerskap(
                land = land,
                gyldigFraOgMed = it.gyldigFraOgMed?.value,
                gyldigTilOgMed = it.gyldigTilOgMed?.value
            )
        }
    }

    private fun hentAdressebeskyttelse(data: Data): List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> {
        return data.persondata.adressebeskyttelse.map {
            val kodebeskrivelse = when (it.gradering) {
                STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG -> kodeverk.hentKodeBeskrivelse(
                    Kodeverk.DISKRESJONSKODER,
                    "SPSF"
                )
                FORTROLIG -> kodeverk.hentKodeBeskrivelse(Kodeverk.DISKRESJONSKODER, "SPFO")
                UGRADERT -> Persondata.KodeBeskrivelse("", "Ugradert")
                else -> Persondata.KodeBeskrivelse("", "Ukjent")
            }
            val adressebeskyttelse = when (it.gradering) {
                STRENGT_FORTROLIG_UTLAND -> Persondata.AdresseBeskyttelse.KODE6_UTLAND
                STRENGT_FORTROLIG -> Persondata.AdresseBeskyttelse.KODE6
                FORTROLIG -> Persondata.AdresseBeskyttelse.KODE7
                UGRADERT -> Persondata.AdresseBeskyttelse.UGRADERT
                else -> Persondata.AdresseBeskyttelse.UKJENT
            }
            Persondata.KodeBeskrivelse(kode = adressebeskyttelse, beskrivelse = kodebeskrivelse.beskrivelse)
        }
    }

    private fun hentSikkerhetstiltak(data: Data): List<Persondata.Sikkerhetstiltak> {
        return data.persondata.sikkerhetstiltak.map {
            Persondata.Sikkerhetstiltak(
                type = it.tiltakstype,
                beskrivelse = it.beskrivelse,
                gyldigFraOgMed = it.gyldigFraOgMed.value,
                gyldigTilOgMed = it.gyldigTilOgMed.value
            )
        }
    }

    private fun hentErEgenAnsatt(data: Data): Persondata.EgenAnsatt {
        return data.erEgenAnsatt
            .map {
                if (it) {
                    Persondata.EgenAnsatt.JA
                } else {
                    Persondata.EgenAnsatt.NEI
                }
            }.getOrElse(Persondata.EgenAnsatt.UKJENT)
    }

    private fun hentPersonstatus(data: Data): List<Persondata.KodeBeskrivelse<Persondata.PersonStatus>> {
        return data.persondata.folkeregisterpersonstatus.map {
            val tpsKode = when (it.status) {
                "bosatt" -> Persondata.PersonStatus.BOSATT
                "doed" -> Persondata.PersonStatus.DOD
                "opphoert" -> Persondata.PersonStatus.OPPHORT
                "inaktiv" -> Persondata.PersonStatus.INAKTIV
                "midlertidig" -> Persondata.PersonStatus.MIDLERTIDIG
                "forsvunnet" -> Persondata.PersonStatus.FORSVUNNET
                "utflyttet" -> Persondata.PersonStatus.UTFLYTTET
                "ikkeBosatt" -> Persondata.PersonStatus.IKKE_BOSATT
                "foedselsregistrert" -> Persondata.PersonStatus.FODSELSREGISTERT
                else -> Persondata.PersonStatus.UKJENT
            }
            val beskrivelse = kodeverk.hentKodeBeskrivelse(Kodeverk.PERSONSTATUSER, tpsKode.tpsKode)
            Persondata.KodeBeskrivelse(tpsKode, beskrivelse.beskrivelse)
        }
    }

    private fun hentSivilstand(data: Data): List<Persondata.Sivilstand> {
        return data.persondata.sivilstand.map { sivilstand ->
            val tpsKode = when (sivilstand.type) {
                HentPersondata.Sivilstandstype.UOPPGITT -> Persondata.SivilstandType.UOPPGITT
                HentPersondata.Sivilstandstype.UGIFT -> Persondata.SivilstandType.UGIFT
                HentPersondata.Sivilstandstype.GIFT -> Persondata.SivilstandType.GIFT
                HentPersondata.Sivilstandstype.ENKE_ELLER_ENKEMANN -> Persondata.SivilstandType.ENKE_ELLER_ENKEMANN
                HentPersondata.Sivilstandstype.SKILT -> Persondata.SivilstandType.SKILT
                HentPersondata.Sivilstandstype.SEPARERT -> Persondata.SivilstandType.SEPARERT
                HentPersondata.Sivilstandstype.REGISTRERT_PARTNER -> Persondata.SivilstandType.REGISTRERT_PARTNER
                HentPersondata.Sivilstandstype.SEPARERT_PARTNER -> Persondata.SivilstandType.SEPARERT_PARTNER
                HentPersondata.Sivilstandstype.SKILT_PARTNER -> Persondata.SivilstandType.SKILT_PARTNER
                HentPersondata.Sivilstandstype.GJENLEVENDE_PARTNER -> Persondata.SivilstandType.GJENLEVENDE_PARTNER
                else -> Persondata.SivilstandType.UOPPGITT
            }
            val beskrivelse = kodeverk.hentKodeBeskrivelse(Kodeverk.SIVILSTAND, tpsKode.tpsKode)
            val kodebeskrivelse = Persondata.KodeBeskrivelse(tpsKode, beskrivelse.beskrivelse)

            Persondata.Sivilstand(
                type = kodebeskrivelse,
                gyldigFraOgMed = sivilstand.gyldigFraOgMed?.value,
                sivilstandRelasjon = hentSivilstandRelasjon(data, sivilstand.relatertVedSivilstand)
            )
        }
    }

    private fun hentSivilstandRelasjon(data: Data, relatertVedSivilstand: String?): Persondata.SivilstandRelasjon? {
        val person = data.tredjepartsPerson.map { it[relatertVedSivilstand] }.getOrNull() ?: return null

        return Persondata.SivilstandRelasjon(
            fnr = person.fnr,
            navn = person.navn,
            alder = person.alder,
            adressebeskyttelse = person.adressebeskyttelse,
            harSammeAdresse = harSammeAdresse(hentBostedAdresse(data).firstOrNull(), person.bostedAdresse.firstOrNull())
        )
    }

    private fun hentForeldreansvar(data: Data): List<Persondata.Foreldreansvar> {
        return data.persondata.foreldreansvar.map { forelderansvar ->
            val ansvarligUtenNavn = forelderansvar.ansvarligUtenIdentifikator?.navn?.let(::hentNavn)
            val ansvarlig = data.tredjepartsPerson.map { it[forelderansvar.ansvar] }.getOrNull()
            val ansvarligsubject = data.tredjepartsPerson.map { it[forelderansvar.ansvarssubjekt] }.getOrNull()
            Persondata.Foreldreansvar(
                ansvar = forelderansvar.ansvar ?: "Kunne ikke hente type ansvar",
                ansvarlig = ansvarlig?.navn?.firstOrNull() ?: ansvarligUtenNavn,
                ansvarsubject = ansvarligsubject?.navn?.firstOrNull()
            )
        }
    }

    private fun hentDeltBosted(data: Data): List<Persondata.DeltBosted> {
        return data.persondata.deltBosted.map {
            Persondata.DeltBosted(
                startdatoForKontrakt = it.startdatoForKontrakt.value,
                sluttdatoForKontrakt = it.sluttdatoForKontrakt?.value,
                adresse = when {
                    it.vegadresse != null -> lagAdresseFraVegadresse(it.vegadresse!!)
                    it.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(it.matrikkeladresse!!)
                    it.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(it.utenlandskAdresse!!)
                    it.coAdressenavn != null -> Persondata.Adresse(
                        linje1 = it.coAdressenavn!!,
                        sistEndret = null
                    )
                    it.ukjentBosted != null -> Persondata.Adresse(
                        linje1 = it.ukjentBosted?.bostedskommune ?: "Ukjent kommune",
                        sistEndret = null
                    )
                    else -> null
                }
            )
        }
    }

    private fun hentDodsbo(data: Data): List<Persondata.Dodsbo> {
        return data.persondata.kontaktinformasjonForDoedsbo.map { dodsbo ->
            Persondata.Dodsbo(
                adressat = hentAdressat(dodsbo, data.tredjepartsPerson),
                adresse = hentAdresse(dodsbo.adresse),
                registrert = dodsbo.attestutstedelsesdato.value,
                skifteform = when (dodsbo.skifteform) {
                    OFFENTLIG -> Persondata.Skifteform.OFFENTLIG
                    ANNET -> Persondata.Skifteform.ANNET
                    else -> Persondata.Skifteform.UKJENT
                },
                sistEndret = hentSisteEndringFraMetadata(dodsbo.metadata)
            )
        }
    }

    private fun hentAdressat(
        dodsbo: HentPersondata.KontaktinformasjonForDoedsbo,
        tredjepartsPerson: PersondataResult<Map<String, Persondata.TredjepartsPerson>>
    ): Persondata.Adressat {
        return Persondata.Adressat(
            advokatSomAdressat = hentAdvokatSomAdressat(dodsbo),
            personSomAdressat = hentPersonSomAdressat(dodsbo, tredjepartsPerson),
            organisasjonSomAdressat = hentOrganisasjonSomAdressat(dodsbo)
        )
    }

    private fun hentAdvokatSomAdressat(dodsbo: HentPersondata.KontaktinformasjonForDoedsbo): Persondata.AdvokatSomAdressat? {
        val adressat = dodsbo.advokatSomKontakt ?: return null
        return Persondata.AdvokatSomAdressat(
            kontaktperson = hentNavn(adressat.personnavn),
            organisasjonsnavn = adressat.organisasjonsnavn,
            organisasjonsnummer = adressat.organisasjonsnummer
        )
    }

    private fun hentPersonSomAdressat(
        dodsbo: HentPersondata.KontaktinformasjonForDoedsbo,
        tredjepartsPerson: PersondataResult<Map<String, Persondata.TredjepartsPerson>>
    ): Persondata.PersonSomAdressat? {
        val adressat = dodsbo.personSomKontakt ?: return null
        val adressatPerson = tredjepartsPerson.map { it[adressat.identifikasjonsnummer] }.getOrNull()
        return Persondata.PersonSomAdressat(
            fnr = adressat.identifikasjonsnummer,
            navn = adressatPerson?.navn ?: emptyList(),
            fodselsdato = adressat.foedselsdato?.value
        )
    }

    private fun hentOrganisasjonSomAdressat(dodsbo: HentPersondata.KontaktinformasjonForDoedsbo): Persondata.OrganisasjonSomAdressat? {
        val adressat = dodsbo.organisasjonSomKontakt ?: return null
        return Persondata.OrganisasjonSomAdressat(
            kontaktperson = adressat.kontaktperson?.let(::hentNavn),
            organisasjonsnavn = adressat.organisasjonsnavn,
            organisasjonsnummer = adressat.organisasjonsnummer
        )
    }

    private fun hentAdresse(adresse: HentPersondata.KontaktinformasjonForDoedsboAdresse): Persondata.Adresse {
        val sisteLinje = listOf(adresse.postnummer, adresse.poststedsnavn, adresse.landkode)
        return Persondata.Adresse(
            linje1 = listOf(adresse.adresselinje1),
            linje2 = if (adresse.adresselinje2 == null) sisteLinje else listOf(adresse.adresselinje2),
            linje3 = if (adresse.adresselinje2 == null) null else sisteLinje,
            sistEndret = null
        )
    }

    private fun hentFullmakt(data: Data): List<Persondata.Fullmakt> {
        return data.persondata.fullmakt.map {
            val navn = data.tredjepartsPerson
                .map { personer -> personer[it.motpartsPersonident]?.navn }
                .getOrNull()

            Persondata.Fullmakt(
                motpartsPersonident = it.motpartsPersonident,
                motpartsPersonNavn = navn?.firstOrNull() ?: Persondata.Navn.UKJENT,
                motpartsRolle = when (it.motpartsRolle) {
                    HentPersondata.FullmaktsRolle.FULLMAKTSGIVER -> Persondata.FullmaktsRolle.FULLMAKTSGIVER
                    HentPersondata.FullmaktsRolle.FULLMEKTIG -> Persondata.FullmaktsRolle.FULLMEKTIG
                    else -> Persondata.FullmaktsRolle.UKJENT
                },
                omrade = hentOmrade(it.omraader),
                gyldigFraOgMed = it.gyldigFraOgMed.value,
                gyldigTilOgMed = it.gyldigTilOgMed.value
            )
        }
    }

    private fun hentOmrade(omraader: List<String>): List<Persondata.KodeBeskrivelse<String>> {
        return omraader.map { omrade -> kodeverk.hentKodeBeskrivelse(Kodeverk.TEMA, omrade) }
    }

    private fun hentVergemal(data: Data): List<Persondata.Verge> {
        return data.persondata.vergemaalEllerFremtidsfullmakt.map { vergemal ->
            val motpart = data.tredjepartsPerson.map { personer ->
                personer[vergemal.vergeEllerFullmektig.motpartsPersonident]?.navn
            }.getOrNull()
            val navn = vergemal.vergeEllerFullmektig.navn?.let(::hentNavn)

            Persondata.Verge(
                ident = vergemal.vergeEllerFullmektig.motpartsPersonident,
                navn = motpart?.firstOrNull() ?: navn,
                vergesakstype = hentVergemalType(vergemal.type),
                omfang = hentVergemalOmfang(vergemal.vergeEllerFullmektig.omfang),
                embete = vergemal.embete,
                gyldighetstidspunkt = vergemal.folkeregistermetadata?.gyldighetstidspunkt?.value?.toLocalDate(),
                opphorstidspunkt = vergemal.folkeregistermetadata?.opphoerstidspunkt?.value?.toLocalDate()
            )
        }
    }

    private fun hentVergemalOmfang(omfang: String?): String {
        return when (omfang) {
            "utlendingssakerPersonligeOgOekonomiskeInteresser" ->
                "Ivareta personens interesser innenfor det personlige og økonomiske området herunder utlendingssaken (kun for EMA)"
            "personligeOgOekonomiskeInteresser" -> "Ivareta personens interesser innenfor det personlige og økonomiske området"
            "oekonomiskeInteresser" -> "Ivareta personens interesser innenfor det økonomiske området"
            "personligeInteresser" -> "Ivareta personens interesser innenfor det personlige området"
            else -> "Ikke oppgitt"
        }
    }

    private fun hentVergemalType(type: String?): String {
        return when (type) {
            "ensligMindreaarigAsylsoeker" -> "Enslig mindreårig asylsøker"
            "ensligMindreaarigFlyktning" -> "Enslig mindreårig flyktning inklusive midlertidige saker for denne gruppen"
            "voksen" -> "Voksen"
            "midlertidigForVoksen" -> "Voksen midlertidig"
            "mindreaarig" -> "Mindreårig (unntatt EMF)"
            "midlertidigForMindreaarig" -> "Mindreårig midlertidig (unntatt EMF)"
            "forvaltningUtenforVergemaal" -> "Forvaltning utenfor vergemål"
            "stadfestetFremtidsfullmakt" -> "Fremtidsfullmakt"
            else -> "Ingen vergesakstype oppgitt"
        }
    }

    private fun hentTilrettelagtKommunikasjon(data: Data): Persondata.TilrettelagtKommunikasjon {
        val talesprak: MutableList<Persondata.KodeBeskrivelse<String>> = mutableListOf()
        val tegnsprak: MutableList<Persondata.KodeBeskrivelse<String>> = mutableListOf()

        data.persondata.tilrettelagtKommunikasjon.map {
            if (it.talespraaktolk != null && it.talespraaktolk!!.spraak != null) {
                talesprak.add(kodeverk.hentKodeBeskrivelse(Kodeverk.SPRAK, it.talespraaktolk!!.spraak!!))
            }
            if (it.tegnspraaktolk != null && it.tegnspraaktolk!!.spraak != null) {
                tegnsprak.add(kodeverk.hentKodeBeskrivelse(Kodeverk.SPRAK, it.tegnspraaktolk!!.spraak!!))
            }
        }

        return Persondata.TilrettelagtKommunikasjon(
            talesprak = talesprak,
            tegnsprak = tegnsprak
        )
    }

    private fun hentTelefonnummer(data: Data): List<Persondata.Telefon> {
        return data.persondata.telefonnummer.map {
            val sisteEndring = hentSisteEndringFraMetadata(it.metadata)
            Persondata.Telefon(
                retningsnummer = kodeverk.hentKodeBeskrivelse(Kodeverk.RETNINGSNUMRE, it.landskode),
                identifikator = it.nummer,
                sistEndret = sisteEndring
            )
        }
    }

    private fun hentKontaktOgReservasjon(data: Data): Dkif.DigitalKontaktinformasjon? {
        return data.dkifData.getOrNull()
    }

    private fun hentBankkonto(data: Data): Persondata.Bankkonto? {
        return data.bankkonto
            .map {
                if (it.person is Bruker) {
                    when (val bankkonto = (it.person as Bruker).bankkonto) {
                        is BankkontoNorge -> Persondata.Bankkonto(
                            kontonummer = bankkonto.bankkonto.bankkontonummer,
                            banknavn = bankkonto.bankkonto.banknavn,
                            sistEndret = Persondata.SistEndret(
                                ident = bankkonto.endretAv,
                                tidspunkt = bankkonto.endringstidspunkt
                                    .toGregorianCalendar()
                                    .toZonedDateTime()
                                    .toLocalDateTime(),
                                system = ""
                            )
                        )
                        is BankkontoUtland -> Persondata.Bankkonto(
                            kontonummer = bankkonto.bankkontoUtland.bankkontonummer,
                            banknavn = bankkonto.bankkontoUtland.banknavn,
                            sistEndret = Persondata.SistEndret(
                                ident = bankkonto.endretAv,
                                tidspunkt = bankkonto.endringstidspunkt
                                    .toGregorianCalendar()
                                    .toZonedDateTime()
                                    .toLocalDateTime(),
                                system = ""
                            ),
                            bankkode = bankkonto.bankkontoUtland.bankkode,
                            swift = bankkonto.bankkontoUtland.swift,
                            landkode = kodeverk.hentKodeBeskrivelse(
                                Kodeverk.LAND,
                                bankkonto.bankkontoUtland.landkode.kodeRef
                            ),
                            adresse = Persondata.Adresse(
                                linje1 = bankkonto.bankkontoUtland.bankadresse.adresselinje1 ?: "Ukjent adresse",
                                linje2 = bankkonto.bankkontoUtland.bankadresse.adresselinje2,
                                linje3 = bankkonto.bankkontoUtland.bankadresse.adresselinje3,
                                sistEndret = null
                            ),
                            valuta = kodeverk.hentKodeBeskrivelse(
                                Kodeverk.VALUTA,
                                bankkonto.bankkontoUtland.valuta.kodeRef
                            )
                        )
                        else -> null
                    }
                } else {
                    null
                }
            }
            .getOrNull()
    }

    private fun hentForelderBarnRelasjon(data: Data): List<Persondata.ForelderBarnRelasjon> {
        return data.persondata.forelderBarnRelasjon.map { relasjon ->
            val tredjepartsPerson = data.tredjepartsPerson.map { it[relasjon.relatertPersonsIdent] }.getOrNull()
            Persondata.ForelderBarnRelasjon(
                ident = tredjepartsPerson?.fnr ?: "",
                rolle = when (relasjon.relatertPersonsRolle) {
                    HentPersondata.ForelderBarnRelasjonRolle.MOR -> Persondata.ForelderBarnRelasjonRolle.MOR
                    HentPersondata.ForelderBarnRelasjonRolle.FAR -> Persondata.ForelderBarnRelasjonRolle.FAR
                    HentPersondata.ForelderBarnRelasjonRolle.MEDMOR -> Persondata.ForelderBarnRelasjonRolle.MEDMOR
                    HentPersondata.ForelderBarnRelasjonRolle.BARN -> Persondata.ForelderBarnRelasjonRolle.BARN
                    else -> Persondata.ForelderBarnRelasjonRolle.UKJENT
                },
                navn = tredjepartsPerson?.navn ?: emptyList(),
                fodselsdato = tredjepartsPerson?.fodselsdato ?: emptyList(),
                alder = tredjepartsPerson?.alder,
                kjonn = tredjepartsPerson?.kjonn ?: emptyList(),
                adressebeskyttelse = tredjepartsPerson?.adressebeskyttelse ?: emptyList(),
                harSammeAdresse = harSammeAdresse(
                    personAdresse = hentBostedAdresse(data).firstOrNull(),
                    tredjepartsPersonAdresse = tredjepartsPerson?.bostedAdresse?.firstOrNull()
                ),
                personstatus = tredjepartsPerson?.personstatus ?: emptyList()
            )
        }
    }

    private fun harSammeAdresse(
        personAdresse: Persondata.Adresse?,
        tredjepartsPersonAdresse: Persondata.Adresse?
    ): Boolean {
        if (personAdresse == null || tredjepartsPersonAdresse == null) {
            return false
        }
        return (personAdresse.linje1 == tredjepartsPersonAdresse.linje1) &&
            (personAdresse.linje2 == tredjepartsPersonAdresse.linje2) &&
            (personAdresse.linje3 == tredjepartsPersonAdresse.linje3)
    }

    private fun hentAlder(data: Data): Int? {
        return data.persondata.foedsel.firstOrNull()?.foedselsdato
            ?.let {
                Period.between(it.value, LocalDate.now()).years
            }
    }
}

fun <T> EnhetligKodeverk.Service.hentKodeBeskrivelse(
    kodeverkRef: Kodeverk,
    kodeRef: T
): Persondata.KodeBeskrivelse<T> {
    val kodeverk = this.hentKodeverk(kodeverkRef)
    val beskrivelse = kodeverk.hentBeskrivelse(kodeRef.toString())
    return Persondata.KodeBeskrivelse(
        kode = kodeRef,
        beskrivelse = beskrivelse
    )
}
