package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.*
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.Gateadresse
import no.nav.tjeneste.virksomhet.person.v3.informasjon.*
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import java.time.LocalDate
import java.time.LocalDateTime

@JvmOverloads
fun gittData(
    persondata: HentPersondata.Person,
    geografiskeTilknytning: PersondataResult<String?> = PersondataResult.runCatching("gt") { "0123" },
    erEgenAnsatt: PersondataResult<Boolean> = PersondataResult.runCatching("egenAnsatt") { false },
    navEnhet: PersondataResult<EnhetKontaktinformasjon?> = PersondataResult.runCatching("navEnhet") { navKontorEnhet },
    dkifData: PersondataResult<Dkif.DigitalKontaktinformasjon> = PersondataResult.runCatching("dkif") { digitalKontaktinformasjon },
    bankkonto: PersondataResult<HentPersonResponse> = PersondataResult.runCatching("bankkonto") { utenlandskBankkonto },
    tredjepartsPerson: PersondataResult<Map<String, Persondata.TredjepartsPerson>> = PersondataResult.runCatching("tredjepartsperson") { tredjepartsPersoner }
) = PersondataFletter.Data(
    persondata = persondata,
    geografiskeTilknytning = geografiskeTilknytning,
    erEgenAnsatt = erEgenAnsatt,
    navEnhet = navEnhet,
    dkifData = dkifData,
    bankkonto = bankkonto,
    tredjepartsPerson = tredjepartsPerson
)

@JvmOverloads
fun gittPerson(
    fnr: String = "12345678910",
    navn: String = "Teste Ruud McTestesen",
    kjonn: HentPersondata.KjoennType = HentPersondata.KjoennType.MANN,
    fodselsdato: String = "2000-01-02",
    adressebeskyttelse: HentPersondata.AdressebeskyttelseGradering = HentPersondata.AdressebeskyttelseGradering.UGRADERT,
    statsborgerskap: HentPersondata.Statsborgerskap = statsborger,
    dodsdato: String = "2010-01-02",
    folkeregisterpersonstatus: String = "bosatt",
    sivilstand: HentPersondata.Sivilstand = sivilstandPerson,
    sikkerhetstiltak: HentPersondata.Sikkerhetstiltak = sikkerhetstiltakData,
    dodsbo: HentPersondata.KontaktinformasjonForDoedsbo = kontaktinformasjonDodsbo,
    tilrettelagtKommunikasjon: HentPersondata.TilrettelagtKommunikasjon = tilrettelagtKommunikasjonData,
    fullmakt: HentPersondata.Fullmakt = fullmaktPerson,
    telefonnummer: HentPersondata.Telefonnummer = HentPersondata.Telefonnummer("47", "90909090", 1, metadata),
    vergemaalEllerFremtidsfullmakt: HentPersondata.VergemaalEllerFremtidsfullmakt = vergemal,
    foreldreansvar: HentPersondata.Foreldreansvar = foreldreansvarData,
    forelderBarnRelasjon: List<HentPersondata.ForelderBarnRelasjon> = forelderBarnRelasjonData,
    deltBosted: HentPersondata.DeltBosted = deltBostedData,
    bosted: List<HentPersondata.Bostedsadresse> = ukjentBosted("Ukjent adresse"),
    kontaktadresse: HentPersondata.Kontaktadresse = kontaktadresseData,
    oppholdsadresse: HentPersondata.Oppholdsadresse = oppholdsadresseData
) = HentPersondata.Person(
    folkeregisteridentifikator = listOf(HentPersondata.Folkeregisteridentifikator(fnr, "I_BRUK", "FNR")),
    navn = listOf(gittHentPersondataNavn(navn)),
    kjoenn = listOf(HentPersondata.Kjoenn(kjonn)),
    foedsel = listOf(HentPersondata.Foedsel(gittDato(fodselsdato))),
    adressebeskyttelse = listOf(
        HentPersondata.Adressebeskyttelse(adressebeskyttelse)
    ),
    statsborgerskap = listOf(statsborgerskap),
    doedsfall = listOf(HentPersondata.Doedsfall(gittDato(dodsdato))),
    folkeregisterpersonstatus = listOf(HentPersondata.Folkeregisterpersonstatus(folkeregisterpersonstatus)),
    sivilstand = listOf(sivilstand),
    sikkerhetstiltak = listOf(sikkerhetstiltak),
    kontaktinformasjonForDoedsbo = listOf(dodsbo),
    tilrettelagtKommunikasjon = listOf(tilrettelagtKommunikasjon),
    fullmakt = listOf(fullmakt),
    telefonnummer = listOf(telefonnummer),
    vergemaalEllerFremtidsfullmakt = listOf(vergemaalEllerFremtidsfullmakt),
    foreldreansvar = listOf(foreldreansvar),
    forelderBarnRelasjon = forelderBarnRelasjon,
    deltBosted = listOf(deltBosted),
    bostedsadresse = bosted,
    kontaktadresse = listOf(kontaktadresse),
    oppholdsadresse = listOf(oppholdsadresse)
)

