package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.FerdigstillHenvendelseRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.FerdigstillHenvendelseRequest.FerdigstillHenvendelseRequestBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.HenvendelseService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.FerdigstillHenvendelseRestRequest;
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
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

@Path("/oppgaver/{id}")
@Produces(APPLICATION_JSON)
public class OppgaveController {

    private final OppgaveBehandlingService oppgaveBehandlingService;

    @Inject
    public OppgaveController(OppgaveBehandlingService oppgaveBehandlingService) {
        this.oppgaveBehandlingService = oppgaveBehandlingService;
    }

    @PUT
    @Path("/")
    @Consumes(APPLICATION_JSON)
    public Response put(@PathParam("id") String oppgaveId, @Context HttpServletRequest httpRequest, FerdigstillHenvendelseRestRequest ferdigstillHenvendelseRestRequest) {
        setWicketRequestCycleForOperasjonerPaaCookies(httpRequest);
        oppgaveBehandlingService.leggTilbakeOppgaveIGsak(oppgaveId, "beskrivelse", null);
        return Response.ok("{\"message\": \"Det gikk bra!\"}").build();
    }

    private void setWicketRequestCycleForOperasjonerPaaCookies(@Context HttpServletRequest request) {
        ThreadContext.setRequestCycle(new RequestCycle(new RequestCycleContext(new ServletWebRequest(request, ""),
                new MockWebResponse(), new BufferedResponseMapper(), new DefaultExceptionMapper())));
    }

}
