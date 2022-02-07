package no.nav.modiapersonoversikt.service.enhetligkodeverk

import io.mockk.*
import no.nav.common.sts.SystemUserTokenProvider
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.apis.KodeverkApi
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.GjelderDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.KodeverkkombinasjonDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.OppgavetypeDTO
import no.nav.modiapersonoversikt.legacy.api.domain.oppgave.generated.models.TemaDTO
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.KodeverkProviders
import no.nav.modiapersonoversikt.service.enhetligkodeverk.kodeverkproviders.oppgave.OppgaveKodeverk
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

        val sfTemagrupper = service.hentKodeverk(KodeverkConfig.SF_TEMAGRUPPER)
        assertThat(sfTemagrupper).isNotNull

        val temaer = service.hentKodeverk(KodeverkConfig.OPPGAVE)
        assertThat(temaer).isNotNull

        val landkode = landkoder.hentVerdi("NO", "NO")
        assertThat(landkode).isEqualTo("Norge")

        val sfTemagruppe = sfTemagrupper.hentVerdi("ARBD", "ARBD")
        assertThat(sfTemagruppe).isEqualTo("Arbeid")

        val tema = temaer.hentVerdi("AAP")
        assertThat(tema.tekst).isEqualTo("Arbeidsavklaringspenger")
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

    @Test
    internal fun `skal overstyre oppgavekodeverk på frister og prioriteter`() {
        val oppgaveApi: KodeverkApi = mockk()
        val systemUserTokenProvider: SystemUserTokenProvider = mockk()
        val provider = OppgaveKodeverk.Provider(systemUserTokenProvider, oppgaveApi)
        every { provider.oppgaveKodeverk.hentInterntKodeverk(any()) } returns listOf(
            KodeverkkombinasjonDTO(
                tema = TemaDTO(
                    tema = "AAP",
                    term = "Arbeidsavklaringspenger"
                ),
                oppgavetyper = listOf(
                    OppgavetypeDTO(
                        oppgavetype = "VURD_HENV",
                        term = "Vurder henvendelse"
                    ),
                    OppgavetypeDTO(
                        oppgavetype = "IKKE_STØTTET",
                        term = "Oppgavetype vi ikke støtter"
                    )
                ),
                gjelderverdier = listOf(
                    GjelderDTO(
                        behandlingstema = "ab0241",
                        behandlingstemaTerm = "Dagliglivet"
                    ),
                    GjelderDTO(
                        behandlingstema = "ab0241",
                        behandlingstemaTerm = "Dagliglivet",
                        behandlingstype = "ae0007",
                        behandlingstypeTerm = "Utbetaling"
                    )
                )
            ),
            KodeverkkombinasjonDTO(
                tema = TemaDTO(
                    tema = "TIL",
                    term = "Tiltak"
                ),
                oppgavetyper = emptyList(),
                gjelderverdier = null
            )
        )

        val kodeverk = provider.hentKodeverk(KodeverkConfig.OPPGAVE.navn)
        val kodeverkVerdier = kodeverk.hentAlleVerdier().toList()

        assertThat(kodeverkVerdier.size).isEqualTo(1)
        assertThat(kodeverkVerdier[0].prioriteter.size).isEqualTo(3)
        assertThat(kodeverkVerdier[0].prioriteter[0].tekst).isEqualTo("Høy")
        assertThat(kodeverkVerdier[0].oppgavetyper.size).isEqualTo(1)
        assertThat(kodeverkVerdier[0].oppgavetyper[0].dagerFrist).isEqualTo(0)
        assertThat(kodeverkVerdier[0].underkategorier.size).isEqualTo(2)
        assertThat(kodeverkVerdier[0].underkategorier[0].kode).isEqualTo("ab0241:")
        assertThat(kodeverkVerdier[0].underkategorier[0].tekst).isEqualTo("Dagliglivet")
        assertThat(kodeverkVerdier[0].underkategorier[1].kode).isEqualTo("ab0241:ae0007")
        assertThat(kodeverkVerdier[0].underkategorier[1].tekst).isEqualTo("Dagliglivet - Utbetaling")
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
            mapOf(
                "AAP" to OppgaveKodeverk.Tema(
                    kode = "AAP",
                    tekst = "Arbeidsavklaringspenger",
                    oppgavetyper = emptyList(),
                    prioriteter = emptyList(),
                    underkategorier = emptyList()
                )
            )
        )

        return providers
    }
}
