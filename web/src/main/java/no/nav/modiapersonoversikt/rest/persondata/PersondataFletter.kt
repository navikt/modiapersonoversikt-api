package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.consumer.dkif.Dkif
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.ANNET
import no.nav.modiapersonoversikt.consumer.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.OFFENTLIG
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import no.nav.modiapersonoversikt.rest.persondata.Persondata.asNavnOgIdent
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bankkonto
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.LocalDate
import java.time.Period
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig as Kodeverk

val log: Logger = LoggerFactory.getLogger(PersondataFletter::class.java)

class PersondataFletter(val kodeverk: EnhetligKodeverk.Service) {
    data class Data(
        val personIdent: String,
        val persondata: HentPersondata.Person,
        val geografiskeTilknytning: PersondataResult<String?>,
        val erEgenAnsatt: PersondataResult<Boolean>,
        val navEnhet: PersondataResult<NorgDomain.EnhetKontaktinformasjon?>,
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
                    it.system.name
                } else {
                    null
                }
            }
        }
    }

    fun flettSammenData(data: Data, clock: Clock = Clock.systemDefaultZone()): Persondata.Data {
        val feilendeSystemer = data.feilendeSystemer().toMutableList()
        return Persondata.Data(
            feilendeSystemer = feilendeSystemer,
            person = Persondata.Person(
                fnr = data.personIdent,
                personIdent = data.personIdent,
                navn = hentNavn(data),
                kjonn = hentKjonn(data),
                fodselsdato = hentFodselsdato(data),
                geografiskTilknytning = hentGeografiskTilknytning(data),
                alder = hentAlder(data, clock),
                dodsdato = hentDodsdato(data),
                bostedAdresse = hentBostedAdresse(data),
                kontaktAdresse = hentKontaktAdresse(data),
                oppholdsAdresse = hentOppholdsAdresse(data),
                navEnhet = hentNavEnhet(data.navEnhet),
                statsborgerskap = hentStatsborgerskap(data),
                adressebeskyttelse = hentAdressebeskyttelse(data.persondata.adressebeskyttelse),
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
                bankkonto = hentBankkonto(data).fold(
                    onSuccess = { it },
                    onFailure = { system, cause ->
                        feilendeSystemer.add(system.name)
                        TjenestekallLogger.logger.error("Persondata feilet system: $system", cause)
                        null
                    }
                ),
                forelderBarnRelasjon = hentForelderBarnRelasjon(data)
            )
        )
    }

    private fun hentGeografiskTilknytning(data: Data): String? {
        return data.geografiskeTilknytning.getOrNull()
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

    private fun hentGyldighetsperiode(gyldigFraOgMed: HentPersondata.DateTime?, gyldigTilOgMed: HentPersondata.DateTime?): Persondata.GyldighetsPeriode? {
        if (gyldigFraOgMed == null && gyldigTilOgMed == null) {
            return null
        }
        return Persondata.GyldighetsPeriode(gyldigFraOgMed?.value?.toLocalDate(), gyldigTilOgMed?.value?.toLocalDate())
    }

    private fun hentGyldighetsperiode(gyldigFraOgMed: HentPersondata.Date?, gyldigTilOgMed: HentPersondata.Date?): Persondata.GyldighetsPeriode? {
        if (gyldigFraOgMed == null && gyldigTilOgMed == null) {
            return null
        }
        return Persondata.GyldighetsPeriode(gyldigFraOgMed?.value, gyldigTilOgMed?.value)
    }

    private fun hentBostedAdresse(data: Data): List<Persondata.Adresse> {
        return data.persondata.bostedsadresse.mapNotNull { adresse ->
            val sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
            val gyldighetsPeriode = hentGyldighetsperiode(adresse.gyldigFraOgMed, adresse.gyldigTilOgMed)
            when {
                adresse.vegadresse != null -> lagAdresseFraVegadresse(
                    adresse = adresse.vegadresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(
                    adresse = adresse.matrikkeladresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(
                    adresse = adresse.utenlandskAdresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.ukjentBosted != null -> Persondata.Adresse(
                    linje1 = adresse.ukjentBosted?.bostedskommune ?: "Ukjent kommune",
                    sistEndret = null,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                else -> {
                    TjenestekallLogger.warn(
                        "PersondataFletter",
                        mapOf(
                            "personIdent" to data.personIdent,
                            "feil" to "Ukjent bostedsadresse struktur",
                            "addresse" to adresse
                        )
                    )
                    null
                }
            }
        }.sortedByDescending { it.gyldighetsPeriode?.gyldigFraOgMed }
    }

    private fun hentKontaktAdresse(data: Data): List<Persondata.Adresse> {
        return data.persondata.kontaktadresse.mapNotNull { adresse ->
            val sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
            val gyldighetsPeriode = hentGyldighetsperiode(adresse.gyldigFraOgMed, adresse.gyldigTilOgMed)
            when {
                adresse.coAdressenavn.isNotNullOrBlank() && adresse.vegadresse != null -> {
                    kombinerCoAdressenavnOgVegadresse(
                        coAdressenavn = adresse.coAdressenavn!!,
                        vegadresse = lagAdresseFraVegadresse(adresse.vegadresse!!),
                        sisteEndring = sisteEndring,
                        gyldighetsPeriode = gyldighetsPeriode
                    )
                }
                adresse.coAdressenavn.isNotNullOrBlank() -> Persondata.Adresse(
                    linje1 = adresse.coAdressenavn!!,
                    sistEndret = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.vegadresse != null -> lagAdresseFraVegadresse(
                    adresse = adresse.vegadresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.postboksadresse != null -> lagAdresseFraPostboksadresse(
                    adresse = adresse.postboksadresse!!,
                    sistEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.postadresseIFrittFormat != null -> lagAdresseFraPostadresseIFrittFormat(
                    adresse = adresse.postadresseIFrittFormat!!,
                    sistEndret = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(
                    adresse = adresse.utenlandskAdresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.utenlandskAdresseIFrittFormat != null -> lagAdresseFraUtenlandskAdresseIFrittFormat(
                    adresse = adresse.utenlandskAdresseIFrittFormat!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                else -> {
                    TjenestekallLogger.warn(
                        "PersondataFletter",
                        mapOf(
                            "personIdent" to data.personIdent,
                            "feil" to "Ukjent kontaktadresse struktur",
                            "addresse" to adresse
                        )
                    )
                    null
                }
            }
        }.sortedByDescending { it.gyldighetsPeriode?.gyldigFraOgMed }
    }

    private fun hentOppholdsAdresse(data: Data): List<Persondata.Adresse> {
        return data.persondata.oppholdsadresse.mapNotNull { adresse ->
            val sisteEndring = hentSisteEndringFraMetadata(adresse.metadata)
            val gyldighetsPeriode = hentGyldighetsperiode(adresse.gyldigFraOgMed, adresse.gyldigTilOgMed)
            when {
                adresse.coAdressenavn.isNotNullOrBlank() && adresse.vegadresse != null -> {
                    kombinerCoAdressenavnOgVegadresse(
                        coAdressenavn = adresse.coAdressenavn!!,
                        vegadresse = lagAdresseFraVegadresse(
                            adresse = adresse.vegadresse!!
                        ),
                        sisteEndring = sisteEndring,
                        gyldighetsPeriode = gyldighetsPeriode
                    )
                }
                adresse.coAdressenavn.isNotNullOrBlank() -> Persondata.Adresse(
                    linje1 = adresse.coAdressenavn!!,
                    sistEndret = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.vegadresse != null -> lagAdresseFraVegadresse(
                    adresse = adresse.vegadresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(
                    adresse = adresse.matrikkeladresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(
                    adresse = adresse.utenlandskAdresse!!,
                    sisteEndring = sisteEndring,
                    gyldighetsPeriode = gyldighetsPeriode
                )
                else -> {
                    TjenestekallLogger.warn(
                        "PersondataFletter",
                        mapOf(
                            "personIdent" to data.personIdent,
                            "feil" to "Ukjent kontaktadresse struktur",
                            "addresse" to adresse
                        )
                    )
                    null
                }
            }
        }
    }

    private fun kombinerCoAdressenavnOgVegadresse(
        coAdressenavn: String,
        vegadresse: Persondata.Adresse,
        sisteEndring: Persondata.SistEndret?,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = coAdressenavn,
        linje2 = vegadresse.linje1,
        linje3 = vegadresse.linje2,
        sistEndret = sisteEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun lagAdresseFraPostadresseIFrittFormat(
        adresse: HentPersondata.PostadresseIFrittFormat,
        sistEndret: Persondata.SistEndret?,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = listOf(adresse.adresselinje1),
        linje2 = listOf(adresse.adresselinje2),
        linje3 = listOf(
            adresse.adresselinje3,
            adresse.postnummer,
            adresse.postnummer?.let { kodeverk.hentKodeverk(Kodeverk.POSTNUMMER).hentVerdi(it, it) }
        ),
        sistEndret = sistEndret,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun lagAdresseFraPostboksadresse(
        adresse: HentPersondata.Postboksadresse,
        sistEndring: Persondata.SistEndret?,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = listOf(adresse.postbokseier),
        linje2 = listOf("Postboks", adresse.postboks),
        linje3 = listOf(
            adresse.postnummer,
            adresse.postnummer?.let { kodeverk.hentKodeverk(Kodeverk.POSTNUMMER).hentVerdi(it, it) }
        ),
        sistEndret = sistEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun hentSisteEndringFraMetadata(metadata: HentPersondata.Metadata): Persondata.SistEndret? {
        return metadata.endringer.maxByOrNull { it.registrert.value }
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
        sisteEndring: Persondata.SistEndret? = null,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.bruksenhetsnummer,
            adresse.tilleggsnavn
        ),
        linje2 = listOf(
            adresse.postnummer,
            adresse.kommunenummer
        ),
        sistEndret = sisteEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun lagAdresseFraUtenlandskAdresse(
        adresse: HentPersondata.UtenlandskAdresse,
        sisteEndring: Persondata.SistEndret? = null,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
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
            kodeverk.hentKodeverk(Kodeverk.LAND).hentVerdi(adresse.landkode, adresse.landkode)
        ),
        sistEndret = sisteEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun lagAdresseFraUtenlandskAdresseIFrittFormat(
        adresse: HentPersondata.UtenlandskAdresseIFrittFormat,
        sisteEndring: Persondata.SistEndret? = null,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = listOf(adresse.adresselinje1),
        linje2 = listOf(adresse.adresselinje2),
        linje3 = listOf(
            adresse.adresselinje3,
            adresse.postkode,
            adresse.byEllerStedsnavn,
            kodeverk.hentKodeverk(Kodeverk.LAND).hentVerdi(adresse.landkode, adresse.landkode)
        ),
        sistEndret = sisteEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    private fun lagAdresseFraBesoksadresse(
        adresse: NorgDomain.Gateadresse
    ) = Persondata.Adresse(
        linje1 = listOfNotNull(
            adresse.gatenavn,
            adresse.husnummer,
            adresse.husbokstav
        ),
        linje2 = listOfNotNull(
            adresse.postnummer,
            adresse.poststed
        ),
        sistEndret = null
    )

    private fun lagAdresseFraVegadresse(
        adresse: HentPersondata.Vegadresse,
        sisteEndring: Persondata.SistEndret? = null,
        gyldighetsPeriode: Persondata.GyldighetsPeriode? = null
    ) = Persondata.Adresse(
        linje1 = listOf(
            adresse.adressenavn,
            adresse.husnummer,
            adresse.husbokstav,
            adresse.bruksenhetsnummer
        ),
        linje2 = listOf(
            adresse.postnummer,
            adresse.postnummer?.let { kodeverk.hentKodeverk(Kodeverk.POSTNUMMER).hentVerdi(it, it) }
        ),
        sistEndret = sisteEndring,
        gyldighetsPeriode = gyldighetsPeriode
    )

    fun hentNavEnhet(navEnhet: PersondataResult<NorgDomain.EnhetKontaktinformasjon?>): Persondata.Enhet? {
        return navEnhet
            .map {
                if (it == null) {
                    null
                } else {
                    Persondata.Enhet(it.enhet.enhetId, it.enhet.enhetNavn, hentPublikumsmottak(it.publikumsmottak))
                }
            }
            .getOrNull()
    }

    private fun hentPublikumsmottak(publikumsmottak: List<NorgDomain.Publikumsmottak>): List<Persondata.Publikumsmottak> {
        return publikumsmottak.map {
            Persondata.Publikumsmottak(
                besoksadresse = lagAdresseFraBesoksadresse(requireNotNull(it.besoksadresse)),
                apningstider = it.apningstider
                    .sortedBy { apningstid -> apningstid.ukedag }
                    .map { apningstid ->
                        Persondata.Apningstid(
                            ukedag = apningstid.ukedag.name,
                            apningstid = lagApningstidString(apningstid)
                        )
                    }
            )
        }
    }

    private fun lagApningstidString(apningstid: NorgDomain.Apningstid): String {
        return if (apningstid.stengt) {
            "Stengt"
        } else {
            val fra = apningstid.apentFra ?: "Ukjent"
            val til = apningstid.apentTil ?: "Ukjent"
            "$fra - $til"
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
                gyldighetsPeriode = hentGyldighetsperiode(it.gyldigFraOgMed, it.gyldigTilOgMed)
            )
        }
    }

    fun hentAdressebeskyttelse(adressebeskyttelse: List<HentPersondata.Adressebeskyttelse>): List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> {
        return adressebeskyttelse.map {
            val kodebeskrivelse = when (it.gradering) {
                STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG -> kodeverk.hentKodeBeskrivelse(
                    Kodeverk.DISKRESJONSKODER,
                    "SPSF"
                )
                FORTROLIG -> kodeverk.hentKodeBeskrivelse(Kodeverk.DISKRESJONSKODER, "SPFO")
                UGRADERT -> Persondata.KodeBeskrivelse("", "Ugradert")
                else -> Persondata.KodeBeskrivelse("", "Ukjent")
            }
            val gradering = when (it.gradering) {
                STRENGT_FORTROLIG_UTLAND -> Persondata.AdresseBeskyttelse.KODE6_UTLAND
                STRENGT_FORTROLIG -> Persondata.AdresseBeskyttelse.KODE6
                FORTROLIG -> Persondata.AdresseBeskyttelse.KODE7
                UGRADERT -> Persondata.AdresseBeskyttelse.UGRADERT
                else -> Persondata.AdresseBeskyttelse.UKJENT
            }
            Persondata.KodeBeskrivelse(kode = gradering, beskrivelse = kodebeskrivelse.beskrivelse)
        }
    }

    private fun hentSikkerhetstiltak(data: Data): List<Persondata.Sikkerhetstiltak> {
        return data.persondata.sikkerhetstiltak.map {
            Persondata.Sikkerhetstiltak(
                type = it.tiltakstype,
                beskrivelse = it.beskrivelse,
                gyldighetsPeriode = hentGyldighetsperiode(it.gyldigFraOgMed, it.gyldigTilOgMed)
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
            harSammeAdresse = harSammeAdresse(hentBostedAdresse(data).firstOrNull(), person.bostedAdresse.firstOrNull()),
            dodsdato = person.dodsdato
        )
    }

    private fun hentForeldreansvar(data: Data): List<Persondata.Foreldreansvar> {
        return data.persondata.foreldreansvar.map { forelderansvar ->
            val ansvarligUtenIdentifikatorNavn = forelderansvar.ansvarligUtenIdentifikator?.navn?.let(::hentNavn)
            val ansvarlig = data.tredjepartsPerson
                .map { it[forelderansvar.ansvarlig] }
                .map { it.asNavnOgIdent() }
                .getOrNull()
            val ansvarligsubject = data.tredjepartsPerson
                .map { it[forelderansvar.ansvarssubjekt] }
                .map { it.asNavnOgIdent() }
                .getOrNull()
            hentAnsvarlig(ansvarlig, ansvarligUtenIdentifikatorNavn)
            Persondata.Foreldreansvar(
                ansvar = forelderansvar.ansvar ?: "Kunne ikke hente type ansvar",
                ansvarlig = hentAnsvarlig(ansvarlig, ansvarligUtenIdentifikatorNavn),
                ansvarsubject = ansvarligsubject
            )
        }
    }

    private fun hentAnsvarlig(ansvarlig: Persondata.NavnOgIdent?, ansvarligUtenIdentifikatorNavn: Persondata.Navn?): Persondata.NavnOgIdent? {
        return if (ansvarlig == null && ansvarligUtenIdentifikatorNavn == null) {
            null
        } else {
            ansvarlig ?: Persondata.NavnOgIdent(
                navn = ansvarligUtenIdentifikatorNavn,
                ident = null
            )
        }
    }

    private fun hentDeltBosted(data: Data): List<Persondata.DeltBosted> {
        return data.persondata.deltBosted.map {
            Persondata.DeltBosted(
                gyldighetsPeriode = hentGyldighetsperiode(it.startdatoForKontrakt, it.sluttdatoForKontrakt),
                adresse = when {
                    it.vegadresse != null -> lagAdresseFraVegadresse(it.vegadresse!!)
                    it.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(it.matrikkeladresse!!)
                    it.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(it.utenlandskAdresse!!)
                    it.coAdressenavn.isNotNullOrBlank() -> Persondata.Adresse(
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
                gyldighetsPeriode = hentGyldighetsperiode(it.gyldigFraOgMed, it.gyldigTilOgMed)
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
                embete = hentVergemalEmbete(vergemal.embete),
                gyldighetsPeriode = hentGyldighetsperiode(vergemal.folkeregistermetadata?.gyldighetstidspunkt, vergemal.folkeregistermetadata?.opphoerstidspunkt)
            )
        }
    }

    private fun hentVergemalEmbete(embete: String?): String {
        return when (embete) {
            "fylkesmannenIOsloOgViken" -> "Fylkesmannen i Oslo og Viken"
            "fylkesmannenIVestfoldOgTelemark" -> "Fylkesmannen i Vestfold og Telemark"
            "fylkesmannenITromsOgFinnmark" -> "Fylkesmannen i Troms og Finnmark"
            "fylkesmannenINordland" -> "Fylkesmannen i Nordland"
            "fylkesmannenITroendelag" -> "Fylkesmannen i Trøndelag"
            "fylkesmannenIInnlandet" -> "Fylkesmannen i Innlandet"
            "fylkesmannenIMoereOgRomsdal" -> "Fylkesmannen i Møre og Romsdal"
            "fylkesmannenIRogaland" -> "Fylkesmannen i Rogaland"
            "fylkesmannenIVestland" -> "Fylkesmannen i Vestland"
            "fylkesmannenIAgder" -> "Fylkesmannen i Agder"
            "statsforvalterenIOsloOgViken" -> "Statsforvalteren i Oslo og Viken"
            "statsforvaltarenIVestfoldOgTelemark" -> "Statsforvaltaren i Vestfold og Telemark"
            "statsforvalterenITromsOgFinnmark" -> "Statsforvalteren i Troms og Finnmark"
            "statsforvalterenINordland" -> "Statsforvalteren i Nordland"
            "statsforvalterenITroendelag" -> "Statsforvalteren i Trøndelag"
            "statsforvalterenIInnlandet" -> "Statsforvalteren i Innlandet"
            "statsforvaltarenIMoereOgRomsdal" -> "Statsforvaltaren i Møre og Romsdal"
            "statsforvaltarenIRogaland" -> "Statsforvaltaren i Rogaland"
            "statsforvaltarenIVestland" -> "Statsforvaltaren i Vestland"
            "statsforvalterenIAgder" -> "Statsforvalteren i Agder"
            null -> "Ikke definert embete"
            else -> {
                log.warn("Ukjent embete: $embete")
                "Ukjent embete: $embete"
            }
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

    private fun hentBankkonto(data: Data): PersondataResult<Persondata.Bankkonto?> {
        return data.bankkonto
            .map(InformasjonElement.BANKKONTO) {
                if (it.person is Bruker) {
                    when (val bankkonto = (it.person as Bruker).bankkonto) {
                        is BankkontoNorge -> Persondata.Bankkonto(
                            kontonummer = bankkonto.bankkonto.bankkontonummer,
                            banknavn = bankkonto.bankkonto.banknavn,
                            sistEndret = hentSistEndretBankkonto(bankkonto)
                        )
                        is BankkontoUtland -> Persondata.Bankkonto(
                            kontonummer = bankkonto.bankkontoUtland.bankkontonummer,
                            banknavn = bankkonto.bankkontoUtland.banknavn,
                            sistEndret = hentSistEndretBankkonto(bankkonto),
                            bankkode = bankkonto.bankkontoUtland.bankkode,
                            swift = bankkonto.bankkontoUtland.swift,
                            landkode = kodeverk.hentKodeBeskrivelse(
                                Kodeverk.LAND,
                                bankkonto.bankkontoUtland.landkode.value
                            ),
                            adresse = Persondata.Adresse(
                                linje1 = bankkonto.bankkontoUtland.bankadresse.adresselinje1 ?: "Ukjent adresse",
                                linje2 = bankkonto.bankkontoUtland.bankadresse.adresselinje2,
                                linje3 = bankkonto.bankkontoUtland.bankadresse.adresselinje3,
                                sistEndret = null
                            ),
                            valuta = kodeverk.hentKodeBeskrivelse(
                                Kodeverk.VALUTA,
                                bankkonto.bankkontoUtland.valuta.value
                            )
                        )
                        else -> null
                    }
                } else {
                    null
                }
            }
    }

    private fun hentSistEndretBankkonto(bankkonto: Bankkonto): Persondata.SistEndret? {
        return if (bankkonto.endretAv != null && bankkonto.endringstidspunkt != null) {
            Persondata.SistEndret(
                ident = bankkonto.endretAv,
                tidspunkt = bankkonto.endringstidspunkt
                    .toGregorianCalendar()
                    .toZonedDateTime()
                    .toLocalDateTime(),
                system = ""
            )
        } else {
            null
        }
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
                dodsdato = tredjepartsPerson?.dodsdato ?: emptyList()
            )
        }.sortedBy { it.alder }
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

    private fun hentAlder(data: Data, clock: Clock): Int? {
        return data.persondata.foedsel.firstOrNull()?.foedselsdato
            ?.let {
                Period.between(it.value, LocalDate.now(clock)).years
            }
    }

    private fun String?.isNotNullOrBlank() = !this.isNullOrBlank()
}

fun <T> EnhetligKodeverk.Service.hentKodeBeskrivelse(
    kodeverkRef: EnhetligKodeverk.Kilde<String, String>,
    kodeRef: T
): Persondata.KodeBeskrivelse<T> {
    val kodeverk = this.hentKodeverk(kodeverkRef)
    val beskrivelse = kodeverk.hentVerdi(kodeRef.toString(), kodeRef.toString())
    return Persondata.KodeBeskrivelse(
        kode = kodeRef,
        beskrivelse = beskrivelse
    )
}
