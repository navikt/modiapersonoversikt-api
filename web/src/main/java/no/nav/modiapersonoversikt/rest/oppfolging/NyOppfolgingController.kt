package no.nav.modiapersonoversikt.rest.oppfolging

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.OppfolgingskontraktService
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.OppfolgingskontraktRequest
import no.nav.modiapersonoversikt.consumer.arena.oppfolgingskontrakt.domain.SYFOPunkt
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.YtelseskontraktService
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Dagpengeytelse
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Vedtak
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.Ytelse
import no.nav.modiapersonoversikt.consumer.arena.ytelseskontrakt.domain.YtelseskontraktRequest
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import no.nav.modiapersonoversikt.rest.Typeanalyzers
import org.joda.time.IllegalFieldValueException
import org.joda.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/oppfolging")
class NyOppfolgingController @Autowired constructor(
    private val service: ArbeidsrettetOppfolging.Service,
    private val tilgangskontroll: Tilgangskontroll,
    private val ytelseskontraktService: YtelseskontraktService,
    private val oppfolgingskontraktService: OppfolgingskontraktService
) {

    @PostMapping
    fun hent(@RequestBody fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(
                Audit.describe(
                    Audit.Action.READ,
                    AuditResources.Person.Oppfolging,
                    AuditIdentifier.FNR to fnr
                )
            ) {
                val oppfolging = service.hentOppfolgingsinfo(Fnr(fnr))

                mapOf(
                    "erUnderOppfolging" to oppfolging.erUnderOppfolging,
                    "veileder" to hentVeileder(oppfolging.veileder),
                    "enhet" to hentEnhet(oppfolging.oppfolgingsenhet)
                ).also(Typeanalyzers.OPPFOLGING_STATUS.analyzer::capture)
            }
    }

    @PostMapping("/ytelserogkontrakter")
    fun hentUtvidetOppf(
        @RequestBody fnr: String,
        @RequestParam("startDato") start: String?,
        @RequestParam("sluttDato") slutt: String?
    ): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(
                Audit.describe(
                    Audit.Action.READ,
                    AuditResources.Person.YtelserOgKontrakter,
                    AuditIdentifier.FNR to fnr
                )
            ) {
                val kontraktResponse = oppfolgingskontraktService.hentOppfolgingskontrakter(
                    lagOppfolgingskontraktRequest(fnr, start, slutt)
                )
                val ytelserResponse = ytelseskontraktService.hentYtelseskontrakter(
                    lagYtelseRequest(
                        fnr,
                        start,
                        slutt
                    )
                )
                val oppfolgingstatus = runCatching { hent(fnr) }

                mapOf(
                    "oppfolging" to oppfolgingstatus.getOrNull(),
                    "meldeplikt" to kontraktResponse.bruker?.meldeplikt,
                    "formidlingsgruppe" to kontraktResponse.bruker?.formidlingsgruppe,
                    "innsatsgruppe" to kontraktResponse.bruker?.innsatsgruppe,
                    "sykmeldtFra" to kontraktResponse.bruker?.sykmeldtFrom?.toString(JODA_DATOFORMAT),
                    "rettighetsgruppe" to ytelserResponse.rettighetsgruppe,
                    "vedtaksdato" to kontraktResponse.vedtaksdato?.toString(JODA_DATOFORMAT),
                    "sykefraværsoppfølging" to hentSyfoPunkt(kontraktResponse.syfoPunkter),
                    "ytelser" to hentYtelser(ytelserResponse.ytelser)
                ).also(Typeanalyzers.OPPFOLGING_YTELSER.analyzer::capture)
            }
    }

    private fun hentYtelser(ytelser: List<Ytelse>?): List<Map<String, Any?>> {
        if (ytelser == null) return emptyList()

        return ytelser.map {
            mapOf(
                "datoKravMottatt" to it.datoKravMottat?.toString(JODA_DATOFORMAT),
                "fom" to it.fom?.toString(JODA_DATOFORMAT),
                "tom" to it.tom?.toString(JODA_DATOFORMAT),
                "status" to it.status,
                "type" to it.type,
                "vedtak" to hentVedtak(it.vedtak),
                "dagerIgjenMedBortfall" to it.dagerIgjenMedBortfall,
                "ukerIgjenMedBortfall" to it.ukerIgjenMedBortfall,
                *hentDagPengerFelter(it)
            )
        }
    }

    private fun hentDagPengerFelter(ytelse: Ytelse): Array<Pair<String, Any?>> {
        return when (ytelse) {
            is Dagpengeytelse -> arrayOf(
                "dagerIgjenPermittering" to ytelse.antallDagerIgjenPermittering,
                "ukerIgjenPermittering" to ytelse.antallUkerIgjenPermittering,
                "dagerIgjen" to ytelse.antallDagerIgjen,
                "ukerIgjen" to ytelse.antallUkerIgjen
            )
            else -> emptyArray()
        }
    }

    private fun hentVedtak(vedtak: List<Vedtak>?): List<Map<String, Any?>> {
        if (vedtak == null) return emptyList()

        return vedtak.map {
            mapOf(
                "aktivFra" to it.activeFrom?.toString(JODA_DATOFORMAT),
                "aktivTil" to it.activeTo?.toString(JODA_DATOFORMAT),
                "aktivitetsfase" to it.aktivitetsfase,
                "vedtakstatus" to it.vedtakstatus,
                "vedtakstype" to it.vedtakstype
            )
        }
    }

    private fun hentSyfoPunkt(syfoPunkter: List<SYFOPunkt>?): List<Map<String, Any?>> {
        if (syfoPunkter == null) return emptyList()

        return syfoPunkter.map {
            mapOf(
                "dato" to it.dato?.toString(JODA_DATOFORMAT),
                "fastOppfølgingspunkt" to it.isFastOppfolgingspunkt,
                "status" to it.status,
                "syfoHendelse" to it.syfoHendelse
            )
        }
    }

    private fun hentVeileder(veileder: Veileder?): Map<String, Any?>? {
        return veileder?.let {
            mapOf(
                "ident" to it.ident,
                "navn" to it.navn
            )
        }
    }

    private fun hentEnhet(enhet: ArbeidsrettetOppfolging.Enhet?): Map<String, Any?>? {
        return enhet?.let {
            mapOf(
                "id" to it.enhetId,
                "navn" to it.navn,
                "status" to null // TODO Ubrukt i frontend, men lar feltet være frem til det er fjernet fra domenemodellen der
            )
        }
    }

    private fun lagYtelseRequest(fodselsnummer: String, start: String?, slutt: String?): YtelseskontraktRequest {
        val request =
            YtelseskontraktRequest()
        request.fodselsnummer = fodselsnummer
        request.from = lagRiktigDato(start)
        request.to = lagRiktigDato(slutt)
        return request
    }

    private fun lagOppfolgingskontraktRequest(fodselsnummer: String, start: String?, slutt: String?): OppfolgingskontraktRequest {
        val request =
            OppfolgingskontraktRequest()
        request.fodselsnummer = fodselsnummer
        request.from = lagRiktigDato(start)
        request.to = lagRiktigDato(slutt)
        return request
    }

    private fun lagRiktigDato(dato: String?): LocalDate? = dato?.let {
        try {
            LocalDate.parse(dato, JODA_DATOFORMAT)
        } catch (exception: IllegalFieldValueException) {
            throw RuntimeException(exception.message)
        }
    }
}
