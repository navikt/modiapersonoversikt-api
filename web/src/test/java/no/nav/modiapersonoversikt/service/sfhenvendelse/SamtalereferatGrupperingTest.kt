package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.JournalpostDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingFraDTO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

internal class SamtalereferatGrupperingTest {
    private val dato = OffsetDateTime.parse("2024-01-01T12:00:00Z")
    private val fra = MeldingFraDTO(identType = MeldingFraDTO.IdentType.SYSTEM)

    private fun referat(
        kjedeId: String,
        meldingsId: String?,
        type: HenvendelseDTO.HenvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
    ) = HenvendelseDTO(
        henvendelseType = type,
        fnr = "00000000000",
        aktorId = "aktør",
        opprettetDato = dato,
        feilsendt = false,
        kjedeId = kjedeId,
        gjeldendeTemagruppe = "ARBD",
        meldinger = listOf(MeldingDTO(sendtDato = dato, fra = fra, meldingsId = meldingsId)),
    )

    @Test
    fun `grupperer kun samtalereferater og bevarer meldingsrekkefolge og andre typer`() {
        val head = referat("", "head")
        val child = referat("head", "child")
        val annen = referat("annen", "annen-melding", HenvendelseDTO.HenvendelseType.MELDINGSKJEDE)

        val resultat = grupperSamtalereferater(listOf(annen, head, child))

        assertThat(resultat).hasSize(2)
        assertThat(resultat.first()).isSameAs(annen)
        assertThat(resultat.last().kjedeId).isEqualTo("head")
        assertThat(resultat.last().meldinger?.map { it.meldingsId }).containsExactly("head", "child")
    }

    @Test
    fun `samler journalposter for hele kjeden slik at tematilgang gjelder alle meldinger`() {
        val journalpost =
            JournalpostDTO(
                journalpostId = "syntetisk",
                journalfortDato = dato,
                journalfortTema = "SYK",
            )
        val resultat =
            grupperSamtalereferater(
                listOf(referat("", "head"), referat("head", "child").copy(journalposter = listOf(journalpost))),
            )

        assertThat(resultat.single().journalposter).containsExactly(journalpost)
    }

    @Test
    fun `avviser foreldrelos melding og ulike eiere`() {
        assertThatThrownBy { grupperSamtalereferater(listOf(referat("missing", "child"))) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            grupperSamtalereferater(listOf(referat("", "head"), referat("head", "child").copy(fnr = "annen")))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `avviser manglende og dupliserte meldingsider`() {
        assertThatThrownBy { grupperSamtalereferater(listOf(referat("", null))) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            grupperSamtalereferater(listOf(referat("", "head"), referat("head", "head")))
        }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            grupperSamtalereferater(listOf(referat("", "head"), referat("", "head")))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
