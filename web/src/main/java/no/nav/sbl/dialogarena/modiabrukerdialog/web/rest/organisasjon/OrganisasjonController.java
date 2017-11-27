package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.organisasjon;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.domain.OrganisasjonEnhetKontaktinformasjon;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.organisasjonenhet.kontaktinformasjon.service.OrganisasjonEnhetKontaktinformasjonService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/organisasjoner/{id}")
@Produces(APPLICATION_JSON)
public class OrganisasjonController {

    private final OrganisasjonEnhetKontaktinformasjonService service;

    @Inject
    public OrganisasjonController(OrganisasjonEnhetKontaktinformasjonService service) {
        this.service = service;
    }

    @GET
    public OrganisasjonEnhetKontaktinformasjon getOrganisasjon(@PathParam("id") String organisasjonsid) {
        return service.hentKontaktinformasjon(organisasjonsid);
    }
}
