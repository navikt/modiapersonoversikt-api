package no.nav.modiapersonoversiktproxy.rest.oppfolging

import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService
import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest
import no.nav.modiapersonoversiktproxy.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktResponse
import no.nav.modiapersonoversiktproxy.rest.JODA_DATOFORMAT
import no.nav.modiapersonoversiktproxy.utils.FnrRequest
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
            @RequestBody fnrRequest: FnrRequest,
            @RequestParam("startDato") start: String?,
            @RequestParam("sluttDato") slutt: String?,
        ): OppfolgingskontraktResponse {
            return oppfolgingskontraktService.hentOppfolgingskontrakter(
                lagOppfolgingskontraktRequest(fnrRequest.fnr, start, slutt),
            )
        }

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
