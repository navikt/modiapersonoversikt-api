package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.commondomain.FnrRequest
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.pleiepenger.PleiepengerService
import no.nav.modiapersonoversikt.consumer.infotrygd.consumer.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/v2/ytelse")
class YtelseControllerV2 @Autowired constructor(
    private val sykepengerService: SykepengerServiceBi,
    private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
    private val pleiepengerService: PleiepengerService,
    private val tilgangskontroll: Tilgangskontroll,
    private val organisasjonService: OrganisasjonService
) {

    @PostMapping("sykepenger")
    fun hentSykepenger(@RequestBody fnrRequest: FnrRequest): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                SykepengerUttrekk(sykepengerService).hent(fnrRequest.fnr)
            }
    }

    @PostMapping("foreldrepenger")
    fun hentForeldrepenger(@RequestBody fnrRequest: FnrRequest): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                ForeldrepengerUttrekk(getForeldrepengerService()).hent(fnrRequest.fnr)
            }
    }

    @PostMapping("pleiepenger")
    fun hentPleiepenger(@RequestBody fnrRequest: FnrRequest): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnrRequest.fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Pleiepenger, AuditIdentifier.FNR to fnrRequest.fnr)) {
                PleiepengerUttrekk(pleiepengerService, organisasjonService).hent(fnrRequest.fnr)
            }
    }

    private fun getForeldrepengerService(): ForeldrepengerServiceBi {
        return ForeldrepengerServiceBi { request ->
            foreldrepengerServiceDefault.hentForeldrepengerListe(request)
        }
    }
}
