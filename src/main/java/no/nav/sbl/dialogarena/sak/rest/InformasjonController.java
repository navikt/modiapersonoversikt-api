package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.enonic.EnonicStringHandler;
import no.nav.sbl.dialogarena.sak.service.enonic.MiljovariablerService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Locale;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/informasjon")
@Produces(APPLICATION_JSON + ";charset=utf-8")
public class InformasjonController {

    @Inject
    private MiljovariablerService miljovariablerService;

    @Inject
    private EnonicStringHandler messageSource;

    @GET
    @Path("/tekster")
    public Properties hentTekster(@QueryParam("type") String type, @QueryParam("sprak") String sprak) {
        if (sprak == null || sprak.trim().isEmpty()) {
            sprak = "nb_NO";
        }

        if (!sprak.matches("[a-z][a-z]_[A-Z][A-Z]")) {
            throw new IllegalArgumentException("Språk må matche xx_XX: " + sprak);
        }

        String[] split = sprak.split("_");
        Locale locale = new Locale(split[0], split[1]);

        return messageSource.getBundleFor(type, locale);
    }

    @GET
    @Path("/miljovariabler")
    public Response hentMiljovariabler(){
        return Response.ok(miljovariablerService.hentMiljovariabler()).build();
    }
}
