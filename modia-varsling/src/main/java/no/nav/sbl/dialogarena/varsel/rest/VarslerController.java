package no.nav.sbl.dialogarena.varsel.rest;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Path("/varsler/{fnr}")
@Produces("application/json")
public class VarslerController {

    @Inject
    VarslerService varslerService;

    @Inject
    @Named("varsling-cms-integrasjon")
    private ContentRetriever contentRetriever;

    @GET
    @Path("/")
    public List<Varsel> hentAlleVarsler(@PathParam("fnr") String fnr) {
        Optional<List<Varsel>> varsler = varslerService.hentAlleVarsler(fnr);
        if (varsler.isPresent()) {
            return varsler.get();
        }
        return Collections.emptyList();
    }

    @GET
    @Path("/resources")
    public Map<String, String> hentAlleResources() {
        return contentRetriever.hentAlleTekster();
    }
}
