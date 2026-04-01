package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.api.domain.aap.generated.models.NonavaapapiinternVedtakUtenUtbetalingDTO
import no.nav.modiapersonoversikt.consumer.aap.AapApi
import no.nav.modiapersonoversikt.consumer.arenainfotrygdproxy.ArenaInfotrygdApi
import no.nav.modiapersonoversikt.consumer.dagpenger.DagpengerService
import no.nav.modiapersonoversikt.consumer.dagpenger.PseudoDagpengerVedtak
import no.nav.modiapersonoversikt.consumer.dagpenger.generated.models.DatadelingRequestDagpengerDto
import no.nav.modiapersonoversikt.consumer.foreldrepenger.Foreldrepenger
import no.nav.modiapersonoversikt.consumer.foreldrepenger.ForeldrepengerService
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonSak
import no.nav.modiapersonoversikt.consumer.pensjon.PensjonService
import no.nav.modiapersonoversikt.consumer.spokelse.SpokelseClient
import no.nav.modiapersonoversikt.consumer.spokelse.Utbetalingsperioder
import no.nav.modiapersonoversikt.consumer.tiltakspenger.TiltakspengerService
import no.nav.modiapersonoversikt.consumer.tiltakspenger.generated.models.VedtakDTO
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
        private val foreldrepengerService: ForeldrepengerService,
        private val dagpengerService: DagpengerService,
        private val spokelseClient: SpokelseClient,
    ) {
        @PostMapping("sykepenger")
        fun hentSykepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): SykepengerResponse =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Sykepenger,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    arenaInfotrygdApi.hentSykepenger(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                }

        @PostMapping("spokelse_sykepenger")
        fun hentSpokelseSykepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): Utbetalingsperioder =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.SpokelseSykepenger,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    spokelseClient.hentUtbetalingsperiode(fnrRequest.fnr, fnrRequest.fom, fnrRequest.tom)
                }


        @PostMapping("tiltakspenger")
        fun hentTiltakspenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<VedtakDTO> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Tiltakspenger,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
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

        @PostMapping("foreldrepenger")
        fun hentForeldrepenger(
            @RequestBody fnrRequest: FnrDatoRangeRequest,
        ): List<Foreldrepenger> =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Foreldrepenger,
                        AuditIdentifier.FNR to fnrRequest.fnr,
                    ),
                ) {
                    foreldrepengerService.hentForeldrepengerSortertPaaPeriode(
                        fnrRequest.fnr,
                        fnrRequest.fom,
                        fnrRequest.tom,
                    )
                }

        @PostMapping("dagpenger")
        fun hentDagpenger(
            @RequestBody datodelingRequest: DatadelingRequestDagpengerDto,
        ): PseudoDagpengerVedtak =
            tilgangskontroll
                .check(Policies.tilgangTilBruker(Fnr(datodelingRequest.personIdent)))
                .get(
                    Audit.describe(
                        Audit.Action.READ,
                        AuditResources.Person.Dagpenger,
                        AuditIdentifier.FNR to datodelingRequest.personIdent,
                    ),
                ) {
                    dagpengerService.hentVedtak(datodelingRequest)
                }
    }
