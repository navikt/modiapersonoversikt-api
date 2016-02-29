package no.nav.sbl.dialogarena.sak.rest;


import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class SaksoversiktController {

    @Inject
    private SaksoversiktService saksoversiktService;

    @Inject
    private SaksService saksService;

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    @GET
    @Path("/temaer")
    public List<Tema> hentTemaer(@PathParam("fnr") String fnr) {
        return saksoversiktService.hentTemaer(fnr);
    }

    @GET
    @Path("/sakstema")
    public Response hentSakstema(@PathParam("fnr") String fnr) {
        List<Sakstema> sakstemaliste = saksService.hentSakstema(saksService.hentAlleSaker(fnr), fnr, false)
                .collect(toList());
        List<ModiaSakstema> tilgangskontrollertSakstemaListe = tilgangskontrollService.harSaksbehandlerTilgangTilSakstema(sakstemaliste);
        return Response.ok(tilgangskontrollertSakstemaListe).build();
    }

}
