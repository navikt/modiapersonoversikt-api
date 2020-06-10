package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.rest.InformasjonController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.baseurls.BaseUrlsController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.DialogController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.DialogMerkController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.dialog.DialogOppgaveController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.egenansatt.EgenAnsattController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.enhet.EnhetController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.featuretoggle.FeatureToggleController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.henvendelse.DelsvarController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kodeverk.KodeverkController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.kontaktinformasjon.KontaktinformasjonController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ldap.LdapController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppfolging.OppfolgingController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.oppgave.OppgaveController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.person.PersonsokController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.saker.SakerController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.utbetaling.UtbetalingController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.vergemal.VergemalController;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.ytelse.YtelseController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        InformasjonController.class,
        MeldingerController.class,
        JournalforingController.class,
        HodeController.class,
        VarslerController.class,
        DelsvarController.class,
        OppgaveController.class,
        EnhetController.class,
        PersonController.class,
        VergemalController.class,
        KontaktinformasjonController.class,
        EgenAnsattController.class,
        KodeverkController.class,
        LdapController.class,
        BaseUrlsController.class,
        FeatureToggleController.class,
        UtbetalingController.class,
        YtelseController.class,
        OppfolgingController.class,
        SakerController.class,
        NaisController.class,
        DialogController.class,
        PersonsokController.class,
        TilgangController.class,
        DialogMerkController.class,
        DialogOppgaveController.class
})
public class RestApiBeans {
}
