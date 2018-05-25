package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest;

import no.nav.brukerdialog.isso.RelyingPartyCallback;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.config.JacksonConfig;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.brukerprofil.BrukerprofilController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt.EgenAnsattController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.EnhetController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.DelsvarController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon.KontaktinformasjonController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave.OppgaveController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal.VergemalController;
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
                DelsvarController.class,
                RelyingPartyCallback.class,
                OppgaveController.class,
                EnhetController.class,
                PersonController.class,
                VergemalController.class,
                KontaktinformasjonController.class,
                BrukerprofilController.class,
                EgenAnsattController.class
        );
    }
}
