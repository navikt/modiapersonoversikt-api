package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.arena.ytelseskontrakt.YtelseskontraktResponse
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.YtelseskontraktRequest
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.YtelseskontraktService
import no.nav.modiapersonoversikt.consumer.infotrygd.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.infotrgd.sykepenger.SykepengerResponse
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import no.nav.modiapersonoversikt.rest.RequestBodyContent
import org.joda.time.IllegalFieldValueException
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class YtelseController
    @Autowired
    constructor(
        private val sykepengerService: SykepengerServiceBi,
        private val ytelseskontraktService: YtelseskontraktService,
    ) {
        @PostMapping("/ytelseskontrakter")
        fun hentYtelseskontrakter(
            @RequestBody body: RequestBodyContent,
        ): YtelseskontraktResponse =
            ytelseskontraktService.hentYtelseskontrakter(
                lagYtelseRequest(
                    body.fnr,
                    body.start,
                    body.slutt,
                ),
            )

        @PostMapping("sykepenger")
        fun hentSykepenger(
            @RequestBody body: RequestBodyContent,
        ): SykepengerResponse =
            SykepengerUttrekk(sykepengerService).hent(
                body.fnr,
                body.start?.let { lagRiktigDato(it) },
                body.slutt?.let { lagRiktigDato(it) },
            )

        private fun lagYtelseRequest(
            fnr: String,
            start: String?,
            slutt: String?,
        ): YtelseskontraktRequest {
            val request =
                YtelseskontraktRequest()
            request.fodselsnummer = fnr
            request.from = lagRiktigDato(start)
            request.to = lagRiktigDato(slutt)
            return request
        }

        private fun lagRiktigDato(dato: String?): LocalDate? =
            dato?.let {
                try {
                    LocalDate.parse(dato, JODA_DATOFORMAT)
                } catch (exception: IllegalFieldValueException) {
                    throw RuntimeException(exception.message)
                }
            }
    }
