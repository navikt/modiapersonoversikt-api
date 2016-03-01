package no.nav.sbl.dialogarena.sak.rest;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.service.interfaces.TilgangskontrollService;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.ModiaSakstema;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SaksService;
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
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet.ENHET_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.RestUtils.hentValgtEnhet;
import static org.slf4j.LoggerFactory.getLogger;

@Path("/saksoversikt/{fnr}")
@Produces("application/json")
public class SaksoversiktController { @Inject
private AnsattService ansattService;

    @Inject
    private SaksoversiktService saksoversiktService;

    @Inject
    private SaksService saksService;

    @Inject
    private TilgangskontrollService tilgangskontrollService;

    private static final Logger logger = getLogger(SaksoversiktController.class);

    @GET
    @Path("/temaer")
    public List<Tema> hentTemaer(@PathParam("fnr") String fnr) {
        return saksoversiktService.hentTemaer(fnr);
    }

    @GET
    @Path("/sakstema")
    public Response hentSakstema(@PathParam("fnr") String fnr, @Context HttpServletRequest request) {
        List<Sakstema> sakstemaliste = saksService.hentSakstema(saksService.hentAlleSaker(fnr), fnr, false)
                .collect(toList());

        String valgtEnhet = hentValgtEnhet(request);
        if (on(ansattService.hentEnhetsliste()).map(ENHET_ID).collect().contains(valgtEnhet)) {
            List<ModiaSakstema> tilgangskontrollertSakstemaListe = tilgangskontrollService.harSaksbehandlerTilgangTilSakstema(sakstemaliste, valgtEnhet);
            return Response.ok(tilgangskontrollertSakstemaListe).build();
        } else {
            logger.warn("{} har ikke tilgang til enhet {}.", getSubjectHandler().getUid(), valgtEnhet);
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

    }

}
