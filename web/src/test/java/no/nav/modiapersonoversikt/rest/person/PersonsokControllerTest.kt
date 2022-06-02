package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.consumer.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.service.pdl.PdlOppslagService.*
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.math.BigInteger
import java.time.*
import javax.xml.datatype.DatatypeFactory

class PersonsokControllerTest {
    @Nested
    inner class PersonResponseMapper {
        @JvmField
        @RegisterExtension
        val snapshot = SnapshotExtension()

        val FIXED_DATE = DatatypeFactory
            .newInstance()
            .newXMLGregorianCalendarDate(2020, 10, 12, 0)

        @Test
        internal fun `should map tps response`() {
            val person = Bruker().apply {
                diskresjonskode = Diskresjonskoder().apply {
                    kodeverksRef = "kodeverkref"
                    kodeRef = "SPSF"
                    value = "SPSF"
                }
                postadresse = Postadresse().apply {
                    ustrukturertAdresse = UstrukturertAdresse().apply {
                        adresselinje1 = "adresselinje1"
                        adresselinje2 = "adresselinje2"
                        adresselinje3 = "adresselinje3"
                        adresselinje4 = "adresselinje4"
                        landkode = Landkoder().apply {
                            kodeverksRef = "landkodeRef"
                            kodeRef = "NOR"
                            value = "NOR"
                        }
                    }
                }
                bostedsadresse = Bostedsadresse().apply {
                    strukturertAdresse = Gateadresse().apply {
                        gatenummer = BigInteger.TWO
                        gatenavn = "Supervegen"
                        husnummer = BigInteger.TEN
                        husbokstav = "Z"
                        poststed = Postnummer().apply {
                            kodeverksRef = "postnummerRef"
                            kodeRef = "1234"
                            value = "Svingen"
                        }
                        bolignummer = "1234"
                        kommunenummer = "654321"
                    }
                }
                kjoenn = Kjoenn().apply {
                    kjoenn = Kjoennstyper().apply {
                        kodeverksRef = "kjonnRef"
                        kodeRef = "M"
                        value = "M"
                    }
                }
                personnavn = Personnavn().apply {
                    fornavn = "fornavn"
                    mellomnavn = "mellomnavn"
                    etternavn = "etternavn"
                    sammensattNavn = "fornavn mellomnavn etternavn"
                }
                personstatus = Personstatus().apply {
                    personstatus = Personstatuser().apply {
                        kodeverksRef = "personstatusRef"
                        kodeRef = "DOD"
                        value = "DOD"
                    }
                }
                ident = NorskIdent().apply {
                    ident = "12345679810"
                    type = Personidenter().apply {
                        kodeverksRef = "personidenterRef"
                        kodeRef = "FNR"
                        value = "FNR"
                    }
                }
                gjeldendePostadresseType = Postadressetyper().apply {
                    kodeverksRef = "postadresseRef"
                    kodeRef = "postadresseRef"
                    value = "NA"
                }
                midlertidigPostadresse = MidlertidigPostadresseNorge().apply {
                    ustrukturertAdresse = UstrukturertAdresse().apply {
                        adresselinje1 = "adresselinje1"
                        adresselinje2 = "adresselinje2"
                        adresselinje3 = "adresselinje3"
                        adresselinje4 = "adresselinje4"
                        landkode = Landkoder().apply {
                            kodeverksRef = "landkodeRef"
                            kodeRef = "NOR"
                            value = "NOR"
                        }
                    }
                    postleveringsPeriode = Gyldighetsperiode().apply {
                        fom = FIXED_DATE
                        tom = FIXED_DATE
                    }
                }
                harAnsvarligEnhet = AnsvarligEnhet().apply {
                    enhet = Organisasjonsenhet().apply {
                        organisasjonselementID = "1234"
                    }
                }
            }

            snapshot.assertMatches(lagPersonResponse(person))
        }

        @Test
        internal fun `should map pdl response`() {
            val person = SokPerson.PersonSearchHit(
                score = 1.0f,
                person = SokPerson.Person(
                    navn = listOf(
                        SokPerson.Navn(
                            fornavn = "fornavn",
                            mellomnavn = "mellomnavn",
                            etternavn = "etternavn",
                            forkortetNavn = null,
                            originaltNavn = null
                        )
                    ),
                    kjoenn = listOf(
                        SokPerson.Kjoenn(
                            SokPerson.KjoennType.KVINNE
                        )
                    ),
                    utenlandskIdentifikasjonsnummer = listOf(
                        SokPerson.UtenlandskIdentifikasjonsnummer(
                            identifikasjonsnummer = "987654-987",
                            utstederland = "SWE",
                            opphoert = false
                        )
                    ),
                    folkeregisteridentifikator = listOf(
                        SokPerson.Folkeregisteridentifikator(
                            identifikasjonsnummer = "12345678910",
                            status = "AKTIV",
                            type = "FNR"
                        )
                    ),
                    kontaktadresse = listOf(
                        SokPerson.Kontaktadresse(
                            gyldigFraOgMed = null,
                            gyldigTilOgMed = null,
                            vegadresse = SokPerson.Vegadresse(
                                husbokstav = "Z",
                                husnummer = "10",
                                bruksenhetsnummer = null,
                                adressenavn = "Supervegen",
                                kommunenummer = "654321",
                                postnummer = "1234",
                                bydelsnummer = null,
                                tilleggsnavn = null
                            ),
                            postboksadresse = null,
                            postadresseIFrittFormat = null,
                            utenlandskAdresse = null,
                            utenlandskAdresseIFrittFormat = null
                        )
                    ),
                    bostedsadresse = listOf(
                        SokPerson.Bostedsadresse(
                            gyldigFraOgMed = null,
                            gyldigTilOgMed = null,
                            matrikkeladresse = SokPerson.Matrikkeladresse(
                                bruksenhetsnummer = "123101",
                                tilleggsnavn = "Supergården",
                                postnummer = "1234",
                                kommunenummer = "654321"
                            ),
                            vegadresse = null,
                            utenlandskAdresse = null,
                            ukjentBosted = null
                        )
                    )
                )
            )

            snapshot.assertMatches(lagPersonResponse(person))
        }

        @Test
        internal fun `should map pdl response when address(es) is invalid and filter them out`() {
            val person = SokPerson.PersonSearchHit(
                score = 1.0f,
                person = SokPerson.Person(
                    navn = listOf(
                        SokPerson.Navn(
                            fornavn = "fornavn",
                            mellomnavn = "mellomnavn",
                            etternavn = "etternavn",
                            forkortetNavn = null,
                            originaltNavn = null
                        )
                    ),
                    kjoenn = listOf(
                        SokPerson.Kjoenn(
                            SokPerson.KjoennType.KVINNE
                        )
                    ),
                    utenlandskIdentifikasjonsnummer = listOf(
                        SokPerson.UtenlandskIdentifikasjonsnummer(
                            identifikasjonsnummer = "987654-987",
                            utstederland = "SWE",
                            opphoert = false
                        )
                    ),
                    folkeregisteridentifikator = listOf(
                        SokPerson.Folkeregisteridentifikator(
                            identifikasjonsnummer = "12345678910",
                            status = "AKTIV",
                            type = "FNR"
                        )
                    ),
                    kontaktadresse = listOf(
                        SokPerson.Kontaktadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2008, 11, 20, 12, 0)),
                            gyldigTilOgMed = SokPerson.DateTime(LocalDateTime.of(2012, 6, 30, 12, 0)),
                            vegadresse = SokPerson.Vegadresse(
                                husbokstav = "Z",
                                husnummer = "10",
                                bruksenhetsnummer = null,
                                adressenavn = "Supervegen",
                                kommunenummer = "654321",
                                postnummer = "1234",
                                bydelsnummer = null,
                                tilleggsnavn = null
                            ),
                            postboksadresse = null,
                            postadresseIFrittFormat = null,
                            utenlandskAdresse = null,
                            utenlandskAdresseIFrittFormat = null
                        ),
                        SokPerson.Kontaktadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2013, 3, 22, 12, 0)),
                            gyldigTilOgMed = SokPerson.DateTime(LocalDateTime.of(2024, 6, 30, 12, 0)),
                            vegadresse = SokPerson.Vegadresse(
                                husbokstav = "Z",
                                husnummer = "10",
                                bruksenhetsnummer = null,
                                adressenavn = "Klarevannveien",
                                kommunenummer = "654321",
                                postnummer = "1234",
                                bydelsnummer = null,
                                tilleggsnavn = null
                            ),
                            postboksadresse = null,
                            postadresseIFrittFormat = null,
                            utenlandskAdresse = null,
                            utenlandskAdresseIFrittFormat = null
                        ),
                        SokPerson.Kontaktadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2018, 11, 20, 12, 0)),
                            gyldigTilOgMed = null,
                            vegadresse = SokPerson.Vegadresse(
                                husbokstav = "Z",
                                husnummer = "10",
                                bruksenhetsnummer = null,
                                adressenavn = "Testerveien",
                                kommunenummer = "654321",
                                postnummer = "1234",
                                bydelsnummer = null,
                                tilleggsnavn = null
                            ),
                            postboksadresse = null,
                            postadresseIFrittFormat = null,
                            utenlandskAdresse = null,
                            utenlandskAdresseIFrittFormat = null
                        )
                    ),
                    bostedsadresse = listOf(
                        SokPerson.Bostedsadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2008, 11, 20, 12, 0)),
                            gyldigTilOgMed = SokPerson.DateTime(LocalDateTime.of(2018, 6, 30, 12, 0)),
                            matrikkeladresse = SokPerson.Matrikkeladresse(
                                bruksenhetsnummer = "123101",
                                tilleggsnavn = "Supergården",
                                postnummer = "1234",
                                kommunenummer = "654321"
                            ),
                            vegadresse = null,
                            utenlandskAdresse = null,
                            ukjentBosted = null
                        ),
                        SokPerson.Bostedsadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2008, 11, 20, 12, 0)),
                            gyldigTilOgMed = null,
                            matrikkeladresse = SokPerson.Matrikkeladresse(
                                bruksenhetsnummer = "123101",
                                tilleggsnavn = "Kokelikogården",
                                postnummer = "1234",
                                kommunenummer = "654321"
                            ),
                            vegadresse = null,
                            utenlandskAdresse = null,
                            ukjentBosted = null
                        ),
                        SokPerson.Bostedsadresse(
                            gyldigFraOgMed = SokPerson.DateTime(LocalDateTime.of(2018, 11, 20, 12, 0)),
                            gyldigTilOgMed = SokPerson.DateTime(LocalDateTime.of(2032, 6, 30, 12, 0)),
                            matrikkeladresse = SokPerson.Matrikkeladresse(
                                bruksenhetsnummer = "123101",
                                tilleggsnavn = "Kirkegården",
                                postnummer = "1234",
                                kommunenummer = "654321"
                            ),
                            vegadresse = null,
                            utenlandskAdresse = null,
                            ukjentBosted = null
                        )
                    )
                )
            )

            snapshot.assertMatches(lagPersonResponse(person))
        }
    }

    @Nested
    inner class PdlKriterierMapper {
        val clock = Clock.fixed(
            Instant.parse("2020-12-02T12:00:00.00Z"),
            ZoneId.systemDefault()
        )

        @Test
        internal fun `samler navne-felt til ett felt`() {
            val kriterier = request
                .copy(fornavn = "Fornavn", etternavn = "Etternavn")
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.NAVN, "Fornavn Etternavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
        }

        @Test
        internal fun `filtrerer bort navne-felt som er null`() {
            val bareFornavn = request
                .copy(fornavn = "Fornavn")
                .tilPdlKriterier(clock)
            val bareEtternavn = request
                .copy(etternavn = "Etternavn")
                .tilPdlKriterier(clock)

            assertThat(bareFornavn).contains(PdlKriterie(PdlFelt.NAVN, "Fornavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
            assertThat(bareEtternavn).contains(PdlKriterie(PdlFelt.NAVN, "Etternavn", searchHistorical = PdlSokeOmfang.HISTORISK_OG_GJELDENDE))
        }

        @Test
        internal fun `samler adresse-felt til ett felt`() {
            val kriterier = request
                .copy(
                    gatenavn = "Gatenavn",
                    husnummer = 1,
                    husbokstav = "A",
                    postnummer = "0100"
                )
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.ADRESSE, "Gatenavn 1 A 0100", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `regner ut tidligste dato basert på alderFra`() {
            val kriterier = request
                .copy(alderFra = 30)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.FODSELSDATO_TIL, "1990-12-02", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `regner ut seneste dato basert på alderTil`() {
            val kriterier = request
                .copy(alderTil = 32)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(PdlKriterie(PdlFelt.FODSELSDATO_FRA, "1987-12-03", searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        @Test
        internal fun `mapper kjønn til pdl-format`() {
            val mann = request
                .copy(kjonn = "M")
                .tilPdlKriterier(clock)

            assertThat(mann).contains(PdlKriterie(PdlFelt.KJONN, "MANN", searchHistorical = PdlSokeOmfang.GJELDENDE))

            val kvinne = request
                .copy(kjonn = "K")
                .tilPdlKriterier(clock)

            assertThat(kvinne).contains(PdlKriterie(PdlFelt.KJONN, "KVINNE", searchHistorical = PdlSokeOmfang.GJELDENDE))

            val ukjent = request
                .copy(kjonn = "U")
                .tilPdlKriterier(clock)

            assertThat(ukjent).contains(PdlKriterie(PdlFelt.KJONN, null, searchHistorical = PdlSokeOmfang.GJELDENDE))
        }

        val request = PersonsokRequest(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
    }
}
