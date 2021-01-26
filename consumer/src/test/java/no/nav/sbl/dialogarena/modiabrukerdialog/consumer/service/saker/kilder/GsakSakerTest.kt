package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.FIRE_DAGER_SIDEN
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.FagsystemSakId_1
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.SakId_1
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.VEDTAKSLOSNINGEN
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.kilder.SakDataGenerator.Companion.earlierDateTimeWithOffSet
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.mediation.SakDto
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.FeatureMatcher
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class GsakSakerTest {

    @Test
    fun `transformasjonen genererer relevante felter`() {
        val sakDto = SakDto(id = SakId_1,
                tema = "AAP",
                applikasjon = "PP01",
                aktoerId = "123",
                orgnr = null,
                fagsakNr = null,
                opprettetAv = null,
                opprettetTidspunkt = earlierDateTimeWithOffSet(4))
        val sak = GsakSaker.TIL_SAK.invoke(sakDto)
        assertThat(sak.saksId, `is`(SakId_1))
        assertThat(sak.fagsystemSaksId, `is`(nullValue()))
        assertThat(sak.temaKode, `is`(Sak.GODKJENTE_TEMA_FOR_GENERELL_SAK[0]))
        assertThat(sak.sakstype, `is`(Sak.SAKSTYPE_GENERELL))
        assertThat(sak.fagsystemKode, `is`(Sak.FAGSYSTEMKODE_PSAK))
        assertThat(sak.opprettetDato, dateMatcher(`is`(true)))
        assertThat(sak.finnesIGsak, `is`(true))
    }

    @Test
    fun `transformasjonen bruker SaksId for fagsystemId og MFS som sakstype om fagsystem er vedtakslosningen`() {
        val sakDto = SakDto(id = SakId_1,
                tema = "AAP",
                applikasjon = VEDTAKSLOSNINGEN,
                aktoerId = "123",
                orgnr = null,
                fagsakNr = FagsystemSakId_1,
                opprettetAv = null,
                opprettetTidspunkt = earlierDateTimeWithOffSet(4))

        val sak = GsakSaker.TIL_SAK.invoke(sakDto)
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
        Assertions.assertDoesNotThrow { GsakSaker.TIL_SAK.invoke(sakDto) }
    }

    fun dateMatcher(matcher: Matcher<in Boolean?>?): Matcher<DateTime?>? {
        return object : FeatureMatcher<DateTime, Boolean?>(matcher, "Date comparison", "dateMatcher") {
            override fun featureValueOf(actual: DateTime): Boolean? {
                return actual.millis - FIRE_DAGER_SIDEN.millis < 1000
            }

        }
    }
}

