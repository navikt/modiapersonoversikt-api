package no.nav.modiapersonoversikt.rest.persondata

import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain.Publikumsmottak
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.*
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.*
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.FullmaktDto
import no.nav.modiapersonoversikt.consumer.pdlFullmaktApi.generated.models.OmraaderMedHandlingDto
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.rest.persondata.PersondataResult.InformasjonElement
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.kontonummer.KontonummerService
import java.time.LocalDate
import java.time.LocalDateTime

fun gittKodeverk() =
    EnhetligKodeverk.Kodeverk(
        navn = "kodeverk",
        kodeverk =
            mapOf(
                "M" to "Mann",
                "KODE6" to "Sperret adresse, strengt fortrolig",
                "UGRADERT" to "Ugradert",
                "NOR" to "Norge",
                "NO" to "Norsk",
                "BOSA" to "Bosatt",
                "GIFT" to "Gift",
                "1444" to "TestPoststed",
                "47" to "+47",
                "ESP" to "Spania",
            ),
    )

internal fun gittTredjepartsperson(
    fnr: String = "98765432100",
    navn: String = "Tredjepart Relasjon",
    alder: Int? = 20,
    adressebeskyttelse: Persondata.AdresseBeskyttelse = Persondata.AdresseBeskyttelse.UGRADERT,
    digitalKontaktinformasjon: Persondata.DigitalKontaktinformasjonTredjepartsperson? = null,
) = Persondata.TredjepartsPerson(
    fnr = fnr,
    navn =
        listOf(
            gittNavn(navn),
        ),
    fodselsdato = emptyList(),
    alder = alder,
    kjonn = emptyList(),
    adressebeskyttelse = listOf(Persondata.KodeBeskrivelse(adressebeskyttelse, adressebeskyttelse.toString())),
    bostedAdresse = emptyList(),
    dodsdato = emptyList(),
    digitalKontaktinformasjon = digitalKontaktinformasjon,
)

internal fun gittNavn(navn: String): Persondata.Navn {
    val split = navn.split(" ")
    return Persondata.Navn(
        fornavn = split.first(),
        mellomnavn =
            if (split.size <= 2) {
                null
            } else {
                split.subList(1, split.size - 1).joinToString(" ")
            },
        etternavn = split.last(),
    )
}

internal fun gittIdentifiserendeInformasjon(
    navn: String,
    kjoenn: String,
): IdentifiserendeInformasjon =
    IdentifiserendeInformasjon(
        navn = gittPersonnavn(navn),
        kjoenn = kjoenn,
        foedselsdato = LocalDate.parse("2000-01-01"),
        statsborgerskap = listOf(),
    )

internal fun gittPersonnavn(navn: String): Personnavn {
    val persondataNavn = gittNavn(navn)
    return Personnavn(
        fornavn = persondataNavn.fornavn,
        mellomnavn = persondataNavn.mellomnavn,
        etternavn = persondataNavn.etternavn,
    )
}

internal fun gittHentPersondataNavn(navn: String): List<Navn> {
    val persondataNavn = gittNavn(navn)
    val fregNavn =
        Navn(
            fornavn = persondataNavn.fornavn,
            mellomnavn = persondataNavn.mellomnavn,
            etternavn = persondataNavn.etternavn,
            metadata =
                Metadata(
                    master = "Freg",
                    endringer = emptyList(),
                ),
        )

    val pdlNavn =
        fregNavn.copy(
            fornavn = "PDL_${fregNavn.fornavn}",
            metadata =
                fregNavn.metadata.copy(
                    master = "PDL",
                ),
        )

    return listOf(fregNavn, pdlNavn)
}

internal val kontaktinformasjonTredjepartsperson =
    Persondata.DigitalKontaktinformasjonTredjepartsperson(
        mobiltelefonnummer = "90909090",
        reservasjon = "false",
    )

internal val kontaktinformasjonTredjepartspersonMap =
    mapOf(
        "55555666000" to kontaktinformasjonTredjepartsperson,
    )

