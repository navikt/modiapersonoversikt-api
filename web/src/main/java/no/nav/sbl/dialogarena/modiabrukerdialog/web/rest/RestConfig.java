package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.brukerdialog.isso.RelyingPartyCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.JacksonConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.EnhetController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.DelsvarController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave.OppgaveController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController;
import no.nav.sbl.dialogarena.sak.rest.DokumentController;
import no.nav.sbl.dialogarena.sak.rest.InformasjonController;
import no.nav.sbl.dialogarena.sak.rest.SaksoversiktController;
import no.nav.sbl.dialogarena.varsel.rest.VarslerController;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature.PERSON_REST_API;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggleKt.visFeature;

public class RestConfig extends ResourceConfig {

    private static List<String> allowedOrigins = Arrays.asList(
            ".adeo.no",
            ".nais.preprod.local",
            "http://localhost:3000"
    );

    public RestConfig() {
        super(
                JacksonConfig.class,
                InformasjonController.class,
                SkrivestotteController.class,
                MeldingerController.class,
                JournalforingController.class,
                HodeController.class,
                VarslerController.class,
                DokumentController.class,
                SaksoversiktController.class,
                DelsvarController.class,
                RelyingPartyCallback.class,
                OppgaveController.class,
                EnhetController.class,
                PersonController.class
        );
        if (!visFeature(PERSON_REST_API)) {
            return;
        }
        register(new ContainerResponseFilter() {
            @Override
            public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
                String origin = request.getHeaderString("Origin");
                if (origin != null && allowedOrigins.stream().anyMatch(origin::endsWith)) {
                    response.getHeaders().add("Access-Control-Allow-Origin", origin);
                    response.getHeaders().add("Access-Control-Allow-Credentials", "true");
                    response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD");
                }
            }
        });
    }
}
