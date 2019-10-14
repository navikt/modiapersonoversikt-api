package no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.enonic.MiljovariablerService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/informasjon")
@Produces(APPLICATION_JSON + ";charset=utf-8")
public class InformasjonController {

    @Inject
    @Named("saksoversikt-cms-integrasjon")
    private ContentRetriever contentRetriever;

    @Inject
    private MiljovariablerService miljovariablerService;

    @GET
    @Path("/tekster")
    public Map<String, String> hentTekster() {
        return contentRetriever.hentAlleTekster();
    }

    @GET
    @Path("/miljovariabler")
    public Response hentMiljovariabler(){
        return Response.ok(miljovariablerService.hentMiljovariabler()).build();
    }
}
