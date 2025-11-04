package no.nav.modiapersonoversikt.consumer.spokelse

import java.time.LocalDate
import no.nav.modiapersonoversikt.consumer.spokelse.SpokelseClient

open class SpokelseClientMock() : SpokelseClient {
    override fun hentUtbetalingsperiode(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Utbetalingsperioder = Utbetalingsperioder(listOf())

    override fun hentSykpengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykpengerVedtak> = listOf()
}