internal val tredjepartsPersoner =
    mapOf(
        "98765432100" to
            gittTredjepartsperson(
                navn = "Datteren Hans",
                alder = 15,
            ),
        "11225678910" to
            gittTredjepartsperson(
                fnr = "11225678910",
                navn = "Gift Relasjon",
            ),
        "11225666000" to
            gittTredjepartsperson(
                fnr = "11225666000",
                navn = "Dødsbo Person",
            ),
        "11223344910" to
            gittTredjepartsperson(
                fnr = "11223344910",
                navn = "Adressebeskyttet Barn",
                alder = 10,
                adressebeskyttelse = Persondata.AdresseBeskyttelse.KODE6,
            ),
        "55555666000" to
            gittTredjepartsperson(
                fnr = "55555666000",
                navn = "Person MedFullmakt",
                digitalKontaktinformasjon = kontaktinformasjonTredjepartsperson,
            ),
        "55555111000" to
            gittTredjepartsperson(
                fnr = "55555111000",
                navn = "Person Vergemål",
            ),
        "55333111000" to
            gittTredjepartsperson(
                fnr = "55333111000",
                navn = "Person MedForeldreansvar",
            ),
    )

internal fun gittNavKontorEnhet(
    enhetId: String = "0123",
    enhetNavn: String = "NAV Oslo",
) = NorgDomain.EnhetKontaktinformasjon(
    enhet =
        NorgDomain.Enhet(
            enhetId = enhetId,
            enhetNavn = enhetNavn,
            status = NorgDomain.EnhetStatus.AKTIV,
            oppgavebehandler = false,
        ),
    publikumsmottak =
        listOf(
            Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Testgata",
                        husnummer = "10",
                        postnummer = "0110",
                        poststed = "Ingensteds",
                    ),
                apningstider =
                    listOf(
                        NorgDomain.Apningstid(
                            ukedag = NorgDomain.Ukedag.TIRSDAG,
                            stengt = false,
                            apentFra = "10:00",
                            apentTil = "15:00",
                        ),
                        NorgDomain.Apningstid(
                            ukedag = NorgDomain.Ukedag.MANDAG,
                            stengt = true,
                            apentFra = null,
                            apentTil = null,
                        ),
                    ),
            ),
        ),
    overordnetEnhet = null,
)

internal fun gittEndring(
    registrert: LocalDateTime =
        LocalDateTime.of(2020, 7, 1, 10, 0),
    registrertAv: String = "Folkeregisteret",
    systemkilde: String = "FREG",
    kilde: String = "Bruker",
) = Endring(
    registrert = registrert,
    registrertAv = registrertAv,
    systemkilde = systemkilde,
    kilde = kilde,
)

internal val metadata =
    Metadata(
        endringer =
            listOf(
                gittEndring(),
            ),
        master = "Freg",
    )

internal val adresse =
    Bostedsadresse(
        gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
        gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
        metadata = metadata,
        folkeregistermetadata = null,
        vegadresse = null,
        matrikkeladresse = null,
        utenlandskAdresse = null,
        ukjentBosted = null,
        angittFlyttedato = null,
    )

internal val utenlandskBankkonto =
    KontonummerService.Konto(
        kontonummer = "123",
        banknavn = null,
        sistEndret = null,
        swift = "ASD123",
        adresse =
            KontonummerService.Adresse(
                linje1 = "Utenlandsk bankkontoadresse",
            ),
        landkode = "ESP",
        valutakode = "NOK",
        opprettetAv = "",
        kilde = "",
    )

internal val kontaktinformasjonDodsbo =
    KontaktinformasjonForDoedsbo(
        skifteform = KontaktinformasjonForDoedsboSkifteform.OFFENTLIG,
        attestutstedelsesdato = gittDato("2014-05-02"),
        personSomKontakt =
            KontaktinformasjonForDoedsboPersonSomKontakt(
                identifikasjonsnummer = "11225666000",
                foedselsdato = null,
                personnavn = null,
            ),
        advokatSomKontakt = null,
        organisasjonSomKontakt = null,
        metadata = metadata,
        adresse =
            KontaktinformasjonForDoedsboAdresse(
                adresselinje1 = "Gateveien",
                adresselinje2 = null,
                poststedsnavn = "Poststed",
                postnummer = "9988",
                landkode = null,
            ),
    )

internal val digitalKontaktinformasjon =
    Krr.DigitalKontaktinformasjon(
        personident = "12345678910",
        reservasjon = null,
        epostadresse = Krr.Epostadresse(value = "test@test.no"),
        mobiltelefonnummer = Krr.MobilTelefon(value = "90009900"),
    )

internal val arbeidsrettetOppfolgingStatus =
    ArbeidsrettetOppfolging.Status(
        underOppfolging = true,
        erManuell = true,
    )

internal val sivilstandPerson =
    Sivilstand(
        type = Sivilstandstype.GIFT,
        gyldigFraOgMed = gittDato("2015-09-09"),
        relatertVedSivilstand = "11225678910",
    )

