package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.GrunninfoService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

@Path("/hode")
@Produces(APPLICATION_JSON)
public class HodeController {

    @Inject
    private GrunninfoService grunninfoService;

    @Inject
    private AnsattService ansattService;

    class Me {
        public final String ident, navn, fornavn, etternavn;

        public Me(String ident, String fornavn, String etternavn) {
            this.ident = ident;
            this.fornavn = fornavn;
            this.etternavn = etternavn;
            this.navn = fornavn + " " + etternavn;
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
    public Me hentSaksbehandler() {
        String ident = getSubjectHandler().getUid();
        GrunnInfo.SaksbehandlerNavn saksbehandler = grunninfoService.hentSaksbehandlerNavn();
        return new Me(ident, saksbehandler.fornavn, saksbehandler.etternavn);
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