@JvmOverloads
fun gittKodeverk() = EnhetligKodeverk.Kodeverk(
    navn = "kodeverk",
    kodeverk = mapOf(
        "M" to "Mann",
        "KODE6" to "Sperret adresse, strengt fortrolig",
        "UGRADERT" to "Ugradert",
        "NOR" to "Norge",
        "NO" to "Norsk",
        "BOSA" to "Bosatt",
        "GIFT" to "Gift",
        "1444" to "TestPoststed",
        "47" to "+47",
        "ESP" to "Spania"
    )
)

internal fun gittTredjepartsperson(
    fnr: String = "98765432100",
    navn: String = "Tredjepart Relasjon",
    alder: Int? = 20,
    adressebeskyttelse: Persondata.AdresseBeskyttelse = Persondata.AdresseBeskyttelse.UGRADERT
) = Persondata.TredjepartsPerson(
    fnr = fnr,
    navn = listOf(
        gittNavn(navn)
    ),
    fodselsdato = emptyList(),
    alder = alder,
    kjonn = emptyList(),
    adressebeskyttelse = listOf(Persondata.KodeBeskrivelse(adressebeskyttelse, adressebeskyttelse.toString())),
    bostedAdresse = emptyList(),
    personstatus = emptyList()
)

internal fun gittNavn(navn: String): Persondata.Navn {
    val split = navn.split(" ")
    return Persondata.Navn(
        fornavn = split.first(),
        mellomnavn = if (split.size <= 2) null else {
            split.subList(1, split.size - 1).joinToString(" ")
        },
        etternavn = split.last()
    )
}

internal fun gittPersonnavn(navn: String): HentPersondata.Personnavn {
    val persondataNavn = gittNavn(navn)
    return HentPersondata.Personnavn(
        fornavn = persondataNavn.fornavn,
        mellomnavn = persondataNavn.mellomnavn,
        etternavn = persondataNavn.etternavn
    )
}

internal fun gittHentPersondataNavn(navn: String): HentPersondata.Navn {
    val persondataNavn = gittNavn(navn)
    return HentPersondata.Navn(
        fornavn = persondataNavn.fornavn,
        mellomnavn = persondataNavn.mellomnavn,
        etternavn = persondataNavn.etternavn
    )
}

internal val tredjepartsPersoner = mapOf(
    "98765432100" to gittTredjepartsperson(
        navn = "Datteren Hans"
    ),
    "11225678910" to gittTredjepartsperson(
        fnr = "11225678910",
        navn = "Gift Relasjon"
    ),
    "11225666000" to gittTredjepartsperson(
        fnr = "11225666000",
        navn = "Dødsbo Person"
    ),
    "11223344910" to gittTredjepartsperson(
        fnr = "11223344910",
        navn = "Adressebeskyttet Barn",
        adressebeskyttelse = Persondata.AdresseBeskyttelse.KODE6
    ),
    "55555666000" to gittTredjepartsperson(
        fnr = "55555666000",
        navn = "Person MedFullmakt"
    ),
    "55555111000" to gittTredjepartsperson(
        fnr = "55555111000",
        navn = "Person Vergemål"
    ),
    "55333111000" to gittTredjepartsperson(
        fnr = "55333111000",
        navn = "Person MedForeldreansvar"
    )
)

