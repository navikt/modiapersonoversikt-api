package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.mockk
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite.AdressebeskyttelseGradering
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite.Bostedsadresse
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class TredjepartspersonMapperTest {
    @JvmField
    @RegisterExtension
    val snapshot = SnapshotExtension(debug = true)

    val kodeverk: EnhetligKodeverk.Service = mockk()
    val mapper = TredjepartspersonMapper(kodeverk)

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
                tilganger = tilganger, person = person.asBolkResult()
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                tilganger = tilganger, person = person
                    .copy(bostedsadresse = vegadresse())
                    .asBolkResult()
            )
        )
        snapshot.assertMatches(
            mapper.lagTredjepartsperson(
                tilganger = tilganger, person = person
                    .copy(bostedsadresse = matrikkel())
                    .asBolkResult()
            )
        )

    }

    @Test
    internal fun `skal fjerne informasjon relatert til kode 6-7 ved manglende tilgang`() {
        TODO("Not yet implemented")
    }

    @Test
    internal fun `skal ikke stoppe ved feil i kodeverk`() {
        TODO("Not yet implemented")
    }

    private fun gittPerson(
        navn: String = "Harry Tester Testesen",
        adressebeskyttelse: AdressebeskyttelseGradering = AdressebeskyttelseGradering.UGRADERT,
        bosted: List<Bostedsadresse> = ukjentBosted("Ingen ved hvor harry bor")
    ) = HentPersondataLite.Person(
        navn = listOf(gittNavn(navn)),
        adressebeskyttelse = listOf(
            HentPersondataLite.Adressebeskyttelse(adressebeskyttelse)
        ),
        bostedsadresse = bosted
    )

    private fun HentPersondataLite.Person.asBolkResult(fnr: String = "0".repeat(11)) =
        HentPersondataLite.HentPersonBolkResult(
            ident = fnr,
            person = this
        )

    private fun gittTilganger(kode6: Boolean, kode7: Boolean) = PersondataService.Tilganger(kode6, kode7)

    private fun gittNavn(navn: String): HentPersondataLite.Navn {
        val split = navn.split(" ")
        return HentPersondataLite.Navn(
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
            vegadresse = HentPersondataLite.Vegadresse(
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
            matrikkeladresse = HentPersondataLite.Matrikkeladresse(
                bruksenhetsnummer = bruksenhetsnummer,
                tilleggsnavn = tilleggsnavn,
                postnummer = postnummer,
                kommunenummer = kommunenummer
            )
        )
    )

    private fun ukjentBosted(bosted: String) = listOf(
        adresse.copy(
            ukjentBosted = HentPersondataLite.UkjentBosted(bosted)
        )
    )

}