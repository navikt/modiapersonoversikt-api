package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import kotlin.Pair;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.naudit.AuditResources.Person;
import no.nav.sbl.dialogarena.naudit.Audit;
import no.nav.sbl.dialogarena.varsel.domain.Varsel;
import no.nav.sbl.dialogarena.varsel.service.VarslerService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@Path("/varsler/{fnr}")
@Produces("application/json")
public class VarslerController {

    @Inject
    VarslerService varslerService;

    @Inject
    @Named("varsling-cms-integrasjon")
    private ContentRetriever contentRetriever;

    @Inject
    Tilgangskontroll tilgangskontroll;

    @GET
    @Path("/")
    public List<Varsel> hentAlleVarsler(@PathParam("fnr") String fnr) {
        return tilgangskontroll
                .check(Policies.tilgangTilBruker.with(fnr))
                .get(Audit.describe(Audit.Action.READ, Person.Varsler, new Pair<>("fnr", fnr)), () -> varslerService
                        .hentAlleVarsler(fnr)
                        .orElse(emptyList())
                );
    }

    @GET
    @Path("/resources")
    public Map<String, String> hentAlleResources() {
        return tilgangskontroll
                .check(Policies.tilgangTilModia)
                .get(Audit.skipAuditLog(), () -> contentRetriever.hentAlleTekster());
    }
}
