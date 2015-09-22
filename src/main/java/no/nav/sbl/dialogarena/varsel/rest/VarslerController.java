package no.nav.sbl.dialogarena.varsel.rest;

import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;


@Path("/varsler/{fnr}")
@Produces("application/json")
public class VarslerController {

    @Inject
    VarslerService varslerService;

    @GET
    @Path("/")
    public List<Varsel> hentAlleVarsler(@PathParam("fnr") String fnr) {
        return varslerService.hentAlleVarsler(fnr);
    }
}
