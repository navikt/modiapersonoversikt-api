package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteSok;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.cms.SkrivestotteTekst;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/skrivestotte")
@Produces(APPLICATION_JSON)
public class SkrivestotteController {

    @Inject
    private SkrivestotteSok skrivestotteSok;

    @Inject
    private OrganisasjonEnhetV2Service organisasjonEnhetService;

    @GET
    @Path("/sok")
    public List<SkrivestotteTekst> hentSkrivestotteTekster(@QueryParam("fritekst") String fritekst, @QueryParam("tags") List<String> tags) {
        return skrivestotteSok.sok(fritekst, tags);
    }

    @GET
    @Path("alletags")
    public Set<String> hentAlleTags() {
        return skrivestotteSok.sok("").stream()
                .flatMap(skrivestotteTekst -> skrivestotteTekst.tags.stream())
                .collect(toSet());
    }

    @GET
    @Path("/enhetsnavn")
    public String hentEnhetsnavn(@QueryParam("enhetId") String enhetId) {
        return organisasjonEnhetService.hentEnhetGittEnhetId(enhetId, OrganisasjonEnhetV2Service.WSOppgavebehandlerfilter.UFILTRERT)
                .map((enhet) -> enhet.enhetNavn)
                .orElse("[Ukjent enhetId: "+ enhetId+"]");
    }
}
