package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.brukerdialog.isso.RelyingPartyCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.JacksonConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave.OppgaveController;
import no.nav.sbl.dialogarena.sak.rest.DokumentController;
import no.nav.sbl.dialogarena.sak.rest.InformasjonController;
import no.nav.sbl.dialogarena.sak.rest.SaksoversiktController;
import no.nav.sbl.dialogarena.varsel.rest.VarslerController;
import org.glassfish.jersey.server.ResourceConfig;

public class RestConfig extends ResourceConfig {

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
                RelyingPartyCallback.class,
                HenvendelseController.class,
                OppgaveController.class
        );
    }
}
