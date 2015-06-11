package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteTekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import org.apache.commons.collections15.Transformer;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static no.nav.modig.lang.collections.IterUtils.on;

@Controller
@Path("/skrivestotte")
@Produces(APPLICATION_JSON)
public class SkrivestotteController {

    @Inject
    private SkrivestotteSok skrivestotteSok;

    @GET
    @Path("/sok")
    public List<SkrivestotteTekst> hentSkrivestotteTekster(@QueryParam("fritekst") String fritekst, @QueryParam("tags") List<String> tags) {
        return skrivestotteSok.sok(fritekst, tags);
    }

    @GET
    @Path("alletags")
    public Set<String> hentAlleTags() {
        return on(skrivestotteSok.sok("")).flatmap(new Transformer<SkrivestotteTekst, List<String>>() {
            @Override
            public List<String> transform(SkrivestotteTekst skrivestotteTekst) {
                return skrivestotteTekst.tags;
            }
        }).collectIn(new HashSet<String>());
    }
}
