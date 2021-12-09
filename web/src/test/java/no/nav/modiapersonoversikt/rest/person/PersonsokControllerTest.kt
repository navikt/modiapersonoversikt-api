package no.nav.modiapersonoversikt.rest.person

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.SokPerson
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService.PdlSokbareFelt
import no.nav.modiapersonoversikt.legacy.api.service.pdl.PdlOppslagService.SokKriterier
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import no.nav.tjeneste.virksomhet.personsoek.v1.informasjon.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
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

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.NAVN, "Fornavn Etternavn"))
        }

        @Test
        internal fun `filtrerer bort navne-felt som er null`() {
            val bareFornavn = request
                .copy(fornavn = "Fornavn")
                .tilPdlKriterier(clock)
            val bareEtternavn = request
                .copy(etternavn = "Etternavn")
                .tilPdlKriterier(clock)

            assertThat(bareFornavn).contains(SokKriterier(PdlSokbareFelt.NAVN, "Fornavn"))
            assertThat(bareEtternavn).contains(SokKriterier(PdlSokbareFelt.NAVN, "Etternavn"))
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

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.ADRESSE, "Gatenavn 1 A 0100"))
        }

        @Test
        internal fun `regner ut tidligste dato basert på alderFra`() {
            val kriterier = request
                .copy(alderFra = 30)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.FODSELSDATO_TIL, "1990-12-02"))
        }

        @Test
        internal fun `regner ut seneste dato basert på alderTil`() {
            val kriterier = request
                .copy(alderTil = 32)
                .tilPdlKriterier(clock)

            assertThat(kriterier).contains(SokKriterier(PdlSokbareFelt.FODSELSDATO_FRA, "1987-12-03"))
        }

        @Test
        internal fun `mapper kjønn til pdl-format`() {
            val mann = request
                .copy(kjonn = "M")
                .tilPdlKriterier(clock)

            assertThat(mann).contains(SokKriterier(PdlSokbareFelt.KJONN, "MANN"))

            val kvinne = request
                .copy(kjonn = "K")
                .tilPdlKriterier(clock)

            assertThat(kvinne).contains(SokKriterier(PdlSokbareFelt.KJONN, "KVINNE"))

            val ukjent = request
                .copy(kjonn = "U")
                .tilPdlKriterier(clock)

            assertThat(ukjent).contains(SokKriterier(PdlSokbareFelt.KJONN, null))
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
