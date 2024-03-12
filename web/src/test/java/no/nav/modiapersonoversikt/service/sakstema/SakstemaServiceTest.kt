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
import no.nav.modiapersonoversikt.service.sakstema.SakstemaServiceImpl.Companion.hentAlleTema
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak
import no.nav.modiapersonoversikt.service.soknadsstatus.SoknadsstatusService
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class SakstemaServiceTest {
    private val sakOgBehandlingService: SakOgBehandlingService = mockk()
    private val kodeverk: EnhetligKodeverk.Service = mockk()
    private val safService: SafService = mockk()
    private val soknadsstatusService: SoknadsstatusService = mockk()
    private val sakstemaService =
        SakstemaServiceImpl(safService, sakOgBehandlingService, kodeverk, soknadsstatusService)

    @Before
    fun setup() {
        every { kodeverk.hentKodeverk<String, String>(any()) } answers {
            EnhetligKodeverk.Kodeverk(
                "DUMMY",
                hashMapOf(
                    "DAG" to "Dagpenger",
                    "AAP" to "Arbeidsavklaringspenger",
                    "OPP" to "Oppfølging",
                    "FOR" to "Foreldrepenger",
                    "SYK" to "Sykepenger",
                    "SYM" to "Sykemeldinger",
                ),
            )
        }
    }

    @Test
    fun lagSakstemaMedOppfoling() {
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val temakoder: Set<String> = setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppe(
                temakoder,
                listOf(sak, oppfolinging),
                emptyList(),
                emptyMap<String, List<Behandlingskjede>>(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat.size, 2)
    }

    @Test
    fun lagSakstemaMedOppfolgingSoknadsstatus() {
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val temakoder: Set<String> = setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder,
                listOf(sak, oppfolinging),
                emptyList(),
                emptyMap(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat.size, 2)
    }

    @Test
    fun sakstemaMedKunOppfolgingGrupperesIkkeSoknadsstatus() {
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val temakoder: Set<String> = setOf(Konstanter.OPPFOLGING)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder,
                listOf(oppfolinging),
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withHoveddokument(
                            Dokument()
                                .withTittel("TEST"),
                        ),
                ),
                emptyMap(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat.size, 1)
    }

    @Test
    fun etSakstemaMedOppfolgingGirEtSakstema() {
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder = setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppe(
                temakoder,
                listOf(oppfolinging, sak),
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                            Dokument()
                                .withTittel("TEST"),
                        ),
                ),
                emptyMap<String, List<Behandlingskjede>>(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat.size, 2)
    }

    @Test
    fun etSakstemaMedOppfolgingGirEtSakstemaSoknadsstatus() {
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder = setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder,
                listOf(oppfolinging, sak),
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                            Dokument()
                                .withTittel("TEST"),
                        ),
                ),
                emptyMap(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat.size, 2)
    }

    @Test
    fun sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadata() {
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val sak2 =
            Sak()
                .withSaksId("1234")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder =
            setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppe(
                temakoder,
                listOf(sak2, sak),
                listOf(
                    DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                            Dokument()
                                .withTittel("Tilhorende Oppfolging"),
                        ),
                ),
                emptyMap<String, List<Behandlingskjede>>(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat[2].temanavn, "Arbeidsavklaringspenger")
        assertEquals(wrapper.resultat.size, 3)
    }

    @Test
    fun sakMedOppfolgingIHenvendelseSkalGrupperesOgFaaTilhorendeMetadataSoknadsstatus() {
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val sak2 =
            Sak()
                .withSaksId("1234")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder =
            setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder,
                listOf(sak2, sak),
                listOf(
                    DokumentMetadata()
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.HENVENDELSE)
                        .withTemakode("OPP")
                        .withHoveddokument(
                            Dokument()
                                .withTittel("Tilhorende Oppfolging"),
                        ),
                ),
                emptyMap(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat[2].temanavn, "Arbeidsavklaringspenger")
        assertEquals(wrapper.resultat.size, 3)
    }

    @Test
    fun sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstema() {
        every { safService.hentJournalposter(any()) } answers { ResultatWrapper(emptyList()) }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }

        val listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR)
        assertEquals(
            listResultatWrapper.resultat.size,
            1,
        )
        assertEquals(listResultatWrapper.resultat[0].temakode, "DAG")
    }

    @Test
    fun sakFraSakogBehandlingUtenTilhoerendeSakstemaOppretterEgetSakstemaSoknadsstatus() {
        every { safService.hentJournalposter(any()) } answers { ResultatWrapper(emptyList()) }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }

        val listResultatWrapper = sakstemaService.hentSakstemaSoknadsstatus(emptyList(), FNR)
        assertEquals(
            listResultatWrapper.resultat.size,
            1,
        )
        assertEquals(listResultatWrapper.resultat[0].temakode, "DAG")
    }

    @Test
    fun sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstema() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata().withTemakode("DAG").withBaksystem(Baksystem.HENVENDELSE),
                ),
            )
        }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }
        val listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR)
        assertEquals(listResultatWrapper.resultat.size, 1)
        assertEquals(listResultatWrapper.resultat[0].temakode, "DAG")
    }

    @Test
    fun sakFraSakogBehandlingMedTilhoerendeSakstemaOppretterIkkeEgetSakstemaSoknadsstatus() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata().withTemakode("DAG").withBaksystem(Baksystem.HENVENDELSE),
                ),
            )
        }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }
        val listResultatWrapper = sakstemaService.hentSakstemaSoknadsstatus(emptyList(), FNR)
        assertEquals(listResultatWrapper.resultat.size, 1)
        assertEquals(listResultatWrapper.resultat[0].temakode, "DAG")
    }

    @Test
    fun forskjelligTemakodeSakOgBehandlingOgAnnet() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata().withTemakode("FOR").withBaksystem(Baksystem.HENVENDELSE),
                ),
            )
        }
        every {
            sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any())
        } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }
        val listResultatWrapper = sakstemaService.hentSakstema(emptyList(), FNR)
        assertEquals(listResultatWrapper.resultat.size, 2)
    }

    @Test
    fun forskjelligTemakodeSakOgBehandlingOgAnnetSoknadsstatus() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata().withTemakode("FOR").withBaksystem(Baksystem.HENVENDELSE),
                ),
            )
        }
        every {
            sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any())
        } answers {
            hashMapOf(
                "DAG" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }
        val listResultatWrapper = sakstemaService.hentSakstemaSoknadsstatus(emptyList(), FNR)
        assertEquals(listResultatWrapper.resultat.size, 2)
    }

    @Test
    fun flereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolging() {
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val sak2 =
            Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder =
            setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppe(
                temakoder,
                listOf(oppfolinging, sak, sak2),
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                            Dokument()
                                .withTittel("TEST"),
                        ),
                ),
                emptyMap<String, List<Behandlingskjede>>(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat[2].temanavn, "Arbeidsavklaringspenger")
        assertEquals(wrapper.resultat.size, 3)
    }

    @Test
    fun flereSakstemaMedOppfolgingGirFlereSakstemaMedOppfolgingSoknadsstatus() {
        val oppfolinging =
            Sak()
                .withSaksId("321")
                .withTemakode(Konstanter.OPPFOLGING)
                .withAvsluttet(Optional.empty())
        val sak =
            Sak()
                .withSaksId("123")
                .withTemakode(Konstanter.DAGPENGER)
                .withAvsluttet(Optional.empty())
        val sak2 =
            Sak()
                .withSaksId("122")
                .withTemakode(Konstanter.ARBEIDSAVKLARINGSPENGER)
                .withAvsluttet(Optional.empty())
        val temakoder =
            setOf(Konstanter.DAGPENGER, Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER)
        val wrapper =
            sakstemaService.opprettSakstemaForEnTemagruppeSoknadsstatus(
                temakoder,
                listOf(oppfolinging, sak, sak2),
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("321")
                        .withMottaker(Entitet.SLUTTBRUKER)
                        .withAvsender(Entitet.NAV)
                        .withRetning(Kommunikasjonsretning.UT)
                        .withDato(LocalDateTime.now())
                        .withBaksystem(Baksystem.JOARK)
                        .withHoveddokument(
                            Dokument()
                                .withTittel("TEST"),
                        ),
                ),
                emptyMap(),
            )
        assertEquals(wrapper.resultat[0].temanavn, "Dagpenger")
        assertEquals(wrapper.resultat[1].temanavn, "Oppfølging")
        assertEquals(wrapper.resultat[2].temanavn, "Arbeidsavklaringspenger")
        assertEquals(wrapper.resultat.size, 3)
    }

    @Test
    fun slaarIkkeSammenSykepengerOgSykemeldingForModia() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withTemakode("SYK")
                        .withBaksystem(Baksystem.JOARK),
                    DokumentMetadata()
                        .withTilhorendeSakid("456")
                        .withTemakode("SYM")
                        .withBaksystem(Baksystem.JOARK),
                    DokumentMetadata()
                        .withTilhorendeSakid("789")
                        .withTemakode("OPP")
                        .withBaksystem(Baksystem.JOARK),
                ),
            )
        }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "SYK" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }

        val saker =
            listOf(
                Sak()
                    .withSaksId("123")
                    .withTemakode("SYM"),
                Sak()
                    .withSaksId("456")
                    .withTemakode("SYK"),
                Sak()
                    .withSaksId("789")
                    .withTemakode("OPP"),
            )
        val listResultatWrapper = sakstemaService.hentSakstema(saker, FNR)
        assertEquals(listResultatWrapper.resultat.size, 3)
        assertEquals(listResultatWrapper.resultat[0].temanavn, "Sykepenger")
    }

    @Test
    fun slaarIkkeSammenSykepengerOgSykemeldingForModiaSoknadsstatus() {
        every { safService.hentJournalposter(any()) } answers {
            ResultatWrapper(
                listOf(
                    DokumentMetadata()
                        .withTilhorendeSakid("123")
                        .withTemakode("SYK")
                        .withBaksystem(Baksystem.JOARK),
                    DokumentMetadata()
                        .withTilhorendeSakid("456")
                        .withTemakode("SYM")
                        .withBaksystem(Baksystem.JOARK),
                    DokumentMetadata()
                        .withTilhorendeSakid("789")
                        .withTemakode("OPP")
                        .withBaksystem(Baksystem.JOARK),
                ),
            )
        }
        every { sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(any()) } answers {
            hashMapOf(
                "SYK" to
                    listOf(
                        Behandlingskjede()
                            .withStatus(BehandlingsStatus.FERDIG_BEHANDLET)
                            .withSistOppdatert(LocalDateTime.now()),
                    ),
            )
        }

        val saker =
            listOf(
                Sak()
                    .withSaksId("123")
                    .withTemakode("SYM"),
                Sak()
                    .withSaksId("456")
                    .withTemakode("SYK"),
                Sak()
                    .withSaksId("789")
                    .withTemakode("OPP"),
            )
        val listResultatWrapper = sakstemaService.hentSakstemaSoknadsstatus(saker, FNR)
        assertEquals(listResultatWrapper.resultat.size, 3)
        assertEquals(listResultatWrapper.resultat[0].temanavn, "Sykemeldinger")
    }

    @Test
    fun gruppererTemaFraSakerDokumentMetadataOgBehandlingskjeder() {
        val temakoder =
            hentAlleTema(
                lagSaker(Konstanter.DAGPENGER, Konstanter.KONTROLL),
                lagDokument(
                    listOf(Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER),
                    listOf("KNA", "IND"),
                ),
                lagBehandlingskjeder(Konstanter.FORELDREPENGER, Konstanter.DAGPENGER, Konstanter.OPPFOLGING),
            )
        MatcherAssert.assertThat(
            temakoder,
            CoreMatchers.hasItems(
                Konstanter.DAGPENGER,
                Konstanter.KONTROLL,
                Konstanter.OPPFOLGING,
                Konstanter.ARBEIDSAVKLARINGSPENGER,
                Konstanter.FORELDREPENGER,
            ),
        )
        MatcherAssert.assertThat(
            temakoder,
            CoreMatchers.not(
                CoreMatchers.hasItems(
                    "KNA",
                    "IND",
                ),
            ),
        )
        MatcherAssert.assertThat(temakoder.size, CoreMatchers.`is`(5))
    }

    companion object {
        private const val FNR = "11111111111"

        private fun lagSaker(vararg temakoder: String): List<Sak> {
            return temakoder
                .map { temakode: String? -> Sak().withTemakode(temakode) }
        }

        private fun lagDokument(
            henvendelseTemakoder: List<String>,
            joarkTemakoder: List<String>,
        ): List<DokumentMetadata> {
            val henvendelseDokument =
                henvendelseTemakoder
                    .map { temakode: String? ->
                        DokumentMetadata()
                            .withTemakode(temakode)
                            .withBaksystem(Baksystem.HENVENDELSE)
                    }
            val joarkDokument =
                joarkTemakoder
                    .map { temakode: String? ->
                        DokumentMetadata()
                            .withTemakode(temakode)
                            .withBaksystem(Baksystem.JOARK)
                    }
            return henvendelseDokument + joarkDokument
        }

        private fun lagBehandlingskjeder(vararg temakoder: String): Map<String, List<Behandlingskjede>> {
            return temakoder.fold(mutableMapOf()) { res, temakode ->
                res[temakode] = emptyList()
                return res
            }
        }
    }
}
