package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.TestSecurityBaseClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ActiveProfiles({
        "kjerneinfoDefault",
        "sykemeldingsperioderDefault",
        "kontrakterDefault",
        "personsokDefault",
        "brukerprofilDefault",
        "behandleBrukerprofilDefault",
        "brukerhenvendelserDefault",
        "oppgavebehandlingDefault",
        "henvendelseinnsynDefault",
        "soknaderTest"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContext.class)
public class ApplicationContextTest extends TestSecurityBaseClass {

    @Test
    public void shouldSetupAppContext() { }

}
