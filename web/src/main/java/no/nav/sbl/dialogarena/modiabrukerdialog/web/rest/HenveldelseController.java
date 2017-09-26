package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.HenvendelseService;
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

@Path("/personer/{fnr}/traader/{traadId}/henvendelser/{id}")
@Produces(APPLICATION_JSON)
public class HenveldelseController {

    private final HenvendelseService henvendelseServiceImpl;

    @Inject
    public HenveldelseController(HenvendelseService henvendelseServiceImpl) {
        this.henvendelseServiceImpl = henvendelseServiceImpl;
    }

    @PUT
    @Path("/ferdigstill")
    public Response ferdigstill(
            @PathParam("fnr") String fnr,
            @PathParam("traadId") String traadId,
            @PathParam("id") String henvendelseId,
            @Context HttpServletRequest request) {

        setWicketRequestCycleForOperasjonerPaaCookies(request);

        henvendelseServiceImpl.ferdigstill(fnr, traadId, henvendelseId, "Innhold");

        return Response.ok("{\"message\": \"Det gikk bra!\"}").build();
    }

    private void setWicketRequestCycleForOperasjonerPaaCookies(@Context HttpServletRequest request) {
        ThreadContext.setRequestCycle(new RequestCycle(new RequestCycleContext(new ServletWebRequest(request, ""),
                new MockWebResponse(), new BufferedResponseMapper(), new DefaultExceptionMapper())));
    }
}
