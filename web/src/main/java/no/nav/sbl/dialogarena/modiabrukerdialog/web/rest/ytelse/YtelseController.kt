package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse

import no.nav.kjerneinfo.consumer.organisasjon.OrganisasjonService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.MockableContext.KJERNEINFO_KEY
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.MockUtil.mockErTillattOgSlaattPaaForKey
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.Wrapper
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.tilgangTilBruker
import no.nav.sykmeldingsperioder.consumer.foreldrepenger.ForeldrepengerServiceBi
import no.nav.sykmeldingsperioder.consumer.pleiepenger.PleiepengerService
import no.nav.sykmeldingsperioder.consumer.sykepenger.SykepengerServiceBi
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/ytelse")
@Produces(MediaType.APPLICATION_JSON)
class YtelseController @Inject constructor(private val sykepengerService: SykepengerServiceBi,
                                           @Named("foreldrepengerServiceDefault") private val foreldrepengerServiceDefault: Wrapper<ForeldrepengerServiceBi>,
                                           @Named("foreldrepengerServiceMock") private val foreldrepengerServiceMock: Wrapper<ForeldrepengerServiceBi>,
                                           private val pleiepengerService: PleiepengerService,
                                           private val tilgangskontroll: Tilgangskontroll,
                                           private val organisasjonService: OrganisasjonService) {

    @GET
    @Path("sykepenger/{fnr}")
    fun hentSykepenger(@PathParam("fnr") fodselsnummer: String): Map<String, Any?> {
        return tilgangskontroll
                .tilgangTilBruker(fodselsnummer)
                .get { SykepengerUttrekk(sykepengerService).hent(fodselsnummer) }
    }

    @GET
    @Path("foreldrepenger/{fnr}")
    fun hentForeldrepenger(@PathParam("fnr") fodselsnummer: String): Map<String, Any?> {
        return tilgangskontroll
                .tilgangTilBruker(fodselsnummer)
                .get { ForeldrepengerUttrekk(getForeldrepengerService()).hent(fodselsnummer) }
    }

    @GET
    @Path("pleiepenger/{fnr}")
    fun hentPleiepenger(@PathParam("fnr") fodselsnummer: String): Map<String, Any?> {
        return tilgangskontroll
                .tilgangTilBruker(fodselsnummer)
                .get { PleiepengerUttrekk(pleiepengerService, organisasjonService).hent(fodselsnummer) }
    }

    private fun getForeldrepengerService(): ForeldrepengerServiceBi {
        return ForeldrepengerServiceBi { request ->
            if (mockErTillattOgSlaattPaaForKey(KJERNEINFO_KEY)) {
                foreldrepengerServiceMock.wrappedObject.hentForeldrepengerListe(request)
            } else foreldrepengerServiceDefault.wrappedObject.hentForeldrepengerListe(request)
        }
    }
}