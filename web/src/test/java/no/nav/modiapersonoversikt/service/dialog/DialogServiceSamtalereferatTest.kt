package no.nav.modiapersonoversikt.service.dialog

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.mockk.verifyOrder
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.HenvendelseDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingDTO
import no.nav.modiapersonoversikt.consumer.sfhenvendelse.generated.models.MeldingFraDTO
import no.nav.modiapersonoversikt.rest.dialog.domain.TraadType
import no.nav.modiapersonoversikt.service.ansattservice.AnsattService
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig
import no.nav.modiapersonoversikt.service.journalforingsaker.JournalforingSak
import no.nav.modiapersonoversikt.service.oppgavebehandling.OppgaveBehandlingService
import no.nav.modiapersonoversikt.service.sfhenvendelse.EksternBruker
import no.nav.modiapersonoversikt.service.sfhenvendelse.SfHenvendelseService
import no.nav.modiapersonoversikt.service.sfhenvendelse.grupperSamtalereferater
import no.nav.personoversikt.common.logging.Logging
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime

internal class DialogServiceSamtalereferatTest {
    @ParameterizedTest
    @EnumSource(HenvendelseDTO.HenvendelseType::class)
    fun `frontend far sladdet per melding uavhengig av sladding`(type: HenvendelseDTO.HenvendelseType) {
        val tidspunkt = OffsetDateTime.parse("2024-01-01T12:00:00Z")
        val sladdetVerdier = listOf(true, false, null)
        val henvendelse =
            HenvendelseDTO(
                henvendelseType = type,
                fnr = "00000000000",
                aktorId = "syntetisk-aktor",
                opprettetDato = tidspunkt,
                feilsendt = false,
                kjedeId = "syntetisk-kjede",
                gjeldendeTemagruppe = "ARBD",
                sladding = true,
                meldinger =
                    sladdetVerdier.mapIndexed { indeks, sladdet ->
                        MeldingDTO(
                            sendtDato = tidspunkt.plusMinutes(indeks.toLong()),
                            fra = MeldingFraDTO(identType = MeldingFraDTO.IdentType.SYSTEM),
                            meldingsId = "melding-$indeks",
                            fritekst = "Syntetisk melding",
                            sladding = sladdet != true,
                            sladdet = sladdet,
                        )
                    },
            )
        val sf = mockk<SfHenvendelseService>()
        every { sf.hentHenvendelser(EksternBruker.Fnr(henvendelse.fnr), "0101") } returns listOf(henvendelse)
        val kodeverk = mockk<EnhetligKodeverk.Service>()
        every { kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA) } returns EnhetligKodeverk.Kodeverk("tema", emptyMap())
        val ansatt = mockk<AnsattService>()
        every { ansatt.hentVeiledere(any()) } returns emptyMap()
        val dialog = DialogServiceImpl(sf, mockk<OppgaveBehandlingService>(), ansatt, kodeverk)

        val resultat = dialog.hentMeldinger(henvendelse.fnr, "0101").single()

