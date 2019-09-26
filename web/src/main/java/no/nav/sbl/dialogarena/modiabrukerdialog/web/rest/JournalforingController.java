package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak.EnhetIkkeSatt;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.api.Feilmelding;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.BehandlingsIdTilgangData;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Tilgangskontroll;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.behandlingsIderTilhorerFnrPolicy;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.tilgangskontroll.Policies.tilgangTilBrukerPolicy;

@Path("/journalforing/{fnr}")
@Produces(APPLICATION_JSON)
public class JournalforingController {

    public static final String FEILMELDING_UTEN_ENHET = "Det er dessverre ikke mulig å journalføre henvendelsen. Du må velge enhet du jobber på vegne av på nytt. Bekreft enhet med å trykke på \"Velg\"-knappen.";

    private final SakerService sakerService;
    private final Tilgangskontroll tilgangskontroll;

    @Inject
    public JournalforingController(SakerService sakerService, Tilgangskontroll tilgangskontroll) {
        this.sakerService = sakerService;
        this.tilgangskontroll = tilgangskontroll;
    }

    @GET
    @Path("/saker/sammensatte")
    public List<Sak> hentSammensatteSaker(@PathParam("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBrukerPolicy.with(fnr))
                .get(() -> sakerService.hentSammensatteSaker(fnr));
    }

    @GET
    @Path("/saker/pensjon")
    public List<Sak> hentPensjonSaker(@PathParam("fnr") String fnr) {
        return tilgangskontroll
                .check(tilgangTilBrukerPolicy.with(fnr))
                .get(() -> sakerService.hentPensjonSaker(fnr));
    }

    @POST
    @Path("/{traadId}")
    @Consumes(APPLICATION_JSON)
    public Response knyttTilSak(@PathParam("fnr") String fnr, @PathParam("traadId") String traadId, Sak sak, @Context HttpServletRequest request) throws JournalforingFeilet {
        return tilgangskontroll
                .check(tilgangTilBrukerPolicy.with(fnr))
                .check(behandlingsIderTilhorerFnrPolicy.with(new BehandlingsIdTilgangData(fnr, asList(traadId))))
                .get(() -> {
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
                });
    }
}
