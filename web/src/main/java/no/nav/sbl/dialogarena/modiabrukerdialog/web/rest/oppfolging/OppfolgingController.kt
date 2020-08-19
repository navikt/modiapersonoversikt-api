package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppfolging

import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.OppfolgingskontraktServiceBi
import no.nav.kontrakter.consumer.fim.oppfolgingskontrakt.to.OppfolgingskontraktRequest
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.YtelseskontraktServiceBi
import no.nav.kontrakter.consumer.fim.ytelseskontrakt.to.YtelseskontraktRequest
import no.nav.kontrakter.domain.oppfolging.SYFOPunkt
import no.nav.kontrakter.domain.ytelse.Dagpengeytelse
import no.nav.kontrakter.domain.ytelse.Vedtak
import no.nav.kontrakter.domain.ytelse.Ytelse
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.oppfolgingsinfo.OppfolgingsinfoApiService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.DATOFORMAT
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.lagRiktigDato
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.Audit.Action.READ
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources.Person
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/oppfolging/{fnr}")
class OppfolgingController @Autowired constructor(private val service: OppfolgingsinfoApiService,
                                                  private val ldapService: LDAPService,
                                                  private val tilgangskontroll: Tilgangskontroll,
                                                  private val ytelseskontraktService: YtelseskontraktServiceBi,
                                                  private val oppfolgingskontraktService: OppfolgingskontraktServiceBi) {

    private val logger = LoggerFactory.getLogger(OppfolgingController::class.java)

    @GetMapping
    fun hent(@PathVariable("fnr") fodselsnummer: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fodselsnummer))
                .get(Audit.describe(READ, Person.Oppfolging, AuditIdentifier.FNR to fodselsnummer)) {
                    val oppfolging = service.hentOppfolgingsinfo(fodselsnummer, ldapService)

                    mapOf(
                            "erUnderOppfølging" to oppfolging.erUnderOppfolging,
                            "veileder" to hentVeileder(oppfolging.veileder),
                            "enhet" to hentEnhet(oppfolging.oppfolgingsenhet)
                    )
                }
    }

    @GetMapping("/ytelserogkontrakter")
    fun hentUtvidetOppf(@PathVariable("fnr") fodselsnummer: String,
                        @RequestParam("startDato") start: String?,
                        @RequestParam("sluttDato") slutt: String?): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fodselsnummer))
                .get(Audit.describe(READ, Person.YtelserOgKontrakter, AuditIdentifier.FNR to fodselsnummer)) {
                    val kontraktResponse = oppfolgingskontraktService.hentOppfolgingskontrakter(lagOppfolgingskontraktRequest(fodselsnummer, start, slutt))
                    val ytelserResponse = ytelseskontraktService.hentYtelseskontrakter(lagYtelseRequest(fodselsnummer, start, slutt))

                    mapOf(
                            "oppfølging" to hent(fodselsnummer),
                            "meldeplikt" to kontraktResponse.bruker?.meldeplikt,
                            "formidlingsgruppe" to kontraktResponse.bruker?.formidlingsgruppe,
                            "innsatsgruppe" to kontraktResponse.bruker?.innsatsgruppe,
                            "sykmeldtFra" to kontraktResponse.bruker?.sykmeldtFrom?.toString(DATOFORMAT),
                            "rettighetsgruppe" to ytelserResponse.rettighetsgruppe,
                            "vedtaksdato" to kontraktResponse.vedtaksdato?.toString(DATOFORMAT),
                            "sykefraværsoppfølging" to hentSyfoPunkt(kontraktResponse.syfoPunkter),
                            "ytelser" to hentYtelser(ytelserResponse.ytelser)
                    )
                }
    }
}

private fun hentYtelser(ytelser: List<Ytelse>?): List<Map<String, Any?>> {
    if (ytelser == null) return emptyList()

    return ytelser.map {
        mapOf(
                "datoKravMottatt" to it.datoKravMottat?.toString(DATOFORMAT),
                "fom" to it.fom?.toString(DATOFORMAT),
                "tom" to it.tom?.toString(DATOFORMAT),
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
                "aktivFra" to it.activeFrom?.toString(DATOFORMAT),
                "aktivTil" to it.activeTo?.toString(DATOFORMAT),
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
                "dato" to it.dato?.toString(DATOFORMAT),
                "fastOppfølgingspunkt" to it.isFastOppfolgingspunkt,
                "status" to it.status,
                "syfoHendelse" to it.syfoHendelse
        )
    }
}

private fun hentVeileder(veileder: Optional<Saksbehandler>): Map<String, Any?>? {
    return if (veileder.isPresent) {
        mapOf(
                "ident" to veileder.get().ident,
                "navn" to veileder.get().navn
        )
    } else {
        null
    }
}

private fun hentEnhet(enhet: Optional<AnsattEnhet>): Map<String, Any?>? {
    return if (enhet.isPresent) {
        mapOf(
                "id" to enhet.get().enhetId,
                "navn" to enhet.get().enhetNavn,
                "status" to enhet.get().status
        )
    } else {
        null
    }
}

private fun lagYtelseRequest(fodselsnummer: String, start: String?, slutt: String?): YtelseskontraktRequest {
    val request = YtelseskontraktRequest()
    request.fodselsnummer = fodselsnummer
    request.from = lagRiktigDato(start)
    request.to = lagRiktigDato(slutt)
    return request
}

private fun lagOppfolgingskontraktRequest(fodselsnummer: String, start: String?, slutt: String?): OppfolgingskontraktRequest {
    val request = OppfolgingskontraktRequest()
    request.fodselsnummer = fodselsnummer
    request.from = lagRiktigDato(start)
    request.to = lagRiktigDato(slutt)
    return request
}

