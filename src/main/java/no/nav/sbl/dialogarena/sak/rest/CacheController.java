package no.nav.sbl.dialogarena.sak.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Path("/cache")
@Produces(APPLICATION_JSON + ";charset=utf-8")
public class CacheController {


    @Context
    private HttpServletRequest request;

    @GET
    @Path("/invalider")
    public Response invaliderCache() {
        request.getSession().invalidate();
        return Response.ok("Cache er invalidert").build();
    }
}
