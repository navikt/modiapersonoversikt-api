package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.FerdigstillHenvendelseRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.FerdigstillHenvendelseRequest.FerdigstillHenvendelseRequestBuilder;
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
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;

@Path("/personer/{fnr}/traader/{traadId}/henvendelser/{id}")
@Produces(APPLICATION_JSON)
public class HenvendelseController {

    private final HenvendelseService henvendelseService;

    @Inject
    public HenvendelseController(HenvendelseService henvendelseService) {
        this.henvendelseService = henvendelseService;
    }

    @PUT
    @Path("/ferdigstill")
    @Consumes(APPLICATION_JSON)
    public Response ferdigstill(
            @PathParam("fnr") String fnr,
            @PathParam("traadId") String traadId,
            @PathParam("id") String henvendelseId,
            @Context HttpServletRequest httpRequest, FerdigstillHenvendelseRestRequest ferdigstillHenvendelseRestRequest) {

        if (!FeatureToggle.visFeature(Feature.DELVISE_SVAR)) {
            return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
        }

        setWicketRequestCycleForOperasjonerPaaCookies(httpRequest);

        FerdigstillHenvendelseRequest ferdigstillHenvendelseRequest = new FerdigstillHenvendelseRequestBuilder()
                .withFodselsnummer(fnr)
                .withTraadId(traadId)
                .withHenvendelseId(henvendelseId)
                .withSvar(ferdigstillHenvendelseRestRequest.svar)
                .withNavIdent(getSubjectHandler().getUid())
                .build();

        henvendelseService.ferdigstill(ferdigstillHenvendelseRequest);

        return Response.ok("{\"message\": \"Det gikk bra!\"}").build();
    }

    private void setWicketRequestCycleForOperasjonerPaaCookies(@Context HttpServletRequest request) {
        ThreadContext.setRequestCycle(new RequestCycle(new RequestCycleContext(new ServletWebRequest(request, ""),
                new MockWebResponse(), new BufferedResponseMapper(), new DefaultExceptionMapper())));
    }
}
