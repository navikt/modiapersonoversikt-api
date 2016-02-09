package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Map;


@Path("/sak/{fnr}")
@Produces("application/json")
public class SaksoversiktController {

    @Inject
    SaksoversiktService saksoversiktService;

    @GET
    @Path("/")
    public Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(@PathParam("fnr") String fnr) {
        return saksoversiktService.hentBehandlingerByTema(fnr);
    }
}
