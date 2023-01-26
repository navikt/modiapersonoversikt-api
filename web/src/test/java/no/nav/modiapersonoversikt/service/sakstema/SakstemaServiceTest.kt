package no.nav.modiapersonoversikt.service.sakstema

import io.mockk.every
import io.mockk.mockk
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.commondomain.sak.Entitet
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.saf.SafService
import no.nav.modiapersonoversikt.service.saf.domain.Dokument
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.saf.domain.Kommunikasjonsretning
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import no.nav.personoversikt.common.test.testenvironment.TestEnvironmentExtension
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.LocalDateTime
import java.time.Month
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SakstemaServiceTest {
    private val safService: SafService = mockk()
    private val sakOgBehandlingService: SakOgBehandlingService = mockk()
    private val kodeverk: EnhetligKodeverk.Service = mockk()

    @RegisterExtension
    val environment = TestEnvironmentExtension(
        "SAKSOVERSIKT_PRODSETTNINGSDATO" to "2015-01-01"
    )

    private val sakstemaService = SakstemaService(safService, sakOgBehandlingService, kodeverk)

    @BeforeEach
    fun setUp() {
        every { kodeverk.hentKodeverk(any<EnhetligKodeverk.Kilde<*, *>>()) } returns EnhetligKodeverk.Kodeverk(
            "DUMMY",
            mapOf(
                "DAG" to "Dagpenger",
                "AAP" to "Arbeidsavklaringspenger",
                "OPP" to "Oppfølging",
                "FOR" to "Foreldrepenger",
                "SYK" to "Sykepenger",
                "SYM" to "Sykemeldinger",
            )
        )
    }

    @Test
    fun `skal lage sakstema med tema oppfølging`() {
        val saker = lagSaker(
            Temakode("DAG") to SaksId("123"),
            Temakode("OPP") to SaksId("321"),
        )
        val temakoder = setOf("DAG", "OPP")

        val wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(
            temakoder = temakoder,
            saker = saker,
            dokumenter = emptyList(),
            behandlingskjeder = emptyMap()
        )

        assertEquals(2, wrapper.resultat.size)
        assertEquals("Dagpenger", wrapper.resultat[0].temanavn)
        assertEquals("Oppfølging", wrapper.resultat[1].temanavn)
    }

    @Test
    fun `samstema med kun oppfølging grupperes ikke`() {
        val saker = lagSaker(
            Temakode("OPP") to SaksId("321")
        )
        val temakoder = setOf("OPP")
        val dokument = DokumentMetadata()
            .withTilhorendeSakid("321")
            .withMottaker(Entitet.SLUTTBRUKER)
            .withAvsender(Entitet.NAV)
            .withRetning(Kommunikasjonsretning.UT)
            .withDato(LocalDateTime.now())
            .withHoveddokument(
                Dokument().withTittel("TEST")
            )

        val wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(
            temakoder = temakoder,
            saker = saker,
            dokumenter = listOf(dokument),
            behandlingskjeder = emptyMap()
        )

        assertEquals(1, wrapper.resultat.size)
        assertEquals("Oppfølging", wrapper.resultat[0].temanavn)
    }

    @Test
    fun `sak med oppfolging i henvendelse skal grupperes og få tilhørende metadata`() {
        val saker = lagSaker(
            Temakode("DAG") to SaksId("123"),
            Temakode("AAP") to SaksId("1234"),
        )

        val oppfolgingDokument = DokumentMetadata()
            .withMottaker(Entitet.SLUTTBRUKER)
            .withAvsender(Entitet.NAV)
            .withRetning(Kommunikasjonsretning.UT)
            .withDato(LocalDateTime.now())
            .withBaksystem(Baksystem.HENVENDELSE)
            .withTemakode("OPP")
            .withHoveddokument(
                Dokument()
                    .withTittel("Tilhørende Oppfolging")
            )

        val wrapper = sakstemaService.opprettSakstemaForEnTemagruppe(
            temakoder = HashSet(listOf("DAG", "OPP", "AAP")),
            saker = saker,
            dokumenter = listOf(oppfolgingDokument),
            behandlingskjeder = emptyMap()
        )

        assertEquals(3, wrapper.resultat.size)
        assertEquals("Arbeidsavklaringspenger", wrapper.resultat[0].temanavn)
        assertEquals("Dagpenger", wrapper.resultat[1].temanavn)
        assertEquals("Oppfølging", wrapper.resultat[2].temanavn)
    }

    @Test
    fun `sak fra SOB uten tilhørende sakstema oppretter eget sakstema`() {
        gittDokumenter()
        gittBehandlingskjeder(Temakode("DAG") to BehandlingsStatus.FERDIG_BEHANDLET)

        val wrapper = sakstemaService.hentSakstema(emptyList(), "1234567910")

        assertEquals(1, wrapper.resultat.size)
        assertEquals("DAG", wrapper.resultat[0].temakode)
        assertEquals("Dagpenger", wrapper.resultat[0].temanavn)
    }

    @Test
    fun `sak fra SOB med tilhørende sakstema oppretter ikke eget sakstema`() {
        gittBehandlingskjeder()
        gittDokumenter(Temakode("DAG") to Baksystem.HENVENDELSE)
        val wrapper = sakstemaService.hentSakstema(emptyList(), "12345678910")

        assertEquals(1, wrapper.resultat.size)
        assertEquals("DAG", wrapper.resultat[0].temakode)
        assertEquals("Dagpenger", wrapper.resultat[0].temanavn)
    }

    @Test
    fun `skal beholde temakoder fra SAF og SOB om forkjellige`() {
        gittDokumenter(Temakode("FOR") to Baksystem.HENVENDELSE)
        gittBehandlingskjeder(Temakode("DAG") to BehandlingsStatus.FERDIG_BEHANDLET)

        val wrapper = sakstemaService.hentSakstema(emptyList(), "12345678910")

        assertEquals(2, wrapper.resultat.size)
    }

    @Test
    fun `slar ikke sammen sykepenger og sykemelding tema`() {
        gittBehandlingskjeder()
        gittDokumenter(
            Temakode("SYK") to Baksystem.JOARK,
            Temakode("SYM") to Baksystem.JOARK,
            Temakode("OPP") to Baksystem.JOARK,
        )
        val saker = lagSaker(
            Temakode("SYM") to SaksId("123"),
            Temakode("SYK") to SaksId("456"),
            Temakode("OPP") to SaksId("789"),
        )

        val wrapper = sakstemaService.hentSakstema(saker, "12345678910")
        assertEquals(3, wrapper.resultat.size)
        assertTrue { wrapper.resultat.find { it.temanavn == "Sykemeldinger" } != null }
        assertTrue { wrapper.resultat.find { it.temanavn == "Sykepenger" } != null }
        assertTrue { wrapper.resultat.find { it.temanavn == "Oppfølging" } != null }
    }

    @Test
    fun `grupperer tema fra saker, dokumenter og behandlingskjeder`() {
        lagSaker()

        val tema = SakstemaService.hentAlleTema(
            saker = lagSaker(
                Temakode("DAG") to SaksId("123"),
                Temakode("KTR") to SaksId("321"),
            ),
            dokumenter = lagDokumenter(
                Temakode("OPP") to Baksystem.HENVENDELSE,
                Temakode("AAP") to Baksystem.HENVENDELSE,
                Temakode("KNA") to Baksystem.JOARK,
                Temakode("IND") to Baksystem.JOARK,
            ),
            behandlingskjeder = lagBehandlingskjeder(
                Temakode("FOR") to BehandlingsStatus.FERDIG_BEHANDLET,
                Temakode("DAG") to BehandlingsStatus.FERDIG_BEHANDLET,
                Temakode("OPP") to BehandlingsStatus.FERDIG_BEHANDLET,
            )
        )

        assertAll(
            { assertTrue(tema.contains("DAG")) },
            { assertTrue(tema.contains("KTR")) },
            { assertTrue(tema.contains("OPP")) },
            { assertTrue(tema.contains("AAP")) },
            { assertTrue(tema.contains("FOR")) },

            { assertFalse(tema.contains("KNA")) },
            { assertFalse(tema.contains("IND")) },

            { assertEquals(5, tema.size) },
        )
    }

    @Test
    fun `skal fjerne gamle journalposter`() {
        val filtrerte = SakstemaService.fjernGamleDokumenter(getMockSakstema())

        assertEquals(3, filtrerte.size)

        assertEquals(2, filtrerte[0].dokumentMetadata.size)
        assertEquals(3, filtrerte[1].dokumentMetadata.size)
        assertEquals(0, filtrerte[2].dokumentMetadata.size)

        assertEquals("2", filtrerte[0].dokumentMetadata[0].journalpostId)
        assertEquals("3", filtrerte[0].dokumentMetadata[1].journalpostId)

        assertEquals("4", filtrerte[1].dokumentMetadata[0].journalpostId)
        assertEquals("5", filtrerte[1].dokumentMetadata[1].journalpostId)
        assertEquals("6", filtrerte[1].dokumentMetadata[2].journalpostId)
    }

    @JvmInline
    private value class SaksId(val value: String)

    @JvmInline
    private value class Temakode(val value: String)
    private fun lagSaker(vararg sak: Pair<Temakode, SaksId>): List<Sak> {
        return sak.map { (tema, id) ->
            Sak()
                .withSaksId(id.value)
                .withTemakode(tema.value)
                .withAvsluttet(Optional.empty())
        }
    }
    private fun gittDokumenter(
        vararg dokument: Pair<Temakode, Baksystem>
    ) {
        every { safService.hentJournalposter(any()) } returns ResultatWrapper(
            dokument.map { (tema, baksystem) ->
                DokumentMetadata()
                    .withTemakode(tema.value)
                    .withBaksystem(baksystem)
            }
        )
    }

    private fun gittBehandlingskjeder(
        vararg behandlingskjeder: Pair<Temakode, BehandlingsStatus>
    ) {
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } returns lagBehandlingskjeder(*behandlingskjeder)
    }

    private fun lagDokumenter(vararg dokumenter: Pair<Temakode, Baksystem>) = dokumenter.map { (tema, baksystem) ->
        DokumentMetadata()
            .withTemakode(tema.value)
            .withBaksystem(baksystem)
    }
    private fun lagBehandlingskjeder(
        vararg behandlingskjeder: Pair<Temakode, BehandlingsStatus>
    ) = behandlingskjeder
        .map { (tema, status) ->
            val kjede = Behandlingskjede()
                .withStatus(status)
                .withSistOppdatert(LocalDateTime.now())
            Pair(tema, kjede)
        }
        .groupBy(
            keySelector = { (tema, _) -> tema.value },
            valueTransform = { (_, kjede) -> kjede }
        )

    private fun getMockSakstema(): List<Sakstema> {
        val dokumentmetadata1 = DokumentMetadata()
            .withJournalpostId("1")
            .withBaksystem(Baksystem.SAF)
            .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata2 = DokumentMetadata()
            .withJournalpostId("2")
            .withBaksystem(Baksystem.SAF)
            .withBaksystem(Baksystem.HENVENDELSE)
            .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata3 = DokumentMetadata()
            .withJournalpostId("3")
            .withBaksystem(Baksystem.HENVENDELSE)
            .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata4 = DokumentMetadata()
            .withJournalpostId("4")
            .withBaksystem(Baksystem.SAF)
            .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata5 = DokumentMetadata()
            .withJournalpostId("5")
            .withBaksystem(Baksystem.HENVENDELSE)
            .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata6 = DokumentMetadata()
            .withJournalpostId("6")
            .withBaksystem(Baksystem.HENVENDELSE)
            .withBaksystem(Baksystem.SAF)
            .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata7 = DokumentMetadata()
            .withJournalpostId("7")
            .withBaksystem(Baksystem.SAF)
            .withDato(LocalDateTime.of(2010, Month.APRIL, 8, 12, 30))
        val dokumentmetadata8 = DokumentMetadata()
            .withJournalpostId("8")
            .withBaksystem(Baksystem.SAF)
            .withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30))
        val sakstema1 = Sakstema()
            .withDokumentMetadata(listOf(dokumentmetadata1, dokumentmetadata2, dokumentmetadata3))
        val sakstema2 = Sakstema()
            .withDokumentMetadata(listOf(dokumentmetadata4, dokumentmetadata5, dokumentmetadata6))
        val sakstema3 = Sakstema()
            .withDokumentMetadata(listOf(dokumentmetadata7, dokumentmetadata8))
        return listOf(
            sakstema1,
            sakstema2,
            sakstema3
        )
    }
}
