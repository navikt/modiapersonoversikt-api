package no.nav.modiapersonoversikt.rest.oppfolging

import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.OppfolgingskontraktResponse
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktRequest
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import no.nav.modiapersonoversikt.rest.RequestBodyContent
import org.joda.time.IllegalFieldValueException
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class OppfolgingController
    @Autowired
    constructor(
        private val oppfolgingskontraktService: OppfolgingskontraktService,
    ) {
        @PostMapping("/Oppfolgingskontrakter")
        fun hentOppfolgingskontrakter(
            @RequestBody body: RequestBodyContent,
        ): OppfolgingskontraktResponse =
            oppfolgingskontraktService.hentOppfolgingskontrakter(
                lagOppfolgingskontraktRequest(body.fnr, body.start, body.slutt),
            )

        private fun lagOppfolgingskontraktRequest(
            fodselsnummer: String,
            start: String?,
            slutt: String?,
        ): OppfolgingskontraktRequest {
            val request =
                OppfolgingskontraktRequest()
            request.fodselsnummer = fodselsnummer
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