internal val statsborger =
    Statsborgerskap(
        land = "NOR",
        gyldigFraOgMed = gittDato("2000-10-01"),
        gyldigTilOgMed = null,
    )

internal val sikkerhetstiltakData =
    Sikkerhetstiltak(
        tiltakstype = "TFUS",
        beskrivelse = "Telefonisk utestengelse",
        gyldigFraOgMed = gittDato("2019-01-01"),
        gyldigTilOgMed = gittDato("2019-01-14"),
    )

internal val tilrettelagtKommunikasjonData =
    TilrettelagtKommunikasjon(
        talespraaktolk = Tolk(spraak = "NO"),
        tegnspraaktolk = Tolk(spraak = "NO"),
    )

internal val fullmaktPerson =
    FullmaktDto(
        fullmaktId = 1232133123,
        fullmektig = "55555666000",
        omraade = listOf(OmraaderMedHandlingDto(tema = "SAP", handling = listOf(OmraaderMedHandlingDto.Handling.LES))),
        gyldigFraOgMed = gittDato("2018-01-03"),
        gyldigTilOgMed = gittDato("2018-10-03"),
    )

internal val vergemal =
    VergemaalEllerFremtidsfullmakt(
        type = "voksen",
        embete = "fylkesmannenIOsloOgViken",
        vergeEllerFullmektig =
            VergeEllerFullmektig(
                identifiserendeInformasjon = gittIdentifiserendeInformasjon("Person Vergemål", "K"),
                motpartsPersonident = "55555111000",
                omfang = null,
                omfangetErInnenPersonligOmraade = false,
            ),
        folkeregistermetadata =
            Folkeregistermetadata(
                gyldighetstidspunkt = gittDateTime("2018-02-02T00:00:00"),
                opphoerstidspunkt = null,
            ),
    )

internal val foreldreansvarData =
    Foreldreansvar(
        ansvar = "felles",
        ansvarlig = "55333111000",
        ansvarssubjekt = "98765432100",
        metadata = Metadata2(historisk = false),
        ansvarligUtenIdentifikator = null,
    )

internal val forelderBarnRelasjonData =
    listOf(
        ForelderBarnRelasjon(
            relatertPersonsIdent = "98765432100",
            relatertPersonUtenFolkeregisteridentifikator = null,
            relatertPersonsRolle = ForelderBarnRelasjonRolle.BARN,
        ),
        ForelderBarnRelasjon(
            relatertPersonsIdent = "11223344910",
            relatertPersonUtenFolkeregisteridentifikator = null,
            relatertPersonsRolle = ForelderBarnRelasjonRolle.BARN,
        ),
        ForelderBarnRelasjon(
            relatertPersonsIdent = null,
            relatertPersonUtenFolkeregisteridentifikator =
                RelatertBiPerson(
                    navn =
                        Personnavn(
                            fornavn = "Tredjepart",
                            mellomnavn = "Relasjon",
                            etternavn = "Fra utlandet",
                        ),
                    foedselsdato = LocalDate.parse("2000-01-01"),
                    statsborgerskap = null,
                    kjoenn = KjoennType.MANN,
                ),
            relatertPersonsRolle = ForelderBarnRelasjonRolle.MEDMOR,
        ),
    )

internal fun gittVegadresse(
    matrikkelId: Long? = null,
    husbokstav: String? = null,
    husnummer: String? = "3",
    bruksenhetsnummer: String? = null,
    adressenavn: String? = "Vegadressestien",
    kommunenummer: String? = "0987",
    bydelsnummer: String? = null,
    tilleggsnavn: String? = null,
    postnumme: String? = "1444",
) = Vegadresse(
    matrikkelId = matrikkelId,
    husbokstav = husbokstav,
    husnummer = husnummer,
    bruksenhetsnummer = bruksenhetsnummer,
    adressenavn = adressenavn,
    kommunenummer = kommunenummer,
    bydelsnummer = bydelsnummer,
    tilleggsnavn = tilleggsnavn,
    postnummer = postnumme,
)

internal val deltBostedData =
    DeltBosted(
        startdatoForKontrakt = gittDato("2019-09-09"),
        sluttdatoForKontrakt = null,
        coAdressenavn = null,
        vegadresse = gittVegadresse(),
        matrikkeladresse = null,
        utenlandskAdresse = null,
        ukjentBosted = null,
    )

internal val kontaktadresseData =
    Kontaktadresse(
        gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
        gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
        metadata = metadata,
        coAdressenavn = "C/O Adressenavn",
        postadresseIFrittFormat = null,
        postboksadresse = null,
        vegadresse = gittVegadresse(),
        utenlandskAdresse = null,
        utenlandskAdresseIFrittFormat = null,
    )

