package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.common.types.identer.EnhetId
import no.nav.modiapersonoversikt.consumer.norg.NorgDomain
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.KjoennType
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.Oppholdstillatelse
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Doedsfall
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Folkeregistermetadata
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.KontaktinformasjonForDoedsboAdvokatSomKontakt
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.KontaktinformasjonForDoedsboOrganisasjonSomKontakt
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Metadata
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Metadata2
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Opphold
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.RelatertBiPerson
import no.nav.modiapersonoversikt.consumer.representasjon.generated.infrastructure.Serializer
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.persondata.Persondata
import no.nav.modiapersonoversikt.service.persondata.PersondataFletter
import no.nav.modiapersonoversikt.service.persondata.PersondataResult
import no.nav.personoversikt.common.logging.TjenestekallLogg
import no.nav.personoversikt.common.test.snapshot.SnapshotExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import no.nav.modiapersonoversikt.consumer.pdl.generated.hentpersondata.Personnavn as PdlPersonnavn

internal class PersondataFletterTest {
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = PersondataFletter(kodeverk, TjenestekallLogg)
    val fnr = "12345678910"

    @BeforeEach
    internal fun setUp() {
        every { kodeverk.hentKodeverk<String, String>(any()) } returns gittKodeverk()
    }

    @Test
    fun `skjulte tredjeparter beholder relasjonene med tomme persondata`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                    skjulingsaarsaker =
                        (
                            listOf(
                                fullmaktPerson.fullmektig,
                                sivilstandPerson.relatertVedSivilstand!!,
                                foreldreansvarData.ansvarlig!!,
                                foreldreansvarData.ansvarssubjekt!!,
                                kontaktinformasjonDodsbo.personSomKontakt!!.identifikasjonsnummer!!,
                                vergemal.vergeEllerFullmektig.motpartsPersonident!!,
                                historiskVergemal.vergeEllerFullmektig.motpartsPersonident!!,
                            ) + forelderBarnRelasjonData.mapNotNull { it.relatertPersonsIdent }
                        ).associateWith { Persondata.Skjulingsaarsak.MANGLER_TILGANG },
                ),
            )
        assertEquals(
            "",
            result.person.fullmektige
                .single()
                .ident,
        )
        assertEquals(
            "",
            result.person.fullmektige
                .single()
                .fullmakt.fullmektig,
        )
        assertTrue(
            result.person.fullmektige
                .single()
                .fullmakt.endringslogg
                .isEmpty(),
        )
        assertNull(
            result.person.fullmektige
                .single()
                .digitalKontaktinformasjonTredjepartsperson,
        )
        assertNull(
            result.person.vergemal
                .single()
                .ident,
        )
        assertNull(
            result.person.vergemal
                .single()
                .navn,
        )
        assertNull(
            result.person.historiskeVergemal
                .single()
                .ident,
        )
        assertNull(
            result.person.historiskeVergemal
                .single()
                .navn,
        )
        assertEquals(
            "",
            result.person.dodsbo
                .single()
                .adresse.linje1,
        )
        assertEquals(
            Persondata.PersonSomAdressat(null, emptyList(), null, Persondata.Skjulingsaarsak.MANGLER_TILGANG),
            result.person.dodsbo
                .single()
                .adressat.personSomAdressat,
        )
        assertEquals(
            Persondata.Foreldreansvar(
                ansvar = "felles",
                ansvarlig = Persondata.NavnOgIdent(null, null, Persondata.Skjulingsaarsak.MANGLER_TILGANG),
                ansvarsubject = Persondata.NavnOgIdent(null, null, Persondata.Skjulingsaarsak.MANGLER_TILGANG),
            ),
            result.person.foreldreansvar.single(),
        )
        assertEquals(forelderBarnRelasjonData.size, result.person.forelderBarnRelasjon.size)
        result.person.forelderBarnRelasjon.filter { it.rolle == Persondata.ForelderBarnRelasjonRolle.BARN }.forEach {
            assertNull(it.ident)
            assertTrue(it.navn.isEmpty())
            assertTrue(it.fodselsdato.isEmpty())
            assertTrue(it.kjonn.isEmpty())
            assertNull(it.alder)
            assertTrue(it.adressebeskyttelse.isEmpty())
            assertTrue(it.dodsdato.isEmpty())
            assertFalse(it.harSammeAdresse)
            assertEquals(Persondata.Skjulingsaarsak.MANGLER_TILGANG, it.skjulingsaarsak)
        }
        val uidentifisert = result.person.forelderBarnRelasjon.single { it.rolle == Persondata.ForelderBarnRelasjonRolle.MEDMOR }
        assertEquals(
            Persondata.ForelderBarnRelasjonRolle.MEDMOR,
            uidentifisert.rolle,
        )
        assertNull(
            uidentifisert.ident,
        )
        assertEquals(
            listOf(Persondata.Navn("Tredjepart", "Relasjon", "Fra utlandet")),
            uidentifisert.navn,
        )
        assertEquals(
            listOf(LocalDate.parse("2000-01-01")),
            uidentifisert.fodselsdato,
        )
        assertEquals(
            Persondata.Kjonn.M,
            uidentifisert.kjonn
                .single()
                .kode,
        )
        assertNull(uidentifisert.skjulingsaarsak)
        assertTrue(
            uidentifisert.adressebeskyttelse
                .isEmpty(),
        )
        assertTrue(
            uidentifisert.dodsdato
                .isEmpty(),
        )
        assertEquals(
            "",
            result.person.sivilstand
                .single()
                .sivilstandRelasjon
                ?.fnr,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.sivilstand
                .single()
                .sivilstandRelasjon
                ?.skjulingsaarsak,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.vergemal
                .single()
                .skjulingsaarsak,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.historiskeVergemal
                .single()
                .skjulingsaarsak,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.fullmektige
                .single()
                .skjulingsaarsak,
        )
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        listOf(
            "55555666000",
            "55555111000",
            "55555222000",
            "11225666000",
            "11225678910",
            "Person Vergemål",
            "Person HistoriskVergemål",
        ).forEach {
            assertFalse(json.contains(it), "Utelatt tredjeperson finnes i responsen")
        }
    }

    @Test
    fun `skjult dodsbokontakt bruker ikke innbakte persondata eller adresse`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                    skjulingsaarsaker =
                        mapOf(
                            kontaktinformasjonDodsbo.personSomKontakt!!.identifikasjonsnummer!! to
                                Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                        ),
                    persondata =
                        testPerson.copy(
                            kontaktinformasjonForDoedsbo =
                                listOf(
                                    kontaktinformasjonDodsbo.copy(
                                        personSomKontakt =
                                            requireNotNull(kontaktinformasjonDodsbo.personSomKontakt).copy(
                                                personnavn = gittPersonnavn("Hemmelig Adressat"),
                                                foedselsdato = LocalDate.parse("1990-05-06"),
                                            ),
                                    ),
                                ),
                        ),
                ),
            )
        val dodsbo = result.person.dodsbo.single()
        assertEquals(
            Persondata.PersonSomAdressat(null, emptyList(), null, Persondata.Skjulingsaarsak.MANGLER_TILGANG),
            dodsbo.adressat.personSomAdressat,
        )
        assertEquals(Persondata.Adresse(linje1 = "", sistEndret = null), dodsbo.adresse)
        val json = Serializer.jacksonObjectMapper.writeValueAsString(dodsbo)
        listOf("Hemmelig", "\"Adressat\"", "1990-05-06", "11225666000", "Gateveien", "9988", "Poststed").forEach {
            assertFalse(json.contains(it), "Skjult dødsbokontakt finnes i responsen")
        }
    }

    @Test
    fun `foreldreansvar beholder godkjent part og utelater persondata for manglende part`() {
        listOf("55333111000", "98765432100").forEach { godkjentIdent ->
            val godkjentPerson = tredjepartsPersoner.getValue(godkjentIdent)
            val result =
                mapper.flettSammenData(
                    testData.copy(
                        tredjepartsPerson = PersondataResult.of(mapOf(godkjentIdent to godkjentPerson)),
                    ),
                )
            val ansvar = result.person.foreldreansvar.single()
            val godkjent = Persondata.NavnOgIdent(godkjentPerson.navn.firstOrNull(), godkjentIdent)
            val uavklart = Persondata.NavnOgIdent(null, null, Persondata.Skjulingsaarsak.UAVKLART)

            assertEquals("felles", ansvar.ansvar)
            assertEquals(if (godkjentIdent == "55333111000") godkjent else uavklart, ansvar.ansvarlig)
            assertEquals(if (godkjentIdent == "98765432100") godkjent else uavklart, ansvar.ansvarsubject)
            val skjultIdent = if (godkjentIdent == "55333111000") "98765432100" else "55333111000"
            val json = Serializer.jacksonObjectMapper.writeValueAsString(ansvar)
            assertFalse(json.contains(skjultIdent))
            assertFalse(
                json.contains(
                    tredjepartsPersoner
                        .getValue(skjultIdent)
                        .navn
                        .first()
                        .etternavn,
                ),
            )
        }
    }

    @Test
    fun `foreldreansvar uten identifikator beholder tidligere navnemapping`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    persondata =
                        testPerson.copy(
                            foreldreansvar =
                                listOf(
                                    foreldreansvarData.copy(
                                        ansvarlig = null,
                                        ansvarssubjekt = null,
                                        ansvarligUtenIdentifikator =
                                            RelatertBiPerson(
                                                navn = gittPersonnavn("Hemmelig Forelder"),
                                                foedselsdato = LocalDate.parse("1990-05-06"),
                                                statsborgerskap = "NOR",
                                                kjoenn = KjoennType.KVINNE,
                                            ),
                                    ),
                                    foreldreansvarData.copy(
                                        ansvarlig = null,
                                        ansvarssubjekt = null,
                                    ),
                                ),
                        ),
                ),
            )

        assertEquals(2, result.person.foreldreansvar.size)
        assertEquals(
            "felles",
            result.person.foreldreansvar
                .first()
                .ansvar,
        )
        assertEquals(
            Persondata.NavnOgIdent(gittNavn("Hemmelig Forelder"), null),
            result.person.foreldreansvar
                .first()
                .ansvarlig,
        )
        assertNull(
            result.person.foreldreansvar
                .first()
                .ansvarsubject,
        )
        assertNull(
            result.person.foreldreansvar
                .last()
                .ansvarlig,
        )
        assertNull(
            result.person.foreldreansvar
                .last()
                .ansvarsubject,
        )
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        assertTrue(json.contains("Hemmelig"))
        assertTrue(json.contains("Forelder"))
        assertFalse(json.contains("1990-05-06"))
    }

    @Test
    fun `inline opplysninger om identifisert person omgar ikke tilgangskontrollen`() {
        val inlinePerson =
            RelatertBiPerson(
                navn = gittPersonnavn("Skjult Person"),
                foedselsdato = LocalDate.parse("1990-05-06"),
                statsborgerskap = "NOR",
                kjoenn = KjoennType.KVINNE,
            )
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                    persondata =
                        testPerson.copy(
                            foreldreansvar = listOf(foreldreansvarData.copy(ansvarligUtenIdentifikator = inlinePerson)),
                            forelderBarnRelasjon =
                                listOf(
                                    forelderBarnRelasjonData.first().copy(
                                        relatertPersonUtenFolkeregisteridentifikator = inlinePerson,
                                    ),
                                ),
                        ),
                ),
            )
        assertEquals(
            Persondata.NavnOgIdent(null, null, Persondata.Skjulingsaarsak.UAVKLART),
            result.person.foreldreansvar
                .single()
                .ansvarlig,
        )
        assertEquals(1, result.person.forelderBarnRelasjon.size)
        assertTrue(
            result.person.forelderBarnRelasjon
                .single()
                .navn
                .isEmpty(),
        )
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        assertFalse(json.contains("Skjult"))
        assertFalse(json.contains("1990-05-06"))
    }

    @Test
    fun `delvis tilgang beholder godkjent fullmektig og skjuler resten`() {
        val ident = fullmaktPerson.fullmektig
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(mapOf(ident to tredjepartsPersoner.getValue(ident))),
                    skjulingsaarsaker =
                        mapOf(
                            "55555111000" to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                            "55555222000" to Persondata.Skjulingsaarsak.UAVKLART,
                            "11225678910" to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                            "55333111000" to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                            "98765432100" to Persondata.Skjulingsaarsak.UAVKLART,
                        ) +
                            forelderBarnRelasjonData
                                .mapNotNull { it.relatertPersonsIdent }
                                .associateWith { Persondata.Skjulingsaarsak.UAVKLART },
                    tilgangsfeil = true,
                ),
            )
        assertEquals(listOf(ident), result.person.fullmektige.map { it.ident })
        assertEquals(
            forelderBarnRelasjonData.size,
            result.person.forelderBarnRelasjon.size,
            "Uidentifisert PDL-relasjon skal fortsatt vises",
        )
        assertEquals(
            listOf(Persondata.Navn("Tredjepart", "Relasjon", "Fra utlandet")),
            result.person.forelderBarnRelasjon
                .single { it.rolle == Persondata.ForelderBarnRelasjonRolle.MEDMOR }
                .navn,
        )
        assertEquals(
            "",
            result.person.dodsbo
                .single()
                .adresse.linje1,
        )
        assertEquals(listOf(PersondataResult.InformasjonElement.TILGANGSMASKINEN_TREDJEPARTSPERSONER), result.feilendeSystemer)
    }

    @Test
    fun `skjulingsaarsak oppgis per person uten personidentitet`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                    skjulingsaarsaker =
                        mapOf(
                            "11225678910" to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                            "55555111000" to Persondata.Skjulingsaarsak.MANGLER_TILGANG,
                            "55555222000" to Persondata.Skjulingsaarsak.UAVKLART,
                        ),
                ),
            )

        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        assertTrue(
            result.person.sivilstand
                .single()
                .sivilstandRelasjon
                ?.fnr == "",
        )
        assertNull(
            result.person.vergemal
                .single()
                .ident,
        )
        assertNull(
            result.person.historiskeVergemal
                .single()
                .ident,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.sivilstand
                .single()
                .sivilstandRelasjon
                ?.skjulingsaarsak,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.MANGLER_TILGANG,
            result.person.vergemal
                .single()
                .skjulingsaarsak,
        )
        assertEquals(
            Persondata.Skjulingsaarsak.UAVKLART,
            result.person.historiskeVergemal
                .single()
                .skjulingsaarsak,
        )
        listOf("11225678910", "55555111000", "Person Vergemål", "Person HistoriskVergemål").forEach {
            assertFalse(json.contains(it), "Skjermet identitet finnes i responsen")
        }
    }

    @Test
    fun `beholder advokat som adressat med kontaktpersonens navn`() {
        val advokatnavn = PdlPersonnavn(fornavn = "Skjermet", mellomnavn = null, etternavn = "Kontaktperson")
        val dodsbo =
            kontaktinformasjonDodsbo.copy(
                personSomKontakt = null,
                advokatSomKontakt =
                    KontaktinformasjonForDoedsboAdvokatSomKontakt(
                        personnavn = advokatnavn,
                        organisasjonsnavn = "Juridisk firma",
                        organisasjonsnummer = "123456789",
                    ),
            )

        val result =
            mapper.flettSammenData(
                testData.copy(persondata = testPerson.copy(kontaktinformasjonForDoedsbo = listOf(dodsbo))),
            )
        val adressat =
            result.person.dodsbo
                .single()
                .adressat.advokatSomAdressat

        assertEquals("Skjermet", adressat?.kontaktperson?.fornavn)
        assertEquals("Kontaktperson", adressat?.kontaktperson?.etternavn)
        assertEquals("Juridisk firma", adressat?.organisasjonsnavn)
        assertEquals("123456789", adressat?.organisasjonsnummer)
        val json = Serializer.jacksonObjectMapper.writeValueAsString(result)
        assertTrue(json.contains("Skjermet"))
        assertTrue(json.contains("Kontaktperson"))
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = fnr,
                        persondata = testPerson,
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            ),
        )
    }

    @Test
    internal fun `skal mappe opphold fra pdl`() {
        val oppholdFra = gittDato("2024-01-01")
        val oppholdTil = gittDato("2025-01-01")
        val result =
            mapper.flettSammenData(
                testData.copy(
                    persondata =
                        testPerson.copy(
                            opphold =
                                listOf(
                                    Opphold(
                                        type = Oppholdstillatelse.MIDLERTIDIG,
                                        oppholdFra = oppholdFra,
                                        oppholdTil = oppholdTil,
                                    ),
                                ),
                        ),
                ),
            )

        assertEquals(
            listOf(Persondata.Opphold(Oppholdstillatelse.MIDLERTIDIG, oppholdFra, oppholdTil)),
            result.person.opphold,
        )
    }

    @Test
    internal fun `skal mappe alle bostedsadresser fra historikken inkludert gjeldende adresse`() {
        val gjeldendeAdresse = bostedadresseData.copy(vegadresse = gittVegadresse(husnummer = "3"))
        val historiskAdresse1 =
            bostedadresseData.copy(
                metadata = bostedadresseData.metadata.copy(historisk = true),
                vegadresse = gittVegadresse(husnummer = "7"),
                gyldigFraOgMed = gittDateTime("2020-01-01T00:00:00"),
                gyldigTilOgMed = gittDateTime("2021-01-01T00:00:00"),
                angittFlyttedato = gittDato("2020-01-01"),
            )
        val historiskAdresse2 =
            bostedadresseData.copy(
                metadata = bostedadresseData.metadata.copy(historisk = true),
                vegadresse = gittVegadresse(husnummer = "12"),
                gyldigFraOgMed = gittDateTime("2018-01-01T00:00:00"),
                gyldigTilOgMed = gittDateTime("2019-01-01T00:00:00"),
                angittFlyttedato = gittDato("2018-01-01"),
            )

        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        persondata =
                            testPerson.copy(
                                bostedsadresse = listOf(gjeldendeAdresse, historiskAdresse1, historiskAdresse2),
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertEquals(1, result.person.bostedAdresse.size)
        assertEquals(
            "Vegadressestien 3",
            result.person.bostedAdresse
                .single()
                .linje1,
        )
        assertEquals(3, result.person.historiskeBostedAdresser.size)
        assertEquals(
            listOf("Vegadressestien 3", "Vegadressestien 7", "Vegadressestien 12"),
            result.person.historiskeBostedAdresser.map { it.linje1 },
        )
    }

    @Test
    internal fun `skal bruke gjeldende adresse i begge listene når historikk mangler`() {
        val result =
            mapper.flettSammenData(
                testData.copy(persondata = testPerson.copy(bostedsadresse = listOf(bostedadresseData))),
            )

        assertEquals(1, result.person.bostedAdresse.size)
        assertEquals(result.person.bostedAdresse, result.person.historiskeBostedAdresser)
    }

    @Test
    internal fun `skal ikke vise historiske adresser som gjeldende`() {
        val historiskAdresse =
            bostedadresseData.copy(metadata = bostedadresseData.metadata.copy(historisk = true))
        val result =
            mapper.flettSammenData(
                testData.copy(persondata = testPerson.copy(bostedsadresse = listOf(historiskAdresse))),
            )

        assertTrue(result.person.bostedAdresse.isEmpty())
        assertEquals(1, result.person.historiskeBostedAdresser.size)
    }

    @Test
    internal fun `skal ha tomme adresselister når pdl ikke returnerer bostedsadresse`() {
        val result =
            mapper.flettSammenData(
                testData.copy(persondata = testPerson.copy(bostedsadresse = emptyList())),
            )

        assertTrue(result.person.bostedAdresse.isEmpty())
        assertTrue(result.person.historiskeBostedAdresser.isEmpty())
    }

    @Test
    internal fun `skal mappe data fra pdl til Persondata når person er dod`() {
        snapshot.assertMatches(
            mapper.flettSammenData(
                data =
                    testData
                        .copy(
                            personIdent = fnr,
                            persondata =
                                testPerson.copy(
                                    doedsfall =
                                        listOf(
                                            Doedsfall(
                                                gittDato("2010-01-02"),
                                                metadata =
                                                    Metadata(
                                                        master = "Freg",
                                                        endringer = emptyList(),
                                                    ),
                                            ),
                                        ),
                                ),
                            geografiskeTilknytning = PersondataResult.NotRelevant(),
                            navEnhet = PersondataResult.NotRelevant(),
                        ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            ),
        )
    }

    @Test
    internal fun `skal filtrere ut egenAnsatt fra feiledeSystemer når veileder ikke har tilgang`() {
        snapshot.assertMatches(
            mapper
                .flettSammenData(
                    data =
                        testData.copy(
                            personIdent = fnr,
                            persondata = testPerson,
                            krrData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                            erEgenAnsatt =
                                PersondataResult.Failure(
                                    PersondataResult.InformasjonElement.EGEN_ANSATT,
                                    Throwable(),
                                ),
                            harTilgangTilSkjermetPerson = false,
                        ),
                    clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
                ).feilendeSystemer,
        )
    }

    @Test
    internal fun `skal ikke filtrere ut egenAnsatt fra feiledeSystemer når veileder har tilgang`() {
        snapshot.assertMatches(
            mapper
                .flettSammenData(
                    data =
                        testData.copy(
                            personIdent = fnr,
                            persondata = testPerson,
                            krrData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable()),
                            erEgenAnsatt =
                                PersondataResult.Failure(
                                    PersondataResult.InformasjonElement.EGEN_ANSATT,
                                    Throwable(),
                                ),
                            harTilgangTilSkjermetPerson = true,
                        ),
                    clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
                ).feilendeSystemer,
        )
    }

    @Test
    internal fun `Publikumsmottak med samme poststed som brukers bostedpoststed skal være først i lista`() {
        val mottak3 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Testgate",
                        husnummer = "1",
                        husbokstav = null,
                        postnummer = "0010",
                        poststed = "Oslo",
                    ),
                apningstider = emptyList(),
            )

        val mottak2 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Bergensgate",
                        husnummer = "2",
                        husbokstav = null,
                        postnummer = "1532",
                        poststed = "Test",
                    ),
                apningstider = emptyList(),
            )

        val mottak1 =
            NorgDomain.Publikumsmottak(
                besoksadresse =
                    NorgDomain.Gateadresse(
                        gatenavn = "Random navn",
                        husnummer = "20",
                        husbokstav = null,
                        postnummer = "1444",
                        poststed = "TestPoststed",
                    ),
                apningstider = emptyList(),
            )

        val enhetKontaktinfo =
            NorgDomain.EnhetKontaktinformasjon(
                enhet =
                    NorgDomain.Enhet(
                        enhetId = "0123",
                        enhetNavn = "NAV Test",
                        oppgavebehandler = true,
                        status = NorgDomain.EnhetStatus.AKTIV,
                    ),
                overordnetEnhet = EnhetId("0001"),
                publikumsmottak = listOf(mottak2, mottak1, mottak3),
            )

        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        navEnhet =
                            PersondataResult.Success(
                                name = PersondataResult.InformasjonElement.NORG_NAVKONTOR,
                                value = enhetKontaktinfo,
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        snapshot.assertMatches(result)
    }

    @Test
    internal fun `skal beholde adressat når identifikasjonsnummer mangler`() {
        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = fnr,
                        persondata =
                            testPerson.copy(
                                kontaktinformasjonForDoedsbo = listOf(kontaktinformasjonDodsboUtenIdentifikasjonsnummer),
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        val adressat =
            result.person.dodsbo
                .single()
                .adressat.personSomAdressat
        assertNull(adressat?.fnr)
        assertEquals(listOf(gittNavn("Ola Nordmann")), adressat?.navn)
        assertEquals(LocalDate.parse("1960-01-01"), adressat?.fodselsdato)
        assertNull(adressat?.skjulingsaarsak)
    }

    @Test
    fun `vergemal uten ident beholder tidligere navn og metadata`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                    persondata =
                        testPerson.copy(
                            vergemaalEllerFremtidsfullmakt =
                                listOf(
                                    vergemal.copy(
                                        vergeEllerFullmektig = vergemal.vergeEllerFullmektig.copy(motpartsPersonident = null),
                                    ),
                                    historiskVergemal.copy(
                                        vergeEllerFullmektig = historiskVergemal.vergeEllerFullmektig.copy(motpartsPersonident = null),
                                    ),
                                ),
                        ),
                ),
            )

        val gjeldende = result.person.vergemal.single()
        val historisk = result.person.historiskeVergemal.single()
        assertNull(gjeldende.ident)
        assertNull(historisk.ident)
        assertEquals(gittNavn("Person Vergemål"), gjeldende.navn)
        assertEquals(gittNavn("Person HistoriskVergemål"), historisk.navn)
        assertFalse(gjeldende.historisk)
        assertTrue(historisk.historisk)
        assertEquals("Voksen", gjeldende.vergesakstype)
    }

    @Test
    fun `organisasjon som adressat beholder kontaktpersonens navn`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    persondata =
                        testPerson.copy(
                            kontaktinformasjonForDoedsbo =
                                listOf(
                                    kontaktinformasjonDodsbo.copy(
                                        personSomKontakt = null,
                                        organisasjonSomKontakt =
                                            KontaktinformasjonForDoedsboOrganisasjonSomKontakt(
                                                organisasjonsnavn = "Testorganisasjon",
                                                organisasjonsnummer = "123456789",
                                                kontaktperson = gittPersonnavn("Ola Nordmann"),
                                            ),
                                    ),
                                ),
                        ),
                ),
            )
        val adressat =
            result.person.dodsbo
                .single()
                .adressat.organisasjonSomAdressat
        assertEquals(gittNavn("Ola Nordmann"), adressat?.kontaktperson)
        assertEquals("Testorganisasjon", adressat?.organisasjonsnavn)
        assertEquals("123456789", adressat?.organisasjonsnummer)
    }

    @Test
    fun `forelder barn uten ident eller inline person beholder relasjonsrollen`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    persondata =
                        testPerson.copy(
                            forelderBarnRelasjon =
                                listOf(
                                    forelderBarnRelasjonData.first().copy(
                                        relatertPersonsIdent = null,
                                        relatertPersonUtenFolkeregisteridentifikator = null,
                                    ),
                                ),
                        ),
                ),
            )
        val relasjon = result.person.forelderBarnRelasjon.single()
        assertEquals(Persondata.ForelderBarnRelasjonRolle.BARN, relasjon.rolle)
        assertNull(relasjon.ident)
        assertTrue(relasjon.navn.isEmpty())
    }

    @Test
    internal fun `KRR-feil skal legges til feilendeSystemer og gi tom kontaktinformasjon`() {
        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        krrData = PersondataResult.Failure(PersondataResult.InformasjonElement.DKIF, Throwable("KRR nede")),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertTrue(result.feilendeSystemer.contains(PersondataResult.InformasjonElement.DKIF))
        assertNull(result.person.kontaktInformasjon.epost)
        assertNull(result.person.kontaktInformasjon.mobil)
        assertNull(result.person.kontaktInformasjon.erReservert)
    }

    @Test
    internal fun `bankkonto-feil skal legges til feilendeSystemer og gi null bankkonto`() {
        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        bankkonto =
                            PersondataResult.Failure(
                                PersondataResult.InformasjonElement.BANKKONTO,
                                Throwable("Kontoregister nede"),
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertTrue(result.feilendeSystemer.contains(PersondataResult.InformasjonElement.BANKKONTO))
        assertNull(result.person.bankkonto)
    }

    @Test
    internal fun `repr-api-feil skal legges til feilendeSystemer`() {
        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        fullmektige =
                            PersondataResult.Failure(
                                PersondataResult.InformasjonElement.REPR_API,
                                Throwable("repr-api nede"),
                            ),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertTrue(result.feilendeSystemer.contains(PersondataResult.InformasjonElement.REPR_API))
        assertTrue(result.person.fullmektige.isEmpty())
    }

    @Test
    internal fun `ingen repr-fullmakter gir tomme fullmektige uten feil`() {
        val result = mapper.flettSammenData(testData.copy(fullmektige = PersondataResult.of(emptyList())))

        assertTrue(result.person.fullmektige.isEmpty())
        assertTrue(PersondataResult.InformasjonElement.REPR_API !in result.feilendeSystemer)
    }

    @Test
    internal fun `returnerer ett fullmektig-objekt per fullmakt`() {
        val fremtidigFullmakt = fullmaktPerson.copy(gyldigFraOgMed = gittDato("2030-01-01"), gyldigTilOgMed = null)
        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        fullmektige =
                            PersondataResult.runCatching(PersondataResult.InformasjonElement.REPR_API) {
                                listOf(fullmaktPerson, fremtidigFullmakt)
                            },
                    ),
            )

        assertEquals(
            listOf("55555666000", "55555666000"),
            result.person.fullmektige.map { it.ident },
        )
        assertEquals(
            listOf(
                forventetFullmakt,
                forventetFullmakt.copy(gyldigFraOgMed = gittDato("2030-01-01"), gyldigTilOgMed = null),
            ),
            result.person.fullmektige.map { it.fullmakt },
        )
    }

    @Test
    internal fun `repr-fullmektig faar kontaktinfo bare ved tilgjengelig tredjepartsperson`() {
        val result =
            mapper.flettSammenData(
                testData.copy(
                    fullmektige = PersondataResult.of(listOf(fullmaktPerson)),
                ),
            )
        assertEquals(
            "55555666000",
            result.person.fullmektige
                .single()
                .ident,
        )
        assertEquals(
            kontaktinformasjonTredjepartsperson,
            result.person.fullmektige
                .single()
                .digitalKontaktinformasjonTredjepartsperson,
        )

        val utenPerson =
            mapper.flettSammenData(
                testData.copy(
                    tredjepartsPerson = PersondataResult.of(emptyMap()),
                ),
            )
        assertEquals(
            "",
            utenPerson.person.fullmektige
                .single()
                .ident,
        )
        assertNull(
            utenPerson.person.fullmektige
                .single()
                .navn,
        )
        assertNull(
            utenPerson.person.fullmektige
                .single()
                .digitalKontaktinformasjonTredjepartsperson,
        )
    }

    @Test
    internal fun `skal skille historiske vergemal fra gjeldende vergemal`() {
        val result =
            mapper.flettSammenData(
                data = testData.copy(personIdent = fnr, persondata = testPerson),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertEquals(1, result.person.vergemal.size)
        assertEquals(
            false,
            result.person.vergemal
                .first()
                .historisk,
        )
        assertEquals(
            "55555111000",
            result.person.vergemal
                .first()
                .ident,
        )

        assertEquals(1, result.person.historiskeVergemal.size)
        assertEquals(
            true,
            result.person.historiskeVergemal
                .first()
                .historisk,
        )
        assertEquals(
            "55555222000",
            result.person.historiskeVergemal
                .first()
                .ident,
        )
    }

    @Test
    internal fun `historisk skal hentes fra metadata og ikke utledes fra gyldighetsperiode`() {
        val avsluttetMenGjeldende =
            vergemal.copy(
                metadata = Metadata2(historisk = false),
                folkeregistermetadata =
                    Folkeregistermetadata(
                        gyldighetstidspunkt = gittDateTime("2010-02-02T00:00:00"),
                        opphoerstidspunkt = gittDateTime("2015-02-02T00:00:00"),
                    ),
            )

        val result =
            mapper.flettSammenData(
                data =
                    testData.copy(
                        personIdent = fnr,
                        persondata = testPerson.copy(vergemaalEllerFremtidsfullmakt = listOf(avsluttetMenGjeldende)),
                    ),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertEquals(1, result.person.vergemal.size)
        assertTrue(result.person.historiskeVergemal.isEmpty())
    }

    @Test
    internal fun `historiske vergemal skal fa navn fra tredjepartsoppslag`() {
        val result =
            mapper.flettSammenData(
                data = testData.copy(personIdent = fnr, persondata = testPerson),
                clock = Clock.fixed(Instant.parse("2021-10-10T12:00:00.000Z"), ZoneId.systemDefault()),
            )

        assertEquals(
            "HistoriskVergemål",
            result.person.historiskeVergemal
                .first()
                .navn
                ?.etternavn,
        )
    }
}
