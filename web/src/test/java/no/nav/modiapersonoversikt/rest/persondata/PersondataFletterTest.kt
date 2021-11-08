package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.rest.enhet.model.EnhetKontaktinformasjon
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.organisasjonenhet.kontaktinformasjon.domain.*
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDate
import java.time.LocalDateTime

internal class PersondataFletterTest {
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = PersondataFletter(kodeverk)

    @BeforeEach
    internal fun setUp() {
        every { kodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            navn = "kodeverk",
            kodeverk = mapOf(
                "M" to "Mann",
                "KODE6" to "Sperret adresse, strengt fortrolig",
                "UGRADERT" to "Ugradert",
                "NOR" to "Norge",
                "NO" to "Norsk",
                "BOSA" to "Bosatt",
                "GIFT" to "Gift"
            )
        )
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                gittData(persondata = gittPerson())
            )
        )
    }

    private fun gittData(
        persondata: HentPersondata.Person,
        geografiskeTilknytning: PersondataResult<String?> = PersondataResult.runCatching("gt") { "0123" },
        erEgenAnsatt: PersondataResult<Boolean> = PersondataResult.runCatching("egenAnsatt") { false },
        navEnhet: PersondataResult<EnhetKontaktinformasjon>? = PersondataResult.runCatching("navEnhet") { navKontorEnhet },
        dkifData: PersondataResult<Dkif.DigitalKontaktinformasjon> = PersondataResult.runCatching("dkif") { digitalKontaktinformasjon },
        bankkonto: PersondataResult<HentPersonResponse> = PersondataResult.runCatching("bankkonto") { HentPersonResponse() },
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

    private fun gittPerson(
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
        bosted: List<HentPersondata.Bostedsadresse> = ukjentBosted("Ukjent adresse")
    ) = HentPersondata.Person(
        folkeregisteridentifikator = listOf(HentPersondata.Folkeregisteridentifikator(fnr, "I_BRUK", "FNR")),
        navn = listOf(gittNavn(navn)),
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
        telefonnummer = emptyList(),
        vergemaalEllerFremtidsfullmakt = emptyList(),
        foreldreansvar = emptyList(),
        forelderBarnRelasjon = emptyList(),
        deltBosted = emptyList(),
        bostedsadresse = bosted,
        kontaktadresse = emptyList()
    )

    private fun gittTredjepartsperson(
        fnr: String = "98765432100",
        navn: String = "Tredjepart Relasjon",
        alder: Int? = 20,
        adressebeskyttelse: Persondata.AdresseBeskyttelse = Persondata.AdresseBeskyttelse.UGRADERT
    ) = Persondata.TredjepartsPerson(
        fnr = fnr,
        navn = listOf(
            Persondata.Navn(
                fornavn = navn.split(" ").first(),
                mellomnavn = null,
                etternavn = navn.split(" ").last()
            )
        ),
        fodselsdato = emptyList(),
        alder = alder,
        kjonn = emptyList(),
        adressebeskyttelse = listOf(kodeverk.hentKodeBeskrivelse(KodeverkConfig.DISKRESJONSKODER, adressebeskyttelse)),
        bostedAdresse = emptyList(),
        personstatus = emptyList()
    )

    private fun gittNavn(navn: String): HentPersondata.Navn {
        val split = navn.split(" ")
        return HentPersondata.Navn(
            fornavn = split.first(),
            mellomnavn = if (split.size <= 2) null else {
                split.subList(1, split.size - 1).joinToString(" ")
            },
            etternavn = split.last()
        )
    }
    
    private val tredjepartsPersoner = mapOf(
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
        )
    )

    private val navKontorEnhet = EnhetKontaktinformasjon(
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

    private fun gittEndring(
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

    private val metadata = HentPersondata.Metadata(
        endringer = listOf(
            gittEndring()
        )
    )

    private val adresse = HentPersondata.Bostedsadresse(
        metadata = metadata,
        folkeregistermetadata = null,
        vegadresse = null,
        matrikkeladresse = null,
        utenlandskAdresse = null,
        ukjentBosted = null
    )
    
    private val kontaktinformasjonDodsbo = HentPersondata.KontaktinformasjonForDoedsbo(
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

    private val digitalKontaktinformasjon = Dkif.DigitalKontaktinformasjon(
        personident = "12345678910",
        reservasjon = null,
        epostadresse = Dkif.Epostadresse(value = "test@test.no"),
        mobiltelefonnummer = Dkif.MobilTelefon(value = "90009900")
    )
    
    private val sivilstandPerson = HentPersondata.Sivilstand(
        type = HentPersondata.Sivilstandstype.GIFT,
        gyldigFraOgMed = gittDato("2015-09-09"),
        relatertVedSivilstand = "11225678910"
    )
    
    private val statsborger = HentPersondata.Statsborgerskap(
        land = "NOR",
        gyldigFraOgMed = gittDato("2000-10-01"),
        gyldigTilOgMed = null
    )
    
    private val sikkerhetstiltakData = HentPersondata.Sikkerhetstiltak(
        tiltakstype = "TFUS",
        beskrivelse = "Telefonisk utestengelse",
        gyldigFraOgMed = gittDato("2019-01-01"),
        gyldigTilOgMed = gittDato("2019-01-14")
    )
    
    private val tilrettelagtKommunikasjonData = HentPersondata.TilrettelagtKommunikasjon(
        talespraaktolk = HentPersondata.Tolk(spraak = "NO"),
        tegnspraaktolk = HentPersondata.Tolk(spraak = "NO")
    )
    
    private val fullmaktPerson = HentPersondata.Fullmakt(
        motpartsPersonident = "55555666000",
        motpartsRolle = HentPersondata.FullmaktsRolle.FULLMEKTIG,
        omraader = emptyList(),
        gyldigFraOgMed = gittDato("2018-01-03"),
        gyldigTilOgMed = gittDato("2018-10-03")
    )
    
    private fun gittDato(dato: String) = HentPersondata.Date(LocalDate.parse(dato))

    private fun ukjentBosted(bosted: String) = listOf(
        adresse.copy(
            ukjentBosted = HentPersondata.UkjentBosted(bosted)
        )
    )
}
