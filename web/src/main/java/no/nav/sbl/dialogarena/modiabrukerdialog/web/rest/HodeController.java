package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;

@Path("/hode")
@Produces(APPLICATION_JSON)
public class HodeController {

    @Inject
    private GrunninfoService grunninfoService;

    @Inject
    private AnsattService ansattService;

    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetService;

    @Inject
    Tilgangskontroll tilgangskontroll;

    class Me {
        public final String ident, navn, fornavn, etternavn, enhetId, enhetNavn;

        public Me(String ident, String fornavn, String etternavn, String enhetId, String enhetNavn) {
            this.ident = ident;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
            this.navn = fornavn + " " + etternavn;
            this.enhetId = enhetId;
            this.enhetNavn = enhetNavn;
        }
    }

    class Enhet {
        public final String enhetId, navn;

        public Enhet(String enhetId, String navn) {
            this.enhetId = enhetId;
            this.navn = navn;
        }
    }

    class Enheter {
        public final String ident;
        public final List<Enhet> enhetliste;

        public Enheter(String ident, List<Enhet> enhetliste) {
            this.ident = ident;
            this.enhetliste = enhetliste;
        }
    }

    @GET
    @Path("/me")
    public Me hentSaksbehandler(@Context HttpServletRequest request) {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(() -> {
                    String ident = getSubjectHandler().getUid();
                    GrunnInfo.SaksbehandlerNavn saksbehandler = grunninfoService.hentSaksbehandlerNavn();
                    String enhetId = hentValgtEnhet(request);
                    String enhetNavn = organisasjonEnhetService.hentEnhetGittEnhetId(enhetId, OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT)
                            .map((enhet) -> enhet.enhetNavn)
                            .orElse("[Ukjent enhetId: " + enhetId + "]");
                    return new Me(ident, saksbehandler.fornavn, saksbehandler.etternavn, enhetId, enhetNavn);
                });
    }

    @GET
    @Path("/enheter")
    public Enheter hentEnheter() {
        String ident = getSubjectHandler().getUid();
        List<Enhet> enheter = ansattService.hentEnhetsliste()
                .stream()
                .map((ansattEnhet) -> new Enhet(ansattEnhet.enhetId, ansattEnhet.enhetNavn))
                .collect(Collectors.toList());

        return new Enheter(ident, enheter);
    }

    @POST
    @Path("/velgenhet")
    public String settValgtEnhet(@Context HttpServletResponse response, String enhetId) {
        CookieUtil.setSaksbehandlersValgteEnhet(response, enhetId);
        return enhetId;
    }
}
