package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.DELVISE_SVAR;

@Path("/oppgaver/{id}")
@Produces(APPLICATION_JSON)
public class OppgaveController {

    private static Logger logger = LoggerFactory.getLogger(OppgaveController.class);

    private final OppgaveBehandlingService oppgaveBehandlingService;

    @Inject
    public OppgaveController(OppgaveBehandlingService oppgaveBehandlingService) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
    }

    @POST
    @Path("/leggTilbake")
    @Consumes(APPLICATION_JSON)
    public Response leggTilbake(@PathParam("id") String oppgaveId, @Context HttpServletRequest httpRequest, LeggTilbakeRESTRequest request) {
        if (!FeatureToggle.visFeature(DELVISE_SVAR)) {
            return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
        }

        LeggTilbakeOppgaveIGsakRequest leggTilbakeOppgaveIGsakRequest = new LeggTilbakeOppgaveIGsakRequest()
                .withOppgaveId(oppgaveId)
                .withBeskrivelse(request.beskrivelse)
                .withTemagruppe(getTemagruppefraRequest(request.temagruppe))
                .withSaksbehandlersValgteEnhet(CookieUtil.getSaksbehandlersValgteEnhet(httpRequest));

        try {
            oppgaveBehandlingService.leggTilbakeOppgaveIGsak(leggTilbakeOppgaveIGsakRequest);
        } catch (RuntimeException exception) {
            throw handterRuntimeFeil(exception);
        }
        createEvent("hendelse.oppgavecontroller.leggtilbake.fullfort").report();
        return Response.ok("{\"message\": \"Success\"}").build();
    }

    private Temagruppe getTemagruppefraRequest(String temagruppe){
        try {
            return Temagruppe.valueOf(temagruppe);
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException("Ugyldig temagruppe: " + temagruppe);
        }
    }

    private RuntimeException handterRuntimeFeil(RuntimeException exception) {
        logger.error("Feil ved legging av oppgave tilbake til GSAK", exception);
        createEvent("hendelse.oppgavecontroller.leggtilbake.runtime-exception").report();
        return exception;
    }

}