internal val oppholdsadresseData =
    Oppholdsadresse(
        gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
        gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
        oppholdAnnetSted = "UTENRIKS",
        coAdressenavn = "Kari Hansen",
        vegadresse = gittVegadresse(),
        matrikkeladresse = null,
        utenlandskAdresse = null,
        metadata = metadata,
    )

internal val bostedadresseData =
    Bostedsadresse(
        gyldigFraOgMed = gittDateTime("2021-02-02T00:00:00"),
        gyldigTilOgMed = gittDateTime("2021-02-02T00:00:00"),
        metadata = metadata,
        vegadresse = gittVegadresse(),
        utenlandskAdresse = null,
        ukjentBosted = null,
        folkeregistermetadata = null,
        matrikkeladresse = null,
        angittFlyttedato = null,
    )

internal fun gittDato(dato: String) = LocalDate.parse(dato)

internal fun gittDateTime(dato: String) = LocalDateTime.parse(dato)

internal fun ukjentBosted(bosted: String) =
    listOf(
        adresse.copy(
            ukjentBosted = UkjentBosted(bosted),
        ),
    )

internal val testPerson =
    Person(
        navn = gittHentPersondataNavn("Teste Ruud McTestesen"),
        kjoenn = listOf(Kjoenn(KjoennType.MANN)),
        foedselsdato = listOf(Foedselsdato(gittDato("2000-01-02"))),
        foedested = listOf(Foedested("Foedested")),
        adressebeskyttelse =
            listOf(
                Adressebeskyttelse(AdressebeskyttelseGradering.UGRADERT),
            ),
        statsborgerskap = listOf(statsborger),
        doedsfall = emptyList(),
        folkeregisterpersonstatus = listOf(Folkeregisterpersonstatus("bosatt")),
        sivilstand = listOf(sivilstandPerson),
        sikkerhetstiltak = listOf(sikkerhetstiltakData),
        kontaktinformasjonForDoedsbo = listOf(kontaktinformasjonDodsbo),
        tilrettelagtKommunikasjon = listOf(tilrettelagtKommunikasjonData),
        telefonnummer = listOf(Telefonnummer("47", "90909090", 1, metadata)),
        vergemaalEllerFremtidsfullmakt = listOf(vergemal),
        foreldreansvar = listOf(foreldreansvarData),
        forelderBarnRelasjon = forelderBarnRelasjonData,
        deltBosted = listOf(deltBostedData),
        bostedsadresse =
            listOf(
                bostedadresseData,
                bostedadresseData.copy(
                    gyldigFraOgMed = gittDateTime("2021-10-01T00:00:00"),
                    gyldigTilOgMed = null,
                ),
            ),
        kontaktadresse =
            listOf(
                kontaktadresseData,
                kontaktadresseData.copy(
                    gyldigFraOgMed = null,
                    gyldigTilOgMed = null,
                    vegadresse = gittVegadresse(husnummer = "10"),
                ),
            ),
        oppholdsadresse = listOf(oppholdsadresseData),
    )

internal val testData =
    PersondataFletter.Data(
        personIdent = "12345678910",
        persondata = testPerson,
        geografiskeTilknytning = PersondataResult.runCatching(InformasjonElement.PDL_GT) { "0123" },
        erEgenAnsatt = PersondataResult.runCatching(InformasjonElement.EGEN_ANSATT) { false },
        navEnhet = PersondataResult.runCatching(InformasjonElement.NORG_NAVKONTOR) { gittNavKontorEnhet() },
        krrData = PersondataResult.runCatching(InformasjonElement.DKIF) { digitalKontaktinformasjon },
        oppfolging = PersondataResult.runCatching(InformasjonElement.OPPFOLGING) { arbeidsrettetOppfolgingStatus },
        bankkonto = PersondataResult.runCatching(InformasjonElement.BANKKONTO) { utenlandskBankkonto },
        tredjepartsPerson = PersondataResult.runCatching(InformasjonElement.PDL_TREDJEPARTSPERSONER) { tredjepartsPersoner },
        kontaktinformasjonTredjepartsperson =
            PersondataResult.runCatching(InformasjonElement.DKIF_TREDJEPARTSPERSONER) {
                kontaktinformasjonTredjepartspersonMap
            },
        fullmektige = PersondataResult.runCatching(InformasjonElement.FULLMAKT) { listOf(fullmaktPerson) },
        harTilgangTilSkjermetPerson = false,
    )
