package no.nav.modiapersonoversikt.service.sakogbehandling

import no.nav.modiapersonoversikt.commondomain.sak.Baksystem
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata
import no.nav.modiapersonoversikt.service.sakogbehandling.FilterUtils.fjernGamleDokumenter
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import java.util.*

internal class FilterUtilsTest {
    @BeforeEach
    fun setup() {
        System.setProperty("SAKSOVERSIKT_PRODSETTNINGSDATO", "2015-01-01")
    }

    @Test
    internal fun `skal fjerne gamle journalposter som kun finnes i saf`() {
        val resultat = fjernGamleDokumenter(getMockSaksteman())

        MatcherAssert.assertThat(resultat[0].dokumentMetadata.size, CoreMatchers.`is`(2))
        MatcherAssert.assertThat(resultat[1].dokumentMetadata.size, CoreMatchers.`is`(3))
        MatcherAssert.assertThat(resultat[2].dokumentMetadata.size, CoreMatchers.`is`(0))

        MatcherAssert.assertThat(resultat[0].dokumentMetadata[0].journalpostId, CoreMatchers.`is`("2"))
        MatcherAssert.assertThat(resultat[0].dokumentMetadata[1].journalpostId, CoreMatchers.`is`("3"))

        MatcherAssert.assertThat(resultat[1].dokumentMetadata[0].journalpostId, CoreMatchers.`is`("4"))
        MatcherAssert.assertThat(resultat[1].dokumentMetadata[1].journalpostId, CoreMatchers.`is`("5"))
        MatcherAssert.assertThat(resultat[1].dokumentMetadata[2].journalpostId, CoreMatchers.`is`("6"))
    }

    private fun getMockSaksteman(): List<Sakstema> {
        val dokumentmetadata1 =
            DokumentMetadata()
                .withJournalpostId("1")
                .withBaksystem(Baksystem.SAF)
                .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata2 =
            DokumentMetadata()
                .withJournalpostId("2")
                .withBaksystem(Baksystem.SAF)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata3 =
            DokumentMetadata()
                .withJournalpostId("3")
                .withBaksystem(Baksystem.HENVENDELSE)
                .withDato(LocalDateTime.of(2013, Month.APRIL, 8, 12, 30))
        val dokumentmetadata4 =
            DokumentMetadata()
                .withJournalpostId("4")
                .withBaksystem(Baksystem.SAF)
                .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata5 =
            DokumentMetadata()
                .withJournalpostId("5")
                .withBaksystem(Baksystem.HENVENDELSE)
                .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata6 =
            DokumentMetadata()
                .withJournalpostId("6")
                .withBaksystem(Baksystem.HENVENDELSE).withBaksystem(
                    Baksystem.SAF,
                )
                .withDato(LocalDateTime.of(2015, Month.APRIL, 8, 12, 30))
        val dokumentmetadata7 =
            DokumentMetadata()
                .withJournalpostId("7")
                .withBaksystem(Baksystem.SAF)
                .withDato(LocalDateTime.of(2010, Month.APRIL, 8, 12, 30))
        val dokumentmetadata8 =
            DokumentMetadata()
                .withJournalpostId("8")
                .withBaksystem(Baksystem.SAF)
                .withDato(LocalDateTime.of(2011, Month.APRIL, 8, 12, 30))

        val sakstema1 =
            Sakstema()
                .withDokumentMetadata(listOf(dokumentmetadata1, dokumentmetadata2, dokumentmetadata3))
        val sakstema2 =
            Sakstema()
                .withDokumentMetadata(listOf(dokumentmetadata4, dokumentmetadata5, dokumentmetadata6))
        val sakstema3 =
            Sakstema()
                .withDokumentMetadata(listOf(dokumentmetadata7, dokumentmetadata8))

        return ArrayList(listOf(sakstema1, sakstema2, sakstema3))
    }
}
