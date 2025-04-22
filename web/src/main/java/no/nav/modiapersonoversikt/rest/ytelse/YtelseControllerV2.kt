package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonEtteroppgjorshistorikk
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/v2/ytelse")
class YtelseControllerV2
    @Autowired
    constructor(
        private val arenaInfotrygdApi: ArenaInfotrygdApi,
        private val tilgangskontroll: Tilgangskontroll,
        private val tiltakspengerService: TiltakspengerService,
        private val pensjonService: PensjonService,
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
                    val sykepengerResponse =
                        arenaInfotrygdApi.hentSykepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                    val pleiepengerResponse =
                        arenaInfotrygdApi.hentPleiepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                    val foreldrepengerResponse =
                        arenaInfotrygdApi.hentForeldrepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                    val tiltakspenger =
                        tiltakspengerService.hentVedtakPerioder(Fnr(fnrRequest.fnr), fnrRequest.fom, fnrRequest.tom)

                    YtelseResponse(
                        sykepenger = sykepengerResponse.sykepenger,
                        foreldrepenger = foreldrepengerResponse.foreldrepenger,
                        pleiepenger = pleiepengerResponse.pleiepenger,
                        tiltakspenger = tiltakspenger,
                        pensjon = pensjonService.hentSaker(fnrRequest.fnr),
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

        @PostMapping("pensjon/{sakId}")
        fun hentPensjonSakEtteroppgjorshistorikk(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
            @PathVariable("sakId") sakId: String,
        ): List<PensjonEtteroppgjorshistorikk> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Pensjon,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    pensjonService.hentEtteroppgjorshistorikk(fnrRequest.fnr, sakId)
                }
    }

data class YtelseResponse(
    val sykepenger: List<Sykepenger>?,
    val foreldrepenger: List<Foreldrepenger>?,
    val pleiepenger: List<Pleiepenger>?,
    val tiltakspenger: List<VedtakDTO>?,
    val pensjon: List<PensjonSak>?,
)
