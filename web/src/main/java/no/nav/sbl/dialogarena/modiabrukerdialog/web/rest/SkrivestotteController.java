package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex;
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
    private HjelpetekstIndex hjelpetekstIndex;

    @GET
    @Path("/sok")
    public List<Hjelpetekst> hentHjelpetekster(@QueryParam("fritekst") String fritekst, @QueryParam("tags") List<String> tags) {
        return hjelpetekstIndex.sok(fritekst, tags);
    }

    @GET
    @Path("alletags")
    public Set<String> hentAlleTags() {
        return on(hjelpetekstIndex.sok("")).flatmap(new Transformer<Hjelpetekst, List<String>>() {
            @Override
            public List<String> transform(Hjelpetekst hjelpetekst) {
                return hjelpetekst.tags;
            }
        }).collectIn(new HashSet<String>());
    }
}
