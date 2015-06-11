package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {

    public RestConfig() {
        super(SkrivestotteController.class, MeldingerController.class);
    }
}
