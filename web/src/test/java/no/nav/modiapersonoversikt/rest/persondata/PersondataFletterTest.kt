package no.nav.modiapersonoversikt.rest.persondata

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.testutils.SnapshotExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

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
                "GIFT" to "Gift",
                "1444" to "TestPoststed",
                "47" to "+47"
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
}
