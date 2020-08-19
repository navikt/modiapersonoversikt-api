package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.naudit.Audit
import no.nav.sbl.dialogarena.naudit.AuditIdentifier
import no.nav.sbl.dialogarena.naudit.AuditResources
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ytelse")
class YtelseController @Autowired constructor(private val sykepengerService: SykepengerServiceBi,
                                              private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
                                              private val pleiepengerService: PleiepengerService,
                                              private val tilgangskontroll: Tilgangskontroll,
                                              private val organisasjonService: OrganisasjonService) {

    @GetMapping("sykepewnger/{fnr}")
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
