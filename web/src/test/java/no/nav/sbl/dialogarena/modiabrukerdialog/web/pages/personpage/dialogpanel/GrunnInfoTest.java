package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GrunnInfoTest {
    @Test
    public void brukersNavnFaarKorrekteVersaler() {
        GrunnInfo.Bruker bruker = new GrunnInfo.Bruker(
                "123", "test-test", "o'testeson", "kontoret"
        );

        assertThat(bruker.navn, is(equalTo("Test-Test O'Testeson")));
    }

    @Test
    public void saksbehandlersNavnFaarKorrekteVersaler() {
        GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler(
                "123", "go-Sak", "Behandler-son"
        );

        assertThat(saksbehandler.navn, is(equalTo("Go-Sak Behandler-Son")));
    }

    @Test
    public void bareVersalerBlirMinusklerOgVersaler() {
        GrunnInfo.Saksbehandler saksbehandler = new GrunnInfo.Saksbehandler(
                "123", "BARE STORE", "BOKSTAVER"
        );

        assertThat(saksbehandler.navn, is(equalTo("Bare Store Bokstaver")));
    }
}