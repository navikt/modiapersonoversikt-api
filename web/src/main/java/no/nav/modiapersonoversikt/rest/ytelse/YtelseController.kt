package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.api.domain.aap.generated.models.NonavaapapiinternVedtakUtenUtbetalingDTO
import no.nav.modiapersonoversikt.consumer.aap.AapApi
import no.nav.modiapersonoversikt.consumer.abakus.AbakusClient
import no.nav.modiapersonoversikt.consumer.abakus.Periode
import no.nav.modiapersonoversikt.consumer.abakus.YtelseV1
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonSak
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonService
import no.nav.modiapersonoversikt.consumer.tiltakspenger.TiltakspengerService
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.models.VedtakDTO
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.Foreldrepenger
import no.nav.modiapersonoversikt.infotrgd.foreldrepenger.ForeldrepengerResponse
import no.nav.modiapersonoversikt.infotrgd.pleiepenger.Pleiepenger
import no.nav.modiapersonoversikt.infotrgd.pleiepenger.PleiepengerResponse
import no.nav.modiapersonoversikt.infotrgd.sykepenger.Sykepenger
import no.nav.modiapersonoversikt.infotrgd.sykepenger.SykepengerResponse
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.rest.common.FnrDatoRangeRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import no.nav.modiapersonoversikt.consumer.abakus.YtelseType as AbakusYtelseType

@RestController
@RequestMapping("/rest/ytelse")
class YtelseController
    @Autowired
    constructor(
        private val arenaInfotrygdApi: ArenaInfotrygdApi,
        private val tilgangskontroll: Tilgangskontroll,
        private val tiltakspengerService: TiltakspengerService,
        private val pensjonService: PensjonService,
        private val aapApi: AapApi,
        private val abakusClient: AbakusClient
    ) {
        @PostMapping("alle-ytelser")
        fun hentYtelser(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): YtelseResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Ytelser,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    val sykepenger =
                        arenaInfotrygdApi.hentSykepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom).sykepenger?.map {
                            YtelseVedtak(YtelseType.Sykepenger, YtelseData.SykepengerYtelse(it))
                        }
                            ?: listOf()
                    val pleiepenger =
                        arenaInfotrygdApi.hentPleiepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom).pleiepenger?.map {
                            YtelseVedtak(YtelseType.Pleiepenger, YtelseData.PleiepengerYtelse(it))
                        }
                            ?: listOf()
                    val foreldrepenger =
                        arenaInfotrygdApi.hentForeldrepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom).foreldrepenger?.map {
                            YtelseVedtak(
                                YtelseType.Foreldrepenger,
                                YtelseData.ForeldrepengerYtelse(it),
                            )
                        }
                            ?: listOf()
                    val tiltakspenger =
                        tiltakspengerService.hentVedtakPerioder(Fnr(fnrRequest.fnr), fnrRequest.fom, fnrRequest.tom).map {
                            YtelseVedtak(YtelseType.Tiltakspenge, YtelseData.TiltakspengerYtelse(it))
                        }
                    val pensjon =
                        pensjonService
                            .hentSaker(
                                fnrRequest.fnr,
                            ).map { YtelseVedtak(YtelseType.Pensjon, YtelseData.PensjonYtelse(it)) }

                    val arbeidsavklaringspenger =
                        aapApi
                            .hentArbeidsavklaringspengerSistePeriodePerVedtak(
                                fnrRequest.fnr,
                                fnrRequest.tom,
                                fnrRequest.fom,
                            ).map { YtelseVedtak(YtelseType.Arbeidsavklaringspenger, YtelseData.ArbeidsavklaringsPengerYtelse(it)) }

                    YtelseResponse(
                        ytelser = sykepenger + pleiepenger + foreldrepenger + tiltakspenger + pensjon + arbeidsavklaringspenger,
                    )
                }

        @PostMapping("sykepenger")
        fun hentSykepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): SykepengerResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    arenaInfotrygdApi.hentSykepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                }

        @PostMapping("abakus_ytelser")
        fun hentAbakusYitelser(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<YtelseV1> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.AbakusYtelser, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    abakusClient.hentYtelser(fnrRequest.fnr, Periode(fnrRequest.fom, fnrRequest.tom),
                        AbakusYtelseType.entries
                    )
                }

        @PostMapping("foreldrepenger")
        fun hentForeldrepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): ForeldrepengerResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    arenaInfotrygdApi.hentForeldrepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                }

        @PostMapping("pleiepenger")
        fun hentPleiepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): PleiepengerResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Pleiepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    arenaInfotrygdApi.hentPleiepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                }

        @PostMapping("tiltakspenger")
        fun hentTiltakspenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<VedtakDTO> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Tiltakspenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                    tiltakspengerService.hentVedtakPerioder(Fnr(fnrRequest.fnr), fnrRequest.fom, fnrRequest.tom)
                }

        @PostMapping("pensjon")
        fun hentPensjonSaker(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<PensjonSak> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Pensjon,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    pensjonService.hentSaker(fnrRequest.fnr)
                }

        @PostMapping("arbeidsavklaringspenger")
        fun hentArbeidsavklaringsPenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<NonavaapapiinternVedtakUtenUtbetalingDTO> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.ArbeidsavklaringsPenger,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    aapApi.hentArbeidsavklaringspengerSistePeriodePerVedtak(
                        fnrRequest.fnr,
                        fnrRequest.tom,
                        fnrRequest.fom,
                    )
                }
    }

sealed class YtelseData {
    data class SykepengerYtelse(
        val data: Sykepenger,
    ) : YtelseData()

    data class ForeldrepengerYtelse(
        val data: Foreldrepenger,
    ) : YtelseData()

    data class PleiepengerYtelse(
        val data: Pleiepenger,
    ) : YtelseData()

    data class TiltakspengerYtelse(
        val data: VedtakDTO,
    ) : YtelseData()

    data class PensjonYtelse(
        val data: PensjonSak,
    ) : YtelseData()

    data class ArbeidsavklaringsPengerYtelse(
        val data: NonavaapapiinternVedtakUtenUtbetalingDTO,
    ) : YtelseData()
}

enum class YtelseType {
    Sykepenger,
    Foreldrepenger,
    Pleiepenger,
    Tiltakspenge,
    Pensjon,
    Arbeidsavklaringspenger,
}

data class YtelseVedtak(
    val ytelseType: YtelseType,
    val ytelseData: YtelseData,
)

data class YtelseResponse(
    val ytelser: List<YtelseVedtak>?,
)
