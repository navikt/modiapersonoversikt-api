package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentTredjepartspersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentTredjepartspersondata.AdressebeskyttelseGradering
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentTredjepartspersondata.Bostedsadresse
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TredjepartspersonMapperTest {
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension()

    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = TredjepartspersonMapper(kodeverk)

    @BeforeEach
    internal fun setUp() {
        every { kodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk("kodeverk", emptyMap())
    }

    @Test
    internal fun `skal mapper ulike adresse typer til internt domene`() {
        val tilganger = gittTilganger(kode6 = false, kode7 = false)
        val person = gittPerson(
            navn = "Aremark Testfamilien",
            adressebeskyttelse = AdressebeskyttelseGradering.UGRADERT,
            bosted = ukjentBosted("Ett sted i Aremark")
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = person
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = person.copy(bostedsadresse = vegadresse())
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = person.copy(bostedsadresse = matrikkel())
            )
        )
    }

    @Test
    internal fun `skal fjerne informasjon relatert til kode 6-7 ved manglende tilgang`() {
        val tilganger = gittTilganger(kode6 = false, kode7 = false)
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = gittPerson(
                    adressebeskyttelse = AdressebeskyttelseGradering.FORTROLIG,
                    bosted = ukjentBosted("Ett sted i Aremark")
                )
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = gittPerson(
                    adressebeskyttelse = AdressebeskyttelseGradering.STRENGT_FORTROLIG,
                    bosted = ukjentBosted("Ett sted i Aremark")
                )
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                ident = "00000000000",
                tilganger = tilganger,
                person = gittPerson(
                    adressebeskyttelse = AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND,
                    bosted = ukjentBosted("Ett sted i Aremark")
                )
            )
        )
    }

    private fun gittPerson(
        navn: String = "Harry Tester Testesen",
        adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT,
        bosted: List<Bostedsadresse> = ukjentBosted("Ingen ved hvor harry bor")
    ) = HentTredjepartspersondata.Person(
        navn = listOf(gittNavn(navn)),
        adressebeskyttelse = listOf(
            HentTredjepartspersondata.Adressebeskyttelse(adressebeskyttelse)
        ),
        bostedsadresse = bosted,
        kjoenn = emptyList(),
        foedsel = emptyList(),
        folkeregisterpersonstatus = emptyList(),
        doedsfall = emptyList()
    )

    private fun gittTilganger(kode6: Boolean, kode7: Boolean) = PersondataService.Tilganger(kode6, kode7)

    private fun gittNavn(navn: String): HentTredjepartspersondata.Navn {
        val split = navn.split(" ")
        return HentTredjepartspersondata.Navn(
            fornavn = split.first(),
            mellomnavn = if (split.size <= 2) null else {
                split.subList(1, split.size - 1).joinToString(" ")
            },
            etternavn = split.last(),
            forkortetNavn = null,
            originaltNavn = null
        )
    }

    private val adresse = Bostedsadresse(
        folkeregistermetadata = null,
        vegadresse = null,
        matrikkeladresse = null,
        utenlandskAdresse = null,
        ukjentBosted = null
    )

    private fun vegadresse(
        husnummer: String? = "13",
        husbokstav: String? = "A",
        bruksenhetsnummer: String? = "01654A",
        adressenavn: String? = "Gutua",
        kommunenummer: String? = "Skauen",
        bydelsnummer: String? = "000001",
        tilleggsnavn: String? = "Gutu Gard",
        postnummer: String? = "1234"
    ) = listOf(
        adresse.copy(
            vegadresse = HentTredjepartspersondata.Vegadresse(
                husnummer = husnummer,
                husbokstav = husbokstav,
                bruksenhetsnummer = bruksenhetsnummer,
                adressenavn = adressenavn,
                kommunenummer = kommunenummer,
                bydelsnummer = bydelsnummer,
                tilleggsnavn = tilleggsnavn,
                postnummer = postnummer
            )
        )
    )

    private fun matrikkel(
        bruksenhetsnummer: String? = "000001",
        tilleggsnavn: String? = "Gutu Gard",
        postnummer: String? = "1234",
        kommunenummer: String? = "Skauen"
    ) = listOf(
        adresse.copy(
            matrikkeladresse = HentTredjepartspersondata.Matrikkeladresse(
                bruksenhetsnummer = bruksenhetsnummer,
                tilleggsnavn = tilleggsnavn,
                postnummer = postnummer,
                kommunenummer = kommunenummer
            )
        )
    )

    private fun ukjentBosted(bosted: String) = listOf(
        adresse.copy(
            ukjentBosted = HentTredjepartspersondata.UkjentBosted(bosted)
        )
    )
}
