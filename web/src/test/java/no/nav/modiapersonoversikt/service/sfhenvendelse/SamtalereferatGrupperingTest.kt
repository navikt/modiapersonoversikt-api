package no.nav.modiapersonoversikt.service.sfhenvendelse

import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.JournalpostDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingFraDTO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

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
    fun `eget meldingsId som kjedeId er hovedreferat og kan samle senere referater`() {
        val head = referat("head", "head")
        val child = referat("head", "child")
        val annen = referat("annen", "annen-melding", HenvendelseDTO.HenvendelseType.MELDINGSKJEDE)

        assertThat(grupperSamtalereferater(listOf(head, annen)))
            .containsExactly(head.copy(journalposter = emptyList(), markeringer = emptyList()), annen)
        val resultat = grupperSamtalereferater(listOf(child, annen, head))
        assertThat(resultat).hasSize(2)
        assertThat(
            resultat
                .single { it.henvendelseType == HenvendelseDTO.HenvendelseType.SAMTALEREFERAT }
                .meldinger
                ?.map { it.meldingsId },
        ).containsExactly("head", "child")
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
    fun `nyeste meldings temagruppe gjelder selv om referatene kommer i annen rekkefolge`() {
        val head = referat("head", "head")
        val siste =
            referat("head", "siste")
                .copy(
                    gjeldendeTemagruppe = "FMLI",
                    opprettetDato = dato.plusMinutes(1).plusSeconds(30),
                    meldinger = listOf(MeldingDTO(sendtDato = dato.plusMinutes(2), fra = fra, meldingsId = "siste")),
                )
        val mellom =
            referat("head", "mellom")
                .copy(
                    gjeldendeTemagruppe = "PENS",
                    opprettetDato = dato.plusMinutes(1),
                    meldinger = listOf(MeldingDTO(sendtDato = dato.plusMinutes(1), fra = fra, meldingsId = "mellom")),
                )

        val resultat = grupperSamtalereferater(listOf(siste, mellom, head)).single()

        assertThat(resultat.kjedeId).isEqualTo("head")
        assertThat(resultat.gjeldendeTemagruppe).isEqualTo("FMLI")
        assertThat(resultat.opprettetDato).isEqualTo(siste.opprettetDato)
        assertThat(resultat.opprettetDato).isNotEqualTo(siste.meldinger!!.single().sendtDato)
        assertThat(resultat.meldinger?.map { it.meldingsId }).containsExactly("head", "siste", "mellom")
        assertThat(grupperSamtalereferater(listOf(siste.copy(gjeldendeTemagruppe = "ARBD"), mellom, head)).single().gjeldendeTemagruppe)
            .isEqualTo("ARBD")
    }

    @Test
    fun `avviser ulik temagruppe ved likt sendetidspunkt for nyeste meldinger`() {
        assertThatThrownBy {
            grupperSamtalereferater(
                listOf(referat("head", "head"), referat("head", "child").copy(gjeldendeTemagruppe = "FMLI")),
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `hovedreferatets tema gjelder hvis det har nyeste melding`() {
        val head =
            referat("head", "head")
                .copy(
                    opprettetDato = dato.plusHours(1),
                    meldinger = listOf(MeldingDTO(sendtDato = dato.plusHours(1), fra = fra, meldingsId = "head")),
                )
        val child =
            referat("head", "child")
                .copy(
                    gjeldendeTemagruppe = "FMLI",
                    meldinger =
                        listOf(MeldingDTO(sendtDato = dato.withOffsetSameInstant(ZoneOffset.ofHours(2)), fra = fra, meldingsId = "child")),
                )

        val resultat = grupperSamtalereferater(listOf(child, head)).single()
        assertThat(resultat.gjeldendeTemagruppe).isEqualTo("ARBD")
        assertThat(resultat.opprettetDato).isEqualTo(head.opprettetDato)
    }

    @Test
    fun `avviser foreldrelos melding og ulike eiere`() {
        assertThatThrownBy { grupperSamtalereferater(listOf(referat("missing", "child"))) }
            .isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            grupperSamtalereferater(listOf(referat("", "head"), referat("head", "child").copy(fnr = "annen")))
        }.isInstanceOf(IllegalArgumentException::class.java)
        assertThatThrownBy {
            grupperSamtalereferater(listOf(referat("", "head"), referat("head", "child").copy(aktorId = "annen")))
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