        assertThat(resultat.meldinger.map { it.sladdet }).containsExactlyElementsOf(sladdetVerdier)
        assertThat(resultat.sladding).isTrue()
        assertThat(resultat.meldinger.map { it.sendtTilSladding }).containsOnly(true)
    }

    @Nested
    inner class Opprettelse {
        private val sf = mockk<SfHenvendelseService>()
        private val kodeverk = mockk<EnhetligKodeverk.Service>()
        private val ansatt = mockk<AnsattService>()
        private val dialog = DialogServiceImpl(sf, mockk<OppgaveBehandlingService>(), ansatt, kodeverk)
        private val referat =
            HenvendelseDTO(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                fnr = "00000000000",
                aktorId = "syntetisk-aktor",
                opprettetDato = OffsetDateTime.parse("2024-01-01T12:00:00Z"),
                feilsendt = false,
                kjedeId = "nytt-referat",
                gjeldendeTemagruppe = "PENS",
                meldinger = emptyList(),
            )
        private val sak =
            JournalforingSak().apply {
                temaKode = "DAG"
                fagsystemSaksId = "syntetisk-sak"
                fagsystemKode = "AO01"
            }
        private val request =
            SendMeldingRequestV2(
                fnr = referat.fnr,
                traadId = null,
                traadType = TraadType.SAMTALEREFERAT,
                enhet = "0101",
                fritekst = "Syntetisk referat",
                temagruppe = "PENS",
                sak = sak,
                erOppgaveTilknyttetAnsatt = null,
                avsluttet = null,
                behandlingsId = null,
                oppgaveId = null,
            )

        init {
            every { kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA) } returns EnhetligKodeverk.Kodeverk("tema", emptyMap())
            every { ansatt.hentVeiledere(any()) } returns emptyMap()
            every { sf.sendSamtalereferat(null, EksternBruker.Fnr(referat.fnr), "0101", "PENS", request.fritekst) } returns referat
            every { sf.journalforHenvendelse(any(), any(), any(), any(), any()) } just runs
        }

        @Test
        fun `referat uten sak opprettes uten journalforing`() {
            val resultat = dialog.sendMelding(referat.fnr, request.copy(sak = null))

            assertThat(resultat.traadId).isEqualTo("nytt-referat")
            assertThat(resultat.journalforingFeilet).isFalse()
            verify(exactly = 0) { sf.journalforHenvendelse(any(), any(), any(), any(), any()) }
        }

        @Test
        fun `referat med sak journalfores etter opprettelse med uendret temagruppe`() {
            val resultat = dialog.sendMelding(referat.fnr, request)

            assertThat(resultat.journalforingFeilet).isFalse()
            assertThat(resultat.temagruppe).isEqualTo("PENS")
            verifyOrder {
                sf.sendSamtalereferat(null, EksternBruker.Fnr(referat.fnr), "0101", "PENS", request.fritekst)
                sf.journalforHenvendelse("0101", "nytt-referat", "DAG", "syntetisk-sak", "AO01")
            }
            verify(exactly = 1) { sf.sendSamtalereferat(any(), any(), any(), any(), any()) }
            verify(exactly = 1) { sf.journalforHenvendelse(any(), any(), any(), any(), any()) }
        }

        @Test
        fun `generell sak trenger ikke fagsaksid`() {
            sak.fagsystemSaksId = null
            sak.fagsystemKode = "FS22"
            sak.sakstype = "GEN"

            assertThat(dialog.sendMelding(referat.fnr, request).journalforingFeilet).isFalse()
            verify { sf.journalforHenvendelse("0101", "nytt-referat", "DAG", null, "FS22") }
        }

        @Test
        fun `journalforingsfeil returnerer opprettet trad uten ny opprettelse`() {
            every { sf.journalforHenvendelse(any(), any(), any(), any(), any()) } throws IllegalStateException("Testfeil")

            val resultat = dialog.sendMelding(referat.fnr, request)

            assertThat(resultat.traadId).isEqualTo("nytt-referat")
            assertThat(resultat.journalforingFeilet).isTrue()
            verify(exactly = 1) { sf.sendSamtalereferat(any(), any(), any(), any(), any()) }
        }

        @ParameterizedTest
        @ValueSource(booleans = [false, true])
        fun `journalforing auditeres med opprettet trad og riktig utfall`(feiler: Boolean) {
            if (feiler) {
                every { sf.journalforHenvendelse(any(), any(), any(), any(), any()) } throws IllegalStateException("Testfeil")
            }
            val logger = Logging.teamLog as Logger
            val appender = ListAppender<ILoggingEvent>().apply { start() }
            logger.addAppender(appender)
            try {
                dialog.sendMelding(referat.fnr, request)
                val audit = appender.list.single { it.formattedMessage.contains("resource='person.henvendelse.journalfor'") }
                assertThat(audit.formattedMessage).contains("TRAAD_ID='nytt-referat'", "SAK_ID='syntetisk-sak'")
                assertThat(audit.formattedMessage.contains("FAIL_REASON=")).isEqualTo(feiler)
            } finally {
                logger.detachAppender(appender)
                appender.stop()
            }
        }

        @Test
        fun `opprettelsesfeil utloser ikke journalforing`() {
            every { sf.sendSamtalereferat(any(), any(), any(), any(), any()) } throws IllegalStateException("Testfeil")

            assertThatThrownBy { dialog.sendMelding(referat.fnr, request) }.isInstanceOf(IllegalStateException::class.java)
            verify(exactly = 0) { sf.journalforHenvendelse(any(), any(), any(), any(), any()) }
        }

        @Test
        fun `ugyldig journalforingsmal avvises for opprettelse`() {
            val ugyldigeSaker =
                listOf(
                    JournalforingSak().apply { temaKode = "" },
                    JournalforingSak().apply {
                        temaKode = "DAG"
                        fagsystemSaksId = "syntetisk-sak"
                        fagsystemKode = "UGYLDIG"
                    },
                    JournalforingSak().apply {
                        temaKode = "DAG"
                        fnr = "11111111111"
                    },
                )
            for (ugyldigSak in ugyldigeSaker) {
                assertThatThrownBy { dialog.sendMelding(referat.fnr, request.copy(sak = ugyldigSak)) }
                    .isInstanceOfSatisfying(ResponseStatusException::class.java) {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
                    }
            }
            verify(exactly = 0) { sf.sendSamtalereferat(any(), any(), any(), any(), any()) }
        }
    }

    @Test
    fun `frontend far samme trad for samlet v1 og grupperte v2 referater`() {
        val tidspunkt = OffsetDateTime.parse("2024-01-01T12:00:00Z")
        val fra = MeldingFraDTO(identType = MeldingFraDTO.IdentType.SYSTEM)
        val head =
            HenvendelseDTO(
                henvendelseType = HenvendelseDTO.HenvendelseType.SAMTALEREFERAT,
                fnr = "00000000000",
                aktorId = "syntetisk-aktor",
                opprettetDato = tidspunkt,
                feilsendt = false,
                kjedeId = "",
                gjeldendeTemagruppe = "ARBD",
                meldinger = listOf(MeldingDTO(meldingsId = "head", sendtDato = tidspunkt, fra = fra, fritekst = "Første melding")),
            )
        val neste =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                opprettetDato = tidspunkt.plusMinutes(1),
                meldinger =
                    listOf(
                        MeldingDTO(meldingsId = "andre", sendtDato = tidspunkt.plusMinutes(1), fra = fra, fritekst = "Andre melding"),
                    ),
            )
        val kodeverk = mockk<EnhetligKodeverk.Service>()
        every { kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA) } returns EnhetligKodeverk.Kodeverk("tema", emptyMap())
        val ansatt = mockk<AnsattService>()
        every { ansatt.hentVeiledere(any()) } returns emptyMap()
        val sf = mockk<SfHenvendelseService>()
        val dialog = DialogServiceImpl(sf, mockk<OppgaveBehandlingService>(), ansatt, kodeverk)
        val v1 =
            head.copy(
                kjedeId = "head",
                gjeldendeTemagruppe = "FMLI",
                opprettetDato = neste.opprettetDato,
                meldinger = head.meldinger.orEmpty() + neste.meldinger.orEmpty(),
            )

        every { sf.hentHenvendelser(any<EksternBruker>(), any()) } returns listOf(v1)
        val forventet = dialog.hentMeldinger(head.fnr, "0101")
        every { sf.hentHenvendelser(any<EksternBruker>(), any()) } returns grupperSamtalereferater(listOf(head, neste))

        assertThat(dialog.hentMeldinger(head.fnr, "0101")).isEqualTo(forventet)
        assertThat(forventet.single().traadId).isEqualTo("head")
        assertThat(forventet.single().opprettetDato).isEqualTo(neste.opprettetDato)
        assertThat(forventet.single().temagruppe).isEqualTo("FMLI")
        assertThat(forventet.single().meldinger.map { it.temagruppe }).containsExactly("FMLI", "FMLI")
        assertThat(forventet.single().meldinger).hasSize(2)
    }
}
