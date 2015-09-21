package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/varsler")
@Produces(APPLICATION_JSON)
public class VarslerController {

    @Inject
    private VarslerPorttype varslerWs;


}
