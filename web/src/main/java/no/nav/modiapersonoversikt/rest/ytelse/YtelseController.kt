package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.modiapersonoversikt.infrastructure.naudit.Audit
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Policies
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService
import no.nav.modiapersonoversikt.integration.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import no.nav.modiapersonoversikt.legacy.kjerneinfo.consumer.organisasjon.OrganisasjonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/ytelse")
class YtelseController @Autowired constructor(
    private val sykepengerService: SykepengerServiceBi,
    private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
    private val pleiepengerService: PleiepengerService,
    private val tilgangskontroll: Tilgangskontroll,
    private val organisasjonService: OrganisasjonService
) {

    @GetMapping("sykepenger/{fnr}")
    fun hentSykepenger(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnr)) {
                SykepengerUttrekk(sykepengerService).hent(fnr)
            }
    }

    @GetMapping("foreldrepenger/{fnr}")
    fun hentForeldrepenger(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnr)) {
                ForeldrepengerUttrekk(getForeldrepengerService()).hent(fnr)
            }
    }

    @GetMapping("pleiepenger/{fnr}")
    fun hentPleiepenger(@PathVariable("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker.with(fnr))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Pleiepenger, AuditIdentifier.FNR to fnr)) {
                PleiepengerUttrekk(pleiepengerService, organisasjonService).hent(fnr)
            }
    }

    private fun getForeldrepengerService(): ForeldrepengerServiceBi {
        return ForeldrepengerServiceBi { request ->
            foreldrepengerServiceDefault.hentForeldrepengerListe(request)
        }
    }
}
