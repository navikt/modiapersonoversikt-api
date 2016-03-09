package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.stream.Collectors.*;
import static javax.ws.rs.core.Response.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;

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
    public Response hentSakstema(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        boolean harManipulertCookie = !tilgangskontrollService.harGodkjentEnhet(request);
        if (harManipulertCookie) {
            return status(403).build();
        }

        List<Sakstema> sakstemaliste = saksService
                .hentSakstema(saksService.hentAlleSaker(fnr).alleSaker, fnr, false)
                .sakstema;


        sakstemaliste.stream()
                .forEach(sakstema -> sakstema.dokumentMetadata
                        .stream()
                        .filter(dokumentMetadata -> !dokumentMetadata.isErJournalfort())
                        .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(IKKE_JOURNALFORT_ELLER_ANNEN_BRUKER))
                        .collect(toList()));

        return ok(mapTilModiaSakstema(sakstemaliste, hentValgtEnhet(request))).build();
    }


    public List<ModiaSakstema> mapTilModiaSakstema(List<Sakstema> sakstemaList, String valgtEnhet) {
        return sakstemaList.stream()
                .map(sakstema -> createModiaSakstema(sakstema, valgtEnhet))
                .collect(toList());
    }

    private ModiaSakstema createModiaSakstema(Sakstema sakstema, String valgtEnhet) {
        return new ModiaSakstema(sakstema)
                .withTilgang(tilgangskontrollService.harEnhetTilgangTilTema(sakstema.temakode, valgtEnhet));
    }
}
