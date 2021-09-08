package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.ANNET
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.KontaktinformasjonForDoedsboSkifteform.OFFENTLIG
import no.nav.modiapersonoversikt.legacy.api.utils.TjenestekallLogger
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoNorge
import no.nav.tjeneste.virksomhet.person.v3.informasjon.BankkontoUtland
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import org.slf4j.LoggerFactory
import java.time.LocalDate

// TODO TEMP KODEVERK
interface KodeverkKilde
class FelleskodeverkKilde(val kodeverk: String) : KodeverkKilde
enum class Kodeverk(val kilde: KodeverkKilde) {
    KJONN(FelleskodeverkKilde("Kjønnstyper")),
    LAND(FelleskodeverkKilde("Landkoder")),
    SPRAK(FelleskodeverkKilde("Språk")),
    RETNINGSNUMRE(FelleskodeverkKilde("Retningsnumre")),
    POSTNUMMER(FelleskodeverkKilde("Postnumre")),
    PERSONSTATUSER(FelleskodeverkKilde("Personstatuser")),
    SIVILSTAND(FelleskodeverkKilde("Personstatuser")),
    DISKRESJONSKODER(FelleskodeverkKilde("Diskresjonskoder")),
    VALUTA(FelleskodeverkKilde("Valutaer"))
}

interface KodeverkService {
    fun hentVerdi(kodeverk: Kodeverk, kodeterm: String, sprak: String = "nb"): String
}

class PersondataFletter(val kodeverk: KodeverkService) {
    private val log = LoggerFactory.getLogger(PersondataFletter::class.java)

    data class Data(
        val persondata: HentPersondata.Person,
        val geografiskeTilknytning: Persondata.Result<String?>,
        val erEgenAnsatt: Persondata.Result<Boolean>,
        val navEnhet: Persondata.Result<AnsattEnhet>,
        val dkifData: Persondata.Result<Dkif.DigitalKontaktinformasjon>,
        val bankkonto: Persondata.Result<HentPersonResponse>,
        val tredjepartsPerson: Persondata.Result<Map<String, Persondata.TredjepartsPerson>>
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
                if (it is Persondata.Result.Failure<*>) {
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
                bankkonto = hentBankkonto(data)
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
                adresse.vegadresse != null -> lagAdresseFraVegadresse(adresse.vegadresse!!)
                adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(adresse.matrikkeladresse!!)
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(adresse.utenlandskAdresse!!)
                adresse.ukjentBosted != null -> Persondata.Adresse(
                    adresse.ukjentBosted?.bostedskommune ?: "Ukjent kommune"
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
                adresse.coAdressenavn != null -> Persondata.Adresse(adresse.coAdressenavn ?: "Ukjent kommune")
                adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(adresse.utenlandskAdresse!!)
                adresse.vegadresse != null -> lagAdresseFraVegadresse(adresse.vegadresse!!)
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

    private fun lagAdresseFraMatrikkeladresse(adresse: HentPersondata.Matrikkeladresse) =
        Persondata.Adresse(
            linje1 = listOf(
                adresse.bruksenhetsnummer,
                adresse.tilleggsnavn
            ),
            linje2 = listOf(
                adresse.postnummer,
                adresse.kommunenummer
            )
        )

    private fun lagAdresseFraUtenlandskAdresse(adresse: HentPersondata.UtenlandskAdresse) =
        Persondata.Adresse(
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
                kodeverk.hentVerdi(Kodeverk.LAND, adresse.landkode)
            )
        )

    private fun lagAdresseFraVegadresse(adresse: HentPersondata.Vegadresse) =
        Persondata.Adresse(
            linje1 = listOf(
                adresse.adressenavn,
                adresse.husnummer,
                adresse.husbokstav,
                adresse.bruksenhetsnummer
            ),
            linje2 = listOf(
                adresse.postnummer,
                adresse.postnummer?.let { kodeverk.hentVerdi(Kodeverk.POSTNUMMER, it) }
            ),
            linje3 = listOf(
                adresse.bydelsnummer,
                adresse.kommunenummer
            )
        )

    private fun hentNavEnhet(data: Data): Persondata.Enhet? {
        return data.navEnhet
            .map { Persondata.Enhet(it.enhetId, it.enhetNavn) }
            .getOrNull()
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
                FORTROLIG -> kodeverk.hentKodeBeskrivelse(Kodeverk.DISKRESJONSKODER, "SPSO")
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
                type = Persondata.SikkerhetstiltakType.valueOf(it.tiltakstype),
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
                gyldigFraOgMed = sivilstand.gyldigFraOgMed?.value
            )
        }
    }

