package no.nav.modiapersonoversikt.rest.oppfolging

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.arena.oppfolgingskontrakt.SYFOPunkt
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Dagpengeytelse
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Vedtak
import no.nav.modiapersonoversikt.arena.ytelseskontrakt.Ytelse
import no.nav.modiapersonoversikt.commondomain.Veileder
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.ArbeidsrettetOppfolging
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.Gjeldende14aVedtakResponse
import no.nav.modiapersonoversikt.consumer.veilarboppfolging.VeilarbvedtaksstotteService
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.JODA_DATOFORMAT
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/oppfolging")
class OppfolgingController
    @Autowired
    constructor(
        private val arenaInfotrygdApi: ArenaInfotrygdApi,
        private val veilarbvedtaksstotteService: VeilarbvedtaksstotteService,
        private val service: ArbeidsrettetOppfolging.Service,
        private val tilgangskontroll: Tilgangskontroll,
    ) {
        @PostMapping
        fun hent(
            @RequestBody fnrRequest: FnrRequest,
        ): OppfolgingDTO =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Oppfolging,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val oppfolging = service.hentOppfolgingsinfo(Fnr(fnrRequest.fnr))

                    OppfolgingDTO(oppfolging.erUnderOppfolging, oppfolging.veileder, oppfolging.oppfolgingsenhet)
                }

        @PostMapping("/ytelserogkontrakter")
        fun hentUtvidetOppf(
            @RequestBody fnrRequest: FnrRequest,
            @RequestParam("startDato") start: String?,
            @RequestParam("sluttDato") slutt: String?,
        ): UtvidetOppfolgingDTO =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.YtelserOgKontrakter,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val kontraktResponse =
                        arenaInfotrygdApi.hentOppfolgingskontrakter(fnrRequest.fnr, start, slutt)
                    val ytelserResponse =
                        arenaInfotrygdApi.hentYtelseskontrakter(
                            fnrRequest.fnr,
                            start,
                            slutt,
                        )

                    val oppfolgingstatus = runCatching { hent(fnrRequest) }

                    UtvidetOppfolgingDTO(
                        oppfolging = oppfolgingstatus.getOrNull(),
                        meldeplikt = kontraktResponse.bruker?.meldeplikt,
                        formidlingsgruppe = kontraktResponse.bruker?.formidlingsgruppe,
                        innsatsgruppe = kontraktResponse.bruker?.innsatsgruppe,
                        sykmeldtFra = kontraktResponse.bruker?.sykmeldtFrom?.toString(JODA_DATOFORMAT),
                        rettighetsgruppe = ytelserResponse.rettighetsgruppe,
                        vedtaksdato = kontraktResponse.vedtaksdato?.toString(JODA_DATOFORMAT),
                        sykefravaersoppfolging = hentSyfoPunkt(kontraktResponse.syfoPunkter),
                        ytelser = hentYtelser(ytelserResponse.ytelser),
                    )
                }

        @PostMapping("/hent-gjeldende-14a-vedtak")
        fun hentGjeldende14aVedtak(
            @RequestBody fnrRequest: FnrRequest,
        ): Gjeldende14aVedtakResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.oppfolging14aVedtak,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val gjeldende14aVedtak = veilarbvedtaksstotteService.hentGjeldende14aVedtak(Fnr(fnrRequest.fnr))
                    Gjeldende14aVedtakResponse(gjeldende14aVedtak)
                }

        @PostMapping("/sykefravaeroppfolging")
        fun hentSykefravaerOppfolging(
            @RequestBody fnrRequest: FnrRequest,
            @RequestParam("startDato") start: String?,
            @RequestParam("sluttDato") slutt: String?,
        ): SykefravaerOppfolgingDTO =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.sykefravaerOppfolging,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val response = arenaInfotrygdApi.hentOppfolgingskontrakter(fnrRequest.fnr, start, slutt)
                    SykefravaerOppfolgingDTO(
                        sykefravaersoppfolging = hentSyfoPunkt(response.syfoPunkter),
                    )
                }

        @PostMapping("/arbeidsoppfolging")
        fun hentArbeidsOppfolging(
            @RequestBody fnrRequest: FnrRequest,
            @RequestParam("startDato") start: String?,
            @RequestParam("sluttDato") slutt: String?,
        ): ArbeidsOppfolgingDTO =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.arbeidsOppfolging,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val response = arenaInfotrygdApi.hentOppfolgingskontrakter(fnrRequest.fnr, start, slutt)
                    val oppfolgingStatus = runCatching { hent(fnrRequest) }

                    ArbeidsOppfolgingDTO(
                        oppfolging = oppfolgingStatus.getOrNull(),
                        meldeplikt = response.bruker?.meldeplikt,
                        formidlingsgruppe = response.bruker?.formidlingsgruppe,
                        vedtaksdato = response.vedtaksdato?.toString(JODA_DATOFORMAT),
                        rettighetsgruppe = response.bruker?.rettighetsgruppe,
                    )
                }

        private fun hentYtelser(ytelser: List<Ytelse>?): List<YtelseDTO> {
            if (ytelser == null) return emptyList()

            return ytelser.map {
                when (it) {
                    is Dagpengeytelse ->
                        DagpengeytelseDTO(
                            datoKravMottatt = it.datoKravMottatt?.toString(JODA_DATOFORMAT),
                            fom = it.fom?.toString(JODA_DATOFORMAT),
                            tom = it.tom?.toString(JODA_DATOFORMAT),
                            status = it.status,
                            type = it.type,
                            vedtak = hentVedtak(it.vedtak),
                            dagerIgjenMedBortfall = it.dagerIgjenMedBortfall,
                            ukerIgjenMedBortfall = it.ukerIgjenMedBortfall,
                            dagerIgjenPermittering = it.antallDagerIgjenPermittering,
                            ukerIgjenPermittering = it.antallUkerIgjenPermittering,
                            dagerIgjen = it.antallDagerIgjen,
                            ukerIgjen = it.antallUkerIgjen,
                        )

                    else ->
                        OppfolgingsYtelseDTO(
                            datoKravMottatt = it.datoKravMottatt?.toString(JODA_DATOFORMAT),
                            fom = it.fom?.toString(JODA_DATOFORMAT),
                            tom = it.tom?.toString(JODA_DATOFORMAT),
                            status = it.status,
                            type = it.type,
                            vedtak = hentVedtak(it.vedtak),
                            dagerIgjenMedBortfall = it.dagerIgjenMedBortfall,
                            ukerIgjenMedBortfall = it.ukerIgjenMedBortfall,
                        )
                }
            }
        }

        private fun hentVedtak(vedtak: List<Vedtak>?): List<OppfolgingYtelseVedtakDTO> {
            if (vedtak == null) return emptyList()

            return vedtak.map {
                OppfolgingYtelseVedtakDTO(
                    aktivFra = it.activeFrom?.toString(JODA_DATOFORMAT),
                    aktivTil = it.activeTo?.toString(JODA_DATOFORMAT),
                    aktivitetsfase = it.aktivitetsfase,
                    vedtakstatus = it.vedtakstatus,
                    vedtakstype = it.vedtakstype,
                )
            }
        }

        private fun hentSyfoPunkt(syfoPunkter: List<SYFOPunkt>?): List<SyfoPunktDTO> {
            if (syfoPunkter == null) return emptyList()

            return syfoPunkter.map {
                SyfoPunktDTO(
                    dato = it.dato?.toString(JODA_DATOFORMAT),
                    fastOppfolgingspunkt = it.isFastOppfolgingspunkt,
                    status = it.status,
                    syfoHendelse = it.syfoHendelse,
                )
            }
        }
    }