internal val navKontorEnhet = EnhetKontaktinformasjon(
    OrganisasjonEnhetKontaktinformasjon()
        .withEnhetId("0123")
        .withEnhetNavn("NAV Oslo")
        .withKontaktinformasjon(
            Kontaktinformasjon().withPublikumsmottakliste(
                listOf(
                    Publikumsmottak()
                        .withApningstider(
                            Apningstider()
                                .withApningstid(
                                    listOf(
                                        Apningstid()
                                            .withApentFra(Klokkeslett(10, 0, 0))
                                            .withApentTil(Klokkeslett(15, 0, 0))
                                            .withUkedag(Ukedag.MANDAG)
                                    )
                                )
                        )
                        .withBesoeksadresse(
                            Gateadresse()
                                .withGatenavn("Testgata")
                                .withHusnummer("10")
                                .withPostnummer("0110")
                                .withPoststed("Ingensteds")
                        )
                )
            )
        )
)

internal fun gittEndring(
    registrert: HentPersondata.DateTime = HentPersondata.DateTime(
        LocalDateTime.of(2020, 7, 1, 10, 0)
    ),
    registrertAv: String = "Folkeregisteret",
    systemkilde: String = "FREG"
) = HentPersondata.Endring(
    registrert = registrert,
    registrertAv = registrertAv,
    systemkilde = systemkilde
)

internal val metadata = HentPersondata.Metadata(
    endringer = listOf(
        gittEndring()
    )
)

internal val adresse = HentPersondata.Bostedsadresse(
    gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
    gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
    metadata = metadata,
    folkeregistermetadata = null,
    vegadresse = null,
    matrikkeladresse = null,
    utenlandskAdresse = null,
    ukjentBosted = null
)

internal val utenlandskBankkonto = HentPersonResponse()
    .withPerson(
        Bruker()
            .withBankkonto(
                BankkontoUtland()
                    .withBankkontoUtland(
                        BankkontonummerUtland()
                            .withBankkontonummer("123")
                            .withLandkode(
                                Landkoder().withValue("ESP")
                            )
                            .withBankadresse(
                                UstrukturertAdresse()
                                    .withAdresselinje1("Utenlandsk bankkontoadresse")
                            )
                            .withSwift("ASD123")
                            .withValuta(
                                Valutaer().withValue("NOK")
                            )
                    )
            )
    )

internal val kontaktinformasjonDodsbo = HentPersondata.KontaktinformasjonForDoedsbo(
    skifteform = HentPersondata.KontaktinformasjonForDoedsboSkifteform.OFFENTLIG,
    attestutstedelsesdato = gittDato("2014-05-02"),
    personSomKontakt = HentPersondata.KontaktinformasjonForDoedsboPersonSomKontakt(
        identifikasjonsnummer = "11225666000",
        foedselsdato = null,
        personnavn = null
    ),
    advokatSomKontakt = null,
    organisasjonSomKontakt = null,
    metadata = metadata,
    adresse = HentPersondata.KontaktinformasjonForDoedsboAdresse(
        adresselinje1 = "Gateveien",
        adresselinje2 = null,
        poststedsnavn = "Poststed",
        postnummer = "9988",
        landkode = null
    )
)

internal val digitalKontaktinformasjon = Dkif.DigitalKontaktinformasjon(
    personident = "12345678910",
    reservasjon = null,
    epostadresse = Dkif.Epostadresse(value = "test@test.no"),
    mobiltelefonnummer = Dkif.MobilTelefon(value = "90009900")
)

internal val sivilstandPerson = HentPersondata.Sivilstand(
    type = HentPersondata.Sivilstandstype.GIFT,
    gyldigFraOgMed = gittDato("2015-09-09"),
    relatertVedSivilstand = "11225678910"
)

internal val statsborger = HentPersondata.Statsborgerskap(
    land = "NOR",
    gyldigFraOgMed = gittDato("2000-10-01"),
    gyldigTilOgMed = null
)

