package no.nav.modiapersonoversikt.service.enhetligkodeverk

import io.mockk.*
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.modiapersonoversikt.utils.MutableClock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.*
import java.util.*

internal class EnhetligKodeverkServiceImplTest {
    @Test
    fun `sikre at kodeverk blir hentet ut`() {
        val providers = withProvidersMock()
        val service = EnhetligKodeverkServiceImpl(providers)

        val landkoder = service.hentKodeverk(KodeverkConfig.LAND)
        assertThat(landkoder).isNotNull

        val temagrupper = service.hentKodeverk(KodeverkConfig.SF_TEMAGRUPPER)
        assertThat(temagrupper).isNotNull

        val landkode = landkoder.hentVerdi("NO", "NO")
        assertThat(landkode).isEqualTo("Norge")

        val temagruppe = temagrupper.hentVerdi("ARBD", "ARBD")
        assertThat(temagruppe).isEqualTo("Arbeid")
    }

    @Test
    internal fun `skal fange opp endringer i kodeverk`() {
        val providers = withProvidersMock()
        val service = EnhetligKodeverkServiceImpl(providers)

        assertThat(service.hentKodeverk(KodeverkConfig.LAND)).isNotNull
        every { providers.fellesKodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            "Land",
            mapOf(
                "NO" to "Noreg"
            )
        )
        val landkode = service.hentKodeverk(KodeverkConfig.LAND).hentVerdi("NO", "NO")
        assertThat(landkode).isEqualTo("Norge")

        service.prepopulerCache()

        val oppdatertLandkode = service.hentKodeverk(KodeverkConfig.LAND).hentVerdi("NO", "NO")
        assertThat(oppdatertLandkode).isEqualTo("Noreg")
    }

    @Test
    internal fun `skal ikke forkaste kodeverk om refreshing feiler`() {
        val providers = withProvidersMock()
        val service = EnhetligKodeverkServiceImpl(providers)

        assertThat(service.hentKodeverk(KodeverkConfig.LAND)).isNotNull
        assertThat(service.hentKodeverk(KodeverkConfig.LAND).hentVerdi("NO", "NO")).isEqualTo("Norge")
        every { providers.fellesKodeverk.hentKodeverk(any()) } throws IllegalStateException("Noe gikk feil")

        service.prepopulerCache()

        assertThat(service.hentKodeverk(KodeverkConfig.LAND)).isNotNull
        assertThat(service.hentKodeverk(KodeverkConfig.LAND).hentVerdi("NO", "NO")).isEqualTo("Norge")
    }

    @Test
    internal fun `skal kunne starte opp selvom alle avhengiheter er nede`() {
        val providers: KodeverkProviders = mockk()
        every { providers.fellesKodeverk.hentKodeverk(any()) } throws IllegalStateException("Noe gikk feil")
        every { providers.sfHenvendelseKodeverk.hentKodeverk(any()) } throws IllegalStateException("Noe gikk feil")

        val service = EnhetligKodeverkServiceImpl(providers)

        assertThat(service.hentKodeverk(KodeverkConfig.LAND)).isNotNull
        assertThat(service.hentKodeverk(KodeverkConfig.LAND).hentVerdi("NO", "NO")).isEqualTo("NO")
        assertThat(service.hentKodeverk(KodeverkConfig.SF_TEMAGRUPPER)).isNotNull
    }

    @Test
    internal fun `regner ut schedule dato riktig`() {
        val (timer, dateSlot, periodeSlot) = withTimerMock()
        val providers = withProvidersMock()

        EnhetligKodeverkServiceImpl(providers, timer)

        assertThat(dateSlot.isCaptured).isTrue()
        assertThat(dateSlot.captured).hasHourOfDay(1)

        val forventetDato = if (dateSlot.captured.toInstant().isBefore(Instant.now())) {
            LocalDate.now().dayOfMonth
        } else {
            LocalDate.now().plusDays(1).dayOfMonth
        }
        assertThat(dateSlot.captured).hasDayOfMonth(forventetDato)

        assertThat(periodeSlot.isCaptured).isTrue()
        assertThat(periodeSlot.captured).isEqualTo(24 * 3600 * 1000)
    }

    @Test
    internal fun `skal rapportere om utdatert kodeverk i selftesten`() {
        val providers = withProvidersMock()

        val clock = MutableClock()
        val service = EnhetligKodeverkServiceImpl(
            providers = providers,
            clock = clock
        )

        val kodeverk = service.hentKodeverk(KodeverkConfig.LAND)

        clock.plusDays(1).plusHours(1)

        val result = service.ping().check.checkHealth()
        assertThat(result.isUnhealthy).isTrue()
        assertThat(result.errorMessage).hasValueSatisfying { errorMessage ->
            assertThat(errorMessage).contains(kodeverk.navn)
            assertThat(errorMessage).contains("SF_TEMAGRUPPER")
        }
    }

    private fun withTimerMock(): Triple<Timer, CapturingSlot<Date>, CapturingSlot<Long>> {
        val timer: Timer = mockk()
        val date = slot<Date>()
        val periode = slot<Long>()
        every { timer.scheduleAtFixedRate(any(), capture(date), capture(periode)) } returns Unit
        return Triple(timer, date, periode)
    }

    private fun withProvidersMock(): KodeverkProviders {
        val providers: KodeverkProviders = mockk()
        every { providers.fellesKodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            "Land",
            mapOf(
                "NO" to "Norge"
            )
        )
        every { providers.sfHenvendelseKodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            "Temagrupper",
            mapOf(
                "ARBD" to "Arbeid"
            )
        )
        every { providers.oppgaveKodeverk.hentKodeverk(any()) } returns EnhetligKodeverk.Kodeverk(
            "Oppgave",
            emptyMap()
        )
        return providers
    }
}
