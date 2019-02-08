package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.Feature
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
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
                                           private val forelderpengerService: ForeldrepengerServiceBi,
                                           private val pleiepengerService: PleiepengerService,
                                           private val unleashService: UnleashService) {

    @GET
    @Path("sykepenger/{fnr}")
    fun hentSykepenger(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        return SykepengerUttrekk(sykepengerService).hent(fødselsnummer)
    }

    @GET
    @Path("foreldrepenger/{fnr}")
    fun hentForeldrepenger(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        return ForeldrepengerUttrekk(forelderpengerService).hent(fødselsnummer)
    }

    @GET
    @Path("pleiepenger/{fnr}")
    fun hentPleiepenger(@PathParam("fnr") fødselsnummer: String): Map<String, Any?> {
        check(unleashService.isEnabled(Feature.NYTT_VISITTKORT))

        return PleiepengerUttrekk(pleiepengerService).hent(fødselsnummer)
    }
}