internal val sikkerhetstiltakData = HentPersondata.Sikkerhetstiltak(
    tiltakstype = "TFUS",
    beskrivelse = "Telefonisk utestengelse",
    gyldigFraOgMed = gittDato("2019-01-01"),
    gyldigTilOgMed = gittDato("2019-01-14")
)

internal val tilrettelagtKommunikasjonData = HentPersondata.TilrettelagtKommunikasjon(
    talespraaktolk = HentPersondata.Tolk(spraak = "NO"),
    tegnspraaktolk = HentPersondata.Tolk(spraak = "NO")
)

internal val fullmaktPerson = HentPersondata.Fullmakt(
    motpartsPersonident = "55555666000",
    motpartsRolle = HentPersondata.FullmaktsRolle.FULLMEKTIG,
    omraader = emptyList(),
    gyldigFraOgMed = gittDato("2018-01-03"),
    gyldigTilOgMed = gittDato("2018-10-03")
)

internal val vergemal = HentPersondata.VergemaalEllerFremtidsfullmakt(
    type = "voksen",
    embete = "fylkesmannenIOsloOgViken",
    vergeEllerFullmektig = HentPersondata.VergeEllerFullmektig(
        navn = gittPersonnavn("Person Vergemål"),
        motpartsPersonident = "55555111000",
        omfang = null,
        omfangetErInnenPersonligOmraade = false
    ),
    folkeregistermetadata = HentPersondata.Folkeregistermetadata(
        gyldighetstidspunkt = gittDateTime("2018-02-02T00:00:00"),
        opphoerstidspunkt = null
    )
)

internal val foreldreansvarData = HentPersondata.Foreldreansvar(
    ansvar = "felles",
    ansvarlig = "55333111000",
    ansvarssubjekt = "98765432100",
    metadata = HentPersondata.Metadata2(historisk = false),
    ansvarligUtenIdentifikator = null
)

internal val forelderBarnRelasjonData = listOf(
    HentPersondata.ForelderBarnRelasjon(
        relatertPersonsIdent = "98765432100",
        relatertPersonsRolle = HentPersondata.ForelderBarnRelasjonRolle.BARN
    ),
    HentPersondata.ForelderBarnRelasjon(
        relatertPersonsIdent = "11223344910",
        relatertPersonsRolle = HentPersondata.ForelderBarnRelasjonRolle.BARN
    )
)

internal val vegadresse = HentPersondata.Vegadresse(
    matrikkelId = null,
    husbokstav = null,
    husnummer = "3",
    bruksenhetsnummer = null,
    adressenavn = "Vegadressestien",
    kommunenummer = "0987",
    bydelsnummer = null,
    tilleggsnavn = null,
    postnummer = "1444"
)

internal val deltBostedData = HentPersondata.DeltBosted(
    startdatoForKontrakt = gittDato("2019-09-09"),
    sluttdatoForKontrakt = null,
    coAdressenavn = null,
    vegadresse = vegadresse,
    matrikkeladresse = null,
    utenlandskAdresse = null,
    ukjentBosted = null
)

internal val kontaktadresseData = HentPersondata.Kontaktadresse(
    gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
    gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
    metadata = metadata,
    coAdressenavn = "C/O Adressenavn",
    postadresseIFrittFormat = null,
    postboksadresse = null,
    vegadresse = vegadresse,
    utenlandskAdresse = null
)

internal val oppholdsadresseData = HentPersondata.Oppholdsadresse(
    gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
    gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
    oppholdAnnetSted = "UTENRIKS",
    coAdressenavn = "Kari Hansen",
    vegadresse = vegadresse,
    matrikkeladresse = null,
    utenlandskAdresse = null,
    metadata = metadata
)

internal fun gittDato(dato: String) = HentPersondata.Date(LocalDate.parse(dato))

internal fun gittDateTime(dato: String) = HentPersondata.DateTime(LocalDateTime.parse(dato))

internal fun ukjentBosted(bosted: String) = listOf(
    adresse.copy(
        ukjentBosted = HentPersondata.UkjentBosted(bosted)
    )
)
