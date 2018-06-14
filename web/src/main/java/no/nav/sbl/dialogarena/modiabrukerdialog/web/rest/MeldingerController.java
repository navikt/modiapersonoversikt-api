package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.IkkeIndeksertException;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.ENHET_ID;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/meldinger/{fnr}")
@Produces(APPLICATION_JSON)
public class MeldingerController {

    @Inject
    private AnsattService ansattService;
    @Inject
    private HenvendelseBehandlingService henvendelse;
    @Inject
    private MeldingerSok searcher;

    private static final Logger logger = getLogger(MeldingerController.class);

    @GET
    @Path("/traader")
    public Response hentTraader(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        indekser(fnr, request);
        try {
            return Response.ok(searcher.sok(fnr, "")).build();
        } catch (IkkeIndeksertException e) {
            return Response.status(FORBIDDEN).type(TEXT_PLAIN).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/sok/{fritekst: .*}")
    public Response sok(@PathParam("fnr") String fnr, @PathParam("fritekst") String fritekst) {
        try {
            return Response.ok(searcher.sok(fnr, fritekst)).build();
        } catch (IkkeIndeksertException e) {
            return Response.status(FORBIDDEN).type(TEXT_PLAIN).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/indekser")
    public Response indekser(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        String valgtEnhet = hentValgtEnhet(request);
        if(ansattService.hentEnhetsliste().stream().map(ENHET_ID).collect(toList()).contains(valgtEnhet)) {
            List<Melding> meldinger = hentAlleMeldinger(fnr, valgtEnhet);
            searcher.indekser(fnr, meldinger);
            return Response.status(Response.Status.OK).build();
        } else {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    private List<Melding> hentAlleMeldinger(@PathParam("fnr") String fnr, String valgtEnhet) {
        return henvendelse.hentMeldinger(fnr, valgtEnhet).getTraader().stream()
                .flatMap(traad -> traad.getMeldinger().stream())
                .collect(toList());
    }

}
