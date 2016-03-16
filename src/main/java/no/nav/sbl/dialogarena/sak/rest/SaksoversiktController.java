package no.nav.sbl.dialogarena.sak.rest;

import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.domain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
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
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;

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

        ResultatWrapper<List<Sak>> sakerWrapper = saksService.hentAlleSaker(fnr);
        ResultatWrapper<List<Sakstema>> sakstemaWrapper = saksService
                .hentSakstema(sakerWrapper.resultat, fnr, false);

        tilgangskontrollService.markerIkkeJournalforte(sakstemaWrapper.resultat);
        saksoversiktService.fjernGamleDokumenter(sakstemaWrapper.resultat);

        return ok(new ResultatWrapper(mapTilModiaSakstema(sakstemaWrapper.resultat,
                hentValgtEnhet(request)),
                collectFeilendeSystemer(sakerWrapper, sakstemaWrapper))).build();

    }

    public List<ModiaSakstema> mapTilModiaSakstema(List<Sakstema> sakstemaList, String valgtEnhet) {
        return sakstemaList.stream()
                .map(sakstema -> createModiaSakstema(sakstema, valgtEnhet))
                .collect(toList());
    }

    private Set<Baksystem> collectFeilendeSystemer(ResultatWrapper<List<Sak>> sakerWrapper, ResultatWrapper<List<Sakstema>> sakstemaWrapper) {
        return concat(sakerWrapper.feilendeSystemer.stream(), sakstemaWrapper.feilendeSystemer.stream()).collect(toSet());
    }

    private ModiaSakstema createModiaSakstema(Sakstema sakstema, String valgtEnhet) {
        return new ModiaSakstema(sakstema)
                .withTilgang(tilgangskontrollService.harEnhetTilgangTilTema(sakstema.temakode, valgtEnhet));
    }
}