data class OppfolgingDTO(
    val erUnderOppfolging: Boolean?,
    val veileder: Veileder?,
    val enhet: ArbeidsrettetOppfolging.OppfolgingsEnhet?,
)

data class SyfoPunktDTO(
    val fastOppfolgingspunkt: Boolean?,
    val dato: String?,
    val status: String?,
    val syfoHendelse: String?,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = OppfolgingsYtelseDTO::class, name = "OppfolgingsYtelseDTO"),
    JsonSubTypes.Type(value = DagpengeytelseDTO::class, name = "YtelseDTO"),
)
sealed class YtelseDTO

open class OppfolgingsYtelseDTO(
    open val type: String?,
    open val status: String?,
    open val datoKravMottatt: String?,
    open val vedtak: List<OppfolgingYtelseVedtakDTO>? = listOf(),
    open val fom: String?,
    open val tom: String?,
    open val dagerIgjenMedBortfall: Int?,
    open val ukerIgjenMedBortfall: Int?,
) : YtelseDTO()

data class ArbeidsOppfolgingDTO(
    val oppfolging: OppfolgingDTO?,
    val meldeplikt: Boolean?,
    val formidlingsgruppe: String?,
    val vedtaksdato: String?,
    val rettighetsgruppe: String?,
)

data class DagpengeytelseDTO(
    override val type: String?,
    override val status: String?,
    override val datoKravMottatt: String?,
    override val vedtak: List<OppfolgingYtelseVedtakDTO>? = listOf(),
    override val fom: String?,
    override val tom: String?,
    override val dagerIgjenMedBortfall: Int?,
    override val ukerIgjenMedBortfall: Int?,
    val dagerIgjenPermittering: Int?,
    val ukerIgjenPermittering: Int?,
    val dagerIgjen: Int?,
    val ukerIgjen: Int?,
) : OppfolgingsYtelseDTO(type, status, datoKravMottatt, vedtak, fom, tom, dagerIgjenMedBortfall, ukerIgjenMedBortfall)

data class OppfolgingYtelseVedtakDTO(
    val aktivFra: String?,
    val aktivTil: String?,
    val aktivitetsfase: String?,
    val vedtakstatus: String?,
    val vedtakstype: String?,
)

data class SykefravaerOppfolgingDTO(
    val sykefravaersoppfolging: List<SyfoPunktDTO>? = listOf(),
)

data class UtvidetOppfolgingDTO(
    val oppfolging: OppfolgingDTO?,
    val meldeplikt: Boolean?,
    val formidlingsgruppe: String?,
    val innsatsgruppe: String?,
    val sykmeldtFra: String?,
    val rettighetsgruppe: String?,
    val vedtaksdato: String?,
    val sykefravaersoppfolging: List<SyfoPunktDTO>? = listOf(),
    val ytelser: List<YtelseDTO>? = listOf(),
)
