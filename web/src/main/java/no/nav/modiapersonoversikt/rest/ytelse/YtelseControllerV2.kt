package no.nav.modiapersonoversikt.rest.ytelse

import no.nav.common.types.identer.Fnr
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
@RequestMapping("/rest/ytelse")
class YtelseControllerV2 @Autowired constructor(
    private val sykepengerService: SykepengerServiceBi,
    private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
    private val pleiepengerService: PleiepengerService,
    private val tilgangskontroll: Tilgangskontroll,
    private val organisasjonService: OrganisasjonService
) {

    @PostMapping("sykepenger")
    fun hentSykepenger(@RequestBody fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnr)) {
                SykepengerUttrekk(sykepengerService).hent(fnr)
            }
    }

    @PostMapping("foreldrepenger")
    fun hentForeldrepenger(@RequestBody fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
            .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnr)) {
                ForeldrepengerUttrekk(getForeldrepengerService()).hent(fnr)
            }
    }

    @PostMapping("pleiepenger")
    fun hentPleiepenger(@RequestBody fnr: String): Map<String, Any?> {
        return tilgangskontroll
            .check(Policies.tilgangTilBruker(Fnr(fnr)))
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
