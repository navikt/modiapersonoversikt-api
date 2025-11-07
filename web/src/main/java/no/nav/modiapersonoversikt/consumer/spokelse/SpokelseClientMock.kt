package no.nav.modiapersonoversikt.consumer.spokelse

import java.time.LocalDate
import no.nav.modiapersonoversikt.consumer.spokelse.SpokelseClient

open class SpokelseClientMock() : SpokelseClient {
    override fun hentUtbetaltePerioder(
        fnr: String,
        fom: LocalDate,
        tom: LocalDate,
    ): UtbetaltePerioder = UtbetaltePerioder(listOf())

    override fun hentSykepengerVedtak(
        fnr: String,
        fom: LocalDate,
    ): List<SykepengerVedtak> = listOf()
}
