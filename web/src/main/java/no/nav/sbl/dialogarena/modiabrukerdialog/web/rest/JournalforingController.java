package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.RestUtils.hentValgtEnhet;

@Path("/journalforing/{fnr}")
@Produces(APPLICATION_JSON)
public class JournalforingController {

    @Inject
    private SakerService sakerService;

    @GET
    @Path("/saker/sammensatte")
    public List<Sak> hentSammensatteSaker(@PathParam("fnr") String fnr) {
        return sakerService.hentSammensatteSaker(fnr);
    }

    @GET
    @Path("/saker/pensjon")
    public List<Sak> hentPensjonSaker(@PathParam("fnr") String fnr) {
        return sakerService.hentPensjonSaker(fnr);
    }

    @POST
    @Path("/{traadId}")
    @Consumes(APPLICATION_JSON)
    public void knyttTilSak(@PathParam("fnr") String fnr, @PathParam("traadId") String traadId, Sak sak, @Context HttpServletRequest request) throws JournalforingFeilet {
        String enhet = hentValgtEnhet(request);
        sakerService.knyttBehandlingskjedeTilSak(fnr, traadId, sak, enhet);
    }
}
