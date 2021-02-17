package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.gsak

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl.generated.HentIdent
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.FodselnummerAktorService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.SubjectHandlerUtil
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.OpprettSakDto
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakApiGateway
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.*


class RestGsakSakerTest {
    val VEDTAKSLOSNINGEN = "FS36"
    val FIRE_DAGER_SIDEN = DateTime.now().minusDays(4) //joda.DateTime
    val FNR = "fnr"
    val SakId_1 = "1"
    val FagsystemSakId_1 = "11"

    val fixedClock = Clock.fixed(Instant.parse("2021-01-25T10:15:30Z"), ZoneId.systemDefault())
    val api = mockk<SakApiGateway>()
    val fodselnummerAktorService = mockk<FodselnummerAktorService>()
    val restClient = RestGsakSaker(api, fodselnummerAktorService, fixedClock)

    @Test
    fun `legg til saker legger til sakene i listen`() {
        every { fodselnummerAktorService.hentAktorIdForFnr(any()) } returns "123"
        every { fodselnummerAktorService.hentFnrForAktorId(any()) } returns "456"
        every { api.hentSaker(any()) } returns listOf(
            SakDto(
                id = SakId_1,
                tema = "AAP",
                applikasjon = VEDTAKSLOSNINGEN,
                aktoerId = "123",
                orgnr = null,
                fagsakNr = FagsystemSakId_1,
                opprettetAv = null,
                opprettetTidspunkt = earlierDateTimeWithOffSet(4)
            )
        )
        val saker = mutableListOf<Sak>()
        restClient.leggTilSaker("fnr", saker)

        assertThat(saker.size, `is`(1));
    }

    @Test
    fun `ved feil kastes feilene videre`() {
        every { api.hentSaker(any()) } throws IllegalStateException("Ukjent feil")
        every { fodselnummerAktorService.hentAktorIdForFnr(any()) } returns "123"
        every { fodselnummerAktorService.hentFnrForAktorId(any()) } returns "456"

        val saker = mutableListOf<Sak>()
        assertThrows<IllegalStateException> {
            restClient.leggTilSaker("fnr", saker)
        }
    }

    @Test
    fun `opprett oppgave kaller api`() {
        val sakDto = SakDto(
            id = SakId_1,
            tema = "AAP",
            applikasjon = "FS22",
            fagsakNr = FagsystemSakId_1
        )
        every { api.opprettSak(any()) } returns sakDto
        every { fodselnummerAktorService.hentAktorIdForFnr(any()) } returns "123"
        every { fodselnummerAktorService.hentFnrForAktorId(any()) } returns "456"
        SubjectHandlerUtil.withIdent("Z999999") {
            restClient.opprettSak("fnr", RestGsakSaker.TIL_SAK(sakDto))
        }

        verify {
            api.opprettSak(
                OpprettSakDto(
                    aktoerId = "123",
                    tema = "AAP",
                    fagsakNr = FagsystemSakId_1,
                    applikasjon = "FS22",
                    opprettetAv = "Z999999"
                )
            )
        }
    }

    @Test
    fun `transformasjonen genererer relevante felter`() {
        val sakDto = SakDto(
            id = SakId_1,
            tema = "AAP",
            applikasjon = "PP01",
            aktoerId = "123",
            orgnr = null,
            fagsakNr = null,
            opprettetAv = null,
            opprettetTidspunkt = earlierDateTimeWithOffSet(4)
        )
        val sak = RestGsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(nullValue()))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_GENERELL))
        assertThat(sak.fagsystemKode, `is`(Sak.FAGSYSTEMKODE_PSAK))
        assertThat(sak.opprettetDato, dateMatcher(`is`(true)))
        assertThat(sak.finnesIGsak, `is`(true))
    }

    @Test
    fun `transformasjon setter default verdi for fagsystemKode og sakstype om applikasjon er null fra rest-tjenesten`() {
        val sakDto = SakDto(
            id = SakId_1,
            tema = "AAP",
            applikasjon = null,
            aktoerId = "123",
            orgnr = null,
            fagsakNr = null,
            opprettetAv = null,
            opprettetTidspunkt = earlierDateTimeWithOffSet(4)
        )
        val sak = RestGsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.fagsystemKode, `is`(Sak.FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_GENERELL))
    }

    @Test
    fun `transformasjonen bruker SaksId for fagsystemId og MFS som sakstype om fagsystem er der vedtakslosningen ikke leverer fagsystemSakId`() {
        val sakDto = SakDto(
            id = SakId_1,
            tema = "AAP",
            applikasjon = VEDTAKSLOSNINGEN,
            aktoerId = "123",
            orgnr = null,
            opprettetAv = null,
            opprettetTidspunkt = earlierDateTimeWithOffSet(4)
        )

        val sak = RestGsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(SakId_1))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_MED_FAGSAK))
        assertThat(sak.fagsystemKode, `is`(VEDTAKSLOSNINGEN))
        assertThat(sak.opprettetDato, dateMatcher(`is`(true)))
        assertThat(sak.finnesIGsak, `is`(true))
    }

    @Test
    fun `skal handtere manglede fagsystemSakId`() {
        val sakDto = SakDto()
        Assertions.assertDoesNotThrow { RestGsakSaker.TIL_SAK.invoke(sakDto) }
    }

    @Test
    fun `kal handtere at FS36 har fagsystemId`() {
        val sakDto = SakDto(
            id = SakId_1,
            tema = "AAP",
            applikasjon = VEDTAKSLOSNINGEN,
            aktoerId = "123",
            orgnr = null,
            fagsakNr = FagsystemSakId_1,
            opprettetAv = null,
            opprettetTidspunkt = earlierDateTimeWithOffSet(4)
        )

        val sak = RestGsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(FagsystemSakId_1))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_MED_FAGSAK))
        assertThat(sak.fagsystemKode, `is`(VEDTAKSLOSNINGEN))
        assertThat(sak.opprettetDato, dateMatcher(`is`(true)))
        assertThat(sak.finnesIGsak, `is`(true))

    }


    fun dateMatcher(matcher: Matcher<in Boolean?>?): Matcher<DateTime?>? {
        return object : FeatureMatcher<DateTime, Boolean?>(matcher, "Date comparison", "dateMatcher") {
            override fun featureValueOf(actual: DateTime): Boolean? {
                return actual.millis - FIRE_DAGER_SIDEN.millis < 1000
            }

        }
    }

    fun earlierDateTimeWithOffSet(offset: Long): OffsetDateTime = OffsetDateTime.now().minusDays(offset)
}

