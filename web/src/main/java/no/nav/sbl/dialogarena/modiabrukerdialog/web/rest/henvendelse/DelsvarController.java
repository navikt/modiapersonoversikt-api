package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http.CookieUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarRequest.DelsvarRequestBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.henvendelse.DelsvarService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.metrics.MetricsFactory.createEvent;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.tilgangTilBruker;

@Path("/personer/{fnr}/traader/{traadId}/henvendelser/{id}")
@Produces(APPLICATION_JSON)
public class DelsvarController {

    private static Logger logger = LoggerFactory.getLogger(DelsvarController.class);

    private final DelsvarService delsvarService;
    private final Tilgangskontroll tilgangskontroll;

    @Inject
    public DelsvarController(DelsvarService delsvarService, Tilgangskontroll tilgangskontroll) {
        this.delsvarService = delsvarService;
        this.tilgangskontroll = tilgangskontroll;
    }
    @POST
    @Path("/delvisSvar")
    @Consumes(APPLICATION_JSON)
    public Response svarDelvis(
            @PathParam("fnr") String fnr,
            @PathParam("traadId") String traadId,
            @PathParam("id") String henvendelseId,
            @Context HttpServletRequest httpRequest, DelsvarRestRequest request) {
        return tilgangskontroll
                .check(tilgangTilBruker.with(fnr))
                .get(() -> {
                    DelsvarRequest delsvarRequest = new DelsvarRequestBuilder()
                            .withFodselsnummer(fnr)
                            .withTraadId(traadId)
                            .withHenvendelseId(henvendelseId)
                            .withSvar(request.svar)
                            .withNavIdent(getSubjectHandler().getUid())
                            .withValgtEnhet(CookieUtil.getSaksbehandlersValgteEnhet(httpRequest))
                            .build();

                    try {
                        delsvarService.svarDelvis(delsvarRequest);
                    } catch (RuntimeException exception) {
                        throw handterRuntimeFeil(exception);
                    }

                    createEvent("hendelse.svardelviscontroller.svardelvis.fullfort").report();
                    return Response.ok("{\"message\": \"Success\"}").build();
                });
    }

    private RuntimeException handterRuntimeFeil(RuntimeException exception) {
        logger.error("Feil ved opprettelse av delvis svar", exception);
        createEvent("hendelse.svardelviscontroller.svardelvis.runtime-exception").report();
        return exception;
    }

}
