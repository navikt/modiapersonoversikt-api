package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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

    @GET
    @Path("/traader")
    public List<Traad> hentTraader(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        return henvendelse.hentTraader(fnr, valgtEnhet);
    }

    @GET
    @Path("/indekser")
    public Response indekser(@PathParam("fnr") String fnr) {
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
