package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/journalforing/{fnr}")
@Produces(APPLICATION_JSON)
public class JournalforingController {

    @Inject
    private SakerService sakerService;

    @GET
    @Path("/saker")
    public List<Sak> hentSaker(@PathParam("fnr") String fnr) {
        return sakerService.hentRelevanteSaker(fnr);
    }

    @POST
    @Path("/{traadId}")
    @Consumes(APPLICATION_JSON)
    public void knyttTilSak(@PathParam("fnr") String fnr, @PathParam("traadId") String traadId, Sak sak) throws JournalforingFeilet {
        sakerService.knyttBehandlingskjedeTilSak(fnr, traadId, sak);
    }
}
