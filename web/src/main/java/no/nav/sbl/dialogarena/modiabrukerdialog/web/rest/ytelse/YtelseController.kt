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
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/ytelse")
@Produces(MediaType.APPLICATION_JSON)
class YtelseController @Inject constructor(private val sykepengerService: SykepengerServiceBi,
                                           private val foreldrepengerServiceDefault: ForeldrepengerServiceBi,
                                           private val pleiepengerService: PleiepengerService,
                                           private val tilgangskontroll: Tilgangskontroll,
                                           private val organisasjonService: OrganisasjonService) {

    @GET
    @Path("sykepenger/{fnr}")
    fun hentSykepenger(@PathParam("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Sykepenger, AuditIdentifier.FNR to fnr)) {
                    SykepengerUttrekk(sykepengerService).hent(fnr)
                }
    }

    @GET
    @Path("foreldrepenger/{fnr}")
    fun hentForeldrepenger(@PathParam("fnr") fnr: String): Map<String, Any?> {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(Audit.Action.READ, AuditResources.Person.Foreldrepenger, AuditIdentifier.FNR to fnr)) {
                    ForeldrepengerUttrekk(getForeldrepengerService()).hent(fnr)
                }
    }

    @GET
    @Path("pleiepenger/{fnr}")
    fun hentPleiepenger(@PathParam("fnr") fnr: String): Map<String, Any?> {
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
