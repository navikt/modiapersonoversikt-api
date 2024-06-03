package no.nav.modiapersonoversiktproxy.rest.ytelse

import no.modiapersonoversikt.common.FnrRequest
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.YtelseskontraktService
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest
import no.nav.modiapersonoversiktproxy.consumer.arena.ytelseskontrakt.domain.YtelseskontraktResponse
import no.nav.modiapersonoversiktproxy.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.foreldrepenger.ForeldrepengerServiceBi
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.pleiepenger.PleiepengerService
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversiktproxy.rest.JODA_DATOFORMAT
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
    private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
    private val pleiepengerService: PleiepengerService,
    private val ytelseskontraktService: YtelseskontraktService,
    private val organisasjonService: OrganisasjonService,
) {
    @PostMapping("/ytelseskontrakter")
    fun hentYtelseskontrakter(
        @RequestBody fnrRequest: FnrRequest,
        @RequestParam("startDato") start: String?,
        @RequestParam("sluttDato") slutt: String?,
    ): YtelseskontraktResponse {
        return ytelseskontraktService.hentYtelseskontrakter(
            lagYtelseRequest(
                fnrRequest.fnr,
                start,
                slutt,
            ),
        )
    }

    @PostMapping("sykepenger")
    fun hentSykepenger(
        @RequestBody fnrRequest: FnrRequest,
    ): Map<String, Any?> {
        return SykepengerUttrekk(sykepengerService).hent(fnrRequest.fnr)
    }

    @PostMapping("foreldrepenger")
    fun hentForeldrepenger(
        @RequestBody fnrRequest: FnrRequest,
    ): Map<String, Any?> {
        return ForeldrepengerUttrekk(getForeldrepengerService()).hent(fnrRequest.fnr)
    }

    @PostMapping("pleiepenger")
    fun hentPleiepenger(
        @RequestBody fnrRequest: FnrRequest,
    ): Map<String, Any?> {
        return PleiepengerUttrekk(pleiepengerService, organisasjonService).hent(fnrRequest.fnr)
    }

    private fun getForeldrepengerService(): ForeldrepengerServiceBi {
        return ForeldrepengerServiceBi { request ->
            foreldrepengerServiceDefault.hentForeldrepengerListe(request)
        }
    }

    private fun lagYtelseRequest(
        fodselsnummer: String,
        start: String?,
        slutt: String?,
    ): YtelseskontraktRequest {
        val request =
            YtelseskontraktRequest()
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
