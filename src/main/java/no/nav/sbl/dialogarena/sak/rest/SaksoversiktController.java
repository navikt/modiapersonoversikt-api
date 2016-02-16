package no.nav.sbl.dialogarena.sak.rest;


import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class SaksoversiktController {

    @Inject
    SaksoversiktService saksoversiktService;

    @Inject
    SaksService saksService;

    @GET
    @Path("/behandlinger-by-tema")
    public Map<TemaVM, List<GenerellBehandling>> hentBehandlingerByTema(@PathParam("fnr") String fnr) {
        return saksoversiktService.hentBehandlingerByTema(fnr);
    }

    @GET
    @Path("/temaer")
    public List<TemaVM> hentTemaer(@PathParam("fnr") String fnr) {
        return saksoversiktService.hentTemaer(fnr);
    }

    @GET
    @Path("/journalposter")
    public List<Journalpost> hentJournalpostListe(@PathParam("fnr") String fnr) {
        return saksService.hentJournalpostListe(fnr).get().collect(Collectors.toList());
    }

    @GET
    @Path("/sakstema")
    public Response hentSakstema(@PathParam("fnr") String fnr) {
        return Response.ok(
                saksService.hentSakstema(saksService.hentAlleSaker(fnr), fnr)
                        .collect(toList())).build();
    }

}
