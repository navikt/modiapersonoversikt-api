package no.nav.modiapersonoversikt.service.enhetligkodeverk

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class EnhetligKodeverkServiceImplTest {

    val mock: KodeverkProviders = mockk()

    @Test
    fun `sikre at kodeverk blir hentet ut`() {
        every { mock.fraFellesKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(emptyMap())

        val service = EnhetligKodeverkServiceImpl(mock)

        val kodeverk = service.hentKodeverk(KodeverkConfig.LAND)
    }
}
