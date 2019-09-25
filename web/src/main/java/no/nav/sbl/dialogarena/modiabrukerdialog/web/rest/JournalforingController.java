package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak.EnhetIkkeSatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.Feilmelding;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;

@Path("/journalforing/{fnr}")
@Produces(APPLICATION_JSON)
public class JournalforingController {

    public static final String FEILMELDING_UTEN_ENHET = "Det er dessverre ikke mulig å journalføre henvendelsen. Du må velge enhet du jobber på vegne av på nytt. Bekreft enhet med å trykke på \"Velg\"-knappen.";

    private final SakerService sakerService;

    @Inject
    public JournalforingController(SakerService sakerService) {
        this.sakerService = sakerService;
    }

    @GET
    @Path("/saker/sammensatte")
    public List<Sak> hentSammensatteSaker(@PathParam("fnr") String fnr) {
        // TODO tilgangsstyring
        return sakerService.hentSammensatteSaker(fnr);
    }

    @GET
    @Path("/saker/pensjon")
    public List<Sak> hentPensjonSaker(@PathParam("fnr") String fnr) {
        // TODO tilgangsstyring
        return sakerService.hentPensjonSaker(fnr);
    }

    @POST
    @Path("/{traadId}")
    @Consumes(APPLICATION_JSON)
    public Response knyttTilSak(@PathParam("fnr") String fnr, @PathParam("traadId") String traadId, Sak sak, @Context HttpServletRequest request) throws JournalforingFeilet {
        // TODO tilgangsstyring
        String enhet = hentValgtEnhet(request);
        try {
            sakerService.knyttBehandlingskjedeTilSak(fnr, traadId, sak, enhet);
        } catch (EnhetIkkeSatt exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new Feilmelding().withMessage(FEILMELDING_UTEN_ENHET)).build();
        } catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok().build();
    }
}
