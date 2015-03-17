package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.DefaultSaksbehandlerInnstillingerService.saksbehandlerInnstillingerCookieId;

@Controller
@Path("/meldinger/{fnr}")
@Produces(APPLICATION_JSON)
public class MeldingerController {

    @Inject
    private HenvendelseBehandlingService henvendelse;
    @Inject
    private MeldingerSok searcher;

    @GET
    @Path("/traader")
    public List<Traad> hentTraader(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        indekser(fnr, request);
        return searcher.sok(fnr, "");
    }

    @GET
    @Path("/sok/{fritekst: .*}")
    public List<Traad> sok(@PathParam("fnr") String fnr, @PathParam("fritekst") String fritekst) {
        return searcher.sok(fnr, fritekst);
    }

    @GET
    @Path("/indekser")
    public Response indekser(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        List<Melding> meldinger = henvendelse.hentMeldinger(fnr, valgtEnhet);
        searcher.indekser(fnr, meldinger);
        return Response.status(Response.Status.OK).build();
    }

    private static String hentValgtEnhet(HttpServletRequest request) {
        String key = saksbehandlerInnstillingerCookieId();
        for (Cookie cookie : request.getCookies()) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException(String.format("Finner ikke cookie med key %s på session", key));
    }
}
