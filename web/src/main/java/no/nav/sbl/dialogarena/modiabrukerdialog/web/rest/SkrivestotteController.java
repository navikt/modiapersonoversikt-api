package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.Hjelpetekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.HjelpetekstIndex;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Controller
@Path("/skrivestotte")
@Produces(APPLICATION_JSON)
public class SkrivestotteController {

    @Inject
    private HjelpetekstIndex hjelpetekstIndex;

    @GET
    public List<Hjelpetekst> hentHjelpetekster(@QueryParam("fritekst") String fritekst, @QueryParam("tags") List<String> tags) {
        return hjelpetekstIndex.sok(fritekst, tags);
    }
}
