package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisRequest.SvarDelvisRequestBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.SvarDelvisService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util.CookieUtil;

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
    @POST
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

        SvarDelvisRequest svarDelvisRequest = new SvarDelvisRequestBuilder()
                .withFodselsnummer(fnr)
                .withTraadId(traadId)
                .withHenvendelseId(henvendelseId)
                .withSvar(request.svar)
                .withNavIdent(getSubjectHandler().getUid())
                .withValgtEnhet(CookieUtil.getSaksbehandlersValgteEnhet(httpRequest))
                .build();

        svarDelvisService.svarDelvis(svarDelvisRequest);

        return Response.ok("{\"message\": \"Success\"}").build();
    }

}
