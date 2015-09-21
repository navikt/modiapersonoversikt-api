package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Varsel;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.VarslerService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/varsler/{fnr}")
@Produces(APPLICATION_JSON)
public class VarslerController {

    @Inject
    VarslerService varslerService;

    @GET
    @Path("/")
    public List<Varsel> hentAlleVarsler(@PathParam("fnr") String fnr) {
        return varslerService.hentAlleVarsler(fnr);
    }
}
