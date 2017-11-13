package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import org.apache.wicket.DefaultExceptionMapper;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.core.request.mapper.BufferedResponseMapper;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleContext;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.DELVISE_SVAR;

@Path("/oppgaver/{id}")
@Produces(APPLICATION_JSON)
public class OppgaveController {

    private final OppgaveBehandlingService oppgaveBehandlingService;

    @Inject
    public OppgaveController(OppgaveBehandlingService oppgaveBehandlingService) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
    }

    @PUT
    @Path("/leggTilbake")
    @Consumes(APPLICATION_JSON)
    public Response leggTilbake(@PathParam("id") String oppgaveId, @Context HttpServletRequest httpRequest, LeggTilbakeRESTRequest request) {
        if (!FeatureToggle.visFeature(DELVISE_SVAR)) {
            return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
        }

        setWicketRequestCycleForOperasjonerPaaCookies(httpRequest);

        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(oppgaveId, request.beskrivelse, getTemagruppefraRequest(request.temagruppe));

        return Response.ok("{\"message\": \"Det gikk bra!\"}").build();
    }

    private Temagruppe getTemagruppefraRequest(String temagruppe){
        try {
            return Temagruppe.valueOf(temagruppe);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Ugyldig temagruppe: " + temagruppe);
        }
    }

    private void setWicketRequestCycleForOperasjonerPaaCookies(@Context HttpServletRequest request) {
        ThreadContext.setRequestCycle(new RequestCycle(new RequestCycleContext(new ServletWebRequest(request, ""),
                new MockWebResponse(), new BufferedResponseMapper(), new DefaultExceptionMapper())));

    }

}