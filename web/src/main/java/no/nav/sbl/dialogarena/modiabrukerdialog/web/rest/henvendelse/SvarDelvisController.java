package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisRequest.SvarDelvisRequestBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisService;
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
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.DELVISE_SVAR;

@Path("/personer/{fnr}/traader/{traadId}/henvendelser/{id}")
@Produces(APPLICATION_JSON)
public class SvarDelvisController {

    private final SvarDelvisService svarDelvisService;

    @Inject
    public SvarDelvisController(SvarDelvisService svarDelvisService) {
        this.svarDelvisService = svarDelvisService;
    }

    @PUT
    @Path("/delvisSvar")
    @Consumes(APPLICATION_JSON)
    public Response svarDelvis(
            @PathParam("fnr") String fnr,
            @PathParam("traadId") String traadId,
            @PathParam("id") String henvendelseId,
            @Context HttpServletRequest httpRequest, SvarDelvisRESTRequest request) {

        if (!FeatureToggle.visFeature(DELVISE_SVAR)) {
            return Response.serverError().status(Response.Status.NOT_IMPLEMENTED).build();
        }

        setWicketRequestCycleForOperasjonerPaaCookies(httpRequest);

        SvarDelvisRequest svarDelvisRequest = new SvarDelvisRequestBuilder()
                .withFodselsnummer(fnr)
                .withTraadId(traadId)
                .withHenvendelseId(henvendelseId)
                .withSvar(request.svar)
                .withNavIdent(getSubjectHandler().getUid())
                .build();

        svarDelvisService.svarDelvis(svarDelvisRequest);

        return Response.ok("{\"message\": \"Success\"}").build();
    }

    private void setWicketRequestCycleForOperasjonerPaaCookies(@Context HttpServletRequest request) {
        ThreadContext.setRequestCycle(new RequestCycle(new RequestCycleContext(new ServletWebRequest(request, ""),
                new MockWebResponse(), new BufferedResponseMapper(), new DefaultExceptionMapper())));
    }
}