    private fun hentForeldreansvar(data: Data): List<Persondata.Foreldreansvar> {
        return data.persondata.foreldreansvar.map { forelderansvar ->
            val ansvarligUtenNavn = forelderansvar.ansvarligUtenIdentifikator?.navn?.let(::hentNavn)
            val ansvarlig = data.tredjepartsPerson.map { it[forelderansvar.ansvar] }.getOrNull()
            val ansvarligsubject = data.tredjepartsPerson.map { it[forelderansvar.ansvarssubjekt] }.getOrNull()
            Persondata.Foreldreansvar(
                ansvar = forelderansvar.ansvar ?: "Kunne ikke hente type ansvar",
                ansvarlig = ansvarlig?.navn ?: ansvarligUtenNavn,
                ansvarsubject = ansvarligsubject?.navn
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
                    it.coAdressenavn != null -> Persondata.Adresse(it.coAdressenavn!!)
                    it.ukjentBosted != null -> Persondata.Adresse(it.ukjentBosted?.bostedskommune ?: "Ukjent kommune")
                    else -> null
                }
            )
        }
    }

    private fun hentDodsbo(data: Data): List<Persondata.Dodsbo> {
        return data.persondata.kontaktinformasjonForDoedsbo.map { dodsbo ->
            Persondata.Dodsbo(
                adressat = hentAdressat(dodsbo),
                adresse = hentAdresse(dodsbo.adresse),
                registrert = dodsbo.attestutstedelsesdato.value,
                skifteform = when (dodsbo.skifteform) {
                    OFFENTLIG -> Persondata.Skifteform.OFFENTLIG
                    ANNET -> Persondata.Skifteform.ANNET
                    else -> Persondata.Skifteform.UKJENT
                }
            )
        }
    }

    private fun hentAdressat(dodsbo: HentPersondata.KontaktinformasjonForDoedsbo): Persondata.Adressat {
        return Persondata.Adressat(
            advokatSomAdressat = hentAdvokatSomAdressat(dodsbo),
            personSomAdressat = hentPersonSomAdressat(dodsbo),
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

    private fun hentPersonSomAdressat(dodsbo: HentPersondata.KontaktinformasjonForDoedsbo): Persondata.PersonSomAdressat? {
        val adressat = dodsbo.personSomKontakt ?: return null
        return Persondata.PersonSomAdressat(
            fnr = adressat.identifikasjonsnummer,
            navn = adressat.personnavn?.let(::hentNavn),
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
            linje3 = if (adresse.adresselinje2 == null) null else sisteLinje
        )
    }

    private fun hentFullmakt(data: Data): List<Persondata.Fullmakt> {
        return data.persondata.fullmakt.map {
            val navn = data.tredjepartsPerson
                .map { personer -> personer[it.motpartsPersonident]?.navn }
                .getOrNull()

            Persondata.Fullmakt(
                motpartsPersonident = it.motpartsPersonident,
                motpartsPersonNavn = navn ?: Persondata.Navn.UKJENT,
                motpartsRolle = when (it.motpartsRolle) {
                    HentPersondata.FullmaktsRolle.FULLMAKTSGIVER -> Persondata.FullmaktsRolle.FULLMAKTSGIVER
                    HentPersondata.FullmaktsRolle.FULLMEKTIG -> Persondata.FullmaktsRolle.FULLMEKTIG
                    else -> Persondata.FullmaktsRolle.UKJENT
                },
                omraade = it.omraader,
                gyldigFraOgMed = it.gyldigFraOgMed.value,
                gyldigTilOgMed = it.gyldigTilOgMed.value
            )
        }
    }

    private fun hentVergemal(data: Data): List<Persondata.Verge> {
        return data.persondata.vergemaalEllerFremtidsfullmakt.map { vergemal ->
            val motpart = data.tredjepartsPerson.map { personer ->
                personer[vergemal.vergeEllerFullmektig.motpartsPersonident]?.navn
            }.getOrNull()
            val navn = vergemal.vergeEllerFullmektig.navn?.let(::hentNavn)

            Persondata.Verge(
                ident = vergemal.vergeEllerFullmektig.motpartsPersonident,
                navn = motpart ?: navn,
                vergesakstype = vergemal.type,
                omfang = vergemal.vergeEllerFullmektig.omfang,
                embete = vergemal.embete,
                gyldighetstidspunkt = vergemal.folkeregistermetadata?.gyldighetstidspunkt?.value?.toLocalDate(),
                opphoerstidspunkt = vergemal.folkeregistermetadata?.opphoerstidspunkt?.value?.toLocalDate()
            )
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
            val sisteEndring = it.metadata.endringer.maxBy { dato -> dato.registrert.value }!!
            Persondata.Telefon(
                retningsnummer = kodeverk.hentKodeBeskrivelse(Kodeverk.RETNINGSNUMRE, it.landskode),
                identifikator = it.nummer,
                sistEndretAv = sisteEndring.registrertAv,
                sistEndret = sisteEndring.registrert.value
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
                            sistEndret = bankkonto.endringstidspunkt
                                .toGregorianCalendar()
                                .toZonedDateTime()
                                .toLocalDateTime(),
                            sistEndretAv = bankkonto.endretAv
                        )
                        is BankkontoUtland -> Persondata.Bankkonto(
                            kontonummer = bankkonto.bankkontoUtland.bankkontonummer,
                            banknavn = bankkonto.bankkontoUtland.banknavn,
                            sistEndret = bankkonto.endringstidspunkt.toGregorianCalendar().toZonedDateTime()
                                .toLocalDateTime(),
                            sistEndretAv = bankkonto.endretAv,

                            bankkode = bankkonto.bankkontoUtland.bankkode,
                            swift = bankkonto.bankkontoUtland.swift,
                            landkode = kodeverk.hentKodeBeskrivelse(
                                Kodeverk.LAND,
                                bankkonto.bankkontoUtland.landkode.kodeRef
                            ),
                            adresse = Persondata.Adresse(
                                linje1 = bankkonto.bankkontoUtland.bankadresse.adresselinje1 ?: "Ukjent adresse",
                                linje2 = bankkonto.bankkontoUtland.bankadresse.adresselinje2,
                                linje3 = bankkonto.bankkontoUtland.bankadresse.adresselinje3
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
}

fun <T> KodeverkService.hentKodeBeskrivelse(
    kodeverk: Kodeverk,
    termnavn: T,
    sprak: String = "nb"
): Persondata.KodeBeskrivelse<T> {
    val beskrivelse = this.hentVerdi(kodeverk, termnavn.toString(), sprak)
    return Persondata.KodeBeskrivelse(
        kode = termnavn,
        beskrivelse = beskrivelse
    )
}
