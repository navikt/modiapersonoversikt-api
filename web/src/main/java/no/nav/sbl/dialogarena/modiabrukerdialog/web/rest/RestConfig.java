package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.JacksonConfig;
import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {

    public RestConfig() {
        super(JacksonConfig.class, SkrivestotteController.class, MeldingerController.class, JournalforingController.class, VarslerController.class);
    }
}
