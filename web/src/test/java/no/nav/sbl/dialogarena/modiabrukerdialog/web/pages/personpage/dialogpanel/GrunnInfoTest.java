package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GrunnInfo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class GrunnInfoTest {
    @Test
    public void brukersNavnFaarKorrekteVersaler() {
        GrunnInfo.Bruker bruker = new GrunnInfo.Bruker(
                "123", "test-test", "o'testeson", "kontoret", "0314", "", "M"
        );

        assertThat(bruker.navn, is(equalTo("Test-Test O'Testeson")));
    }

    @Test
    public void bareVersalerBlirMinusklerOgVersaler() {
        GrunnInfo.Bruker saksbehandler = new GrunnInfo.Bruker(
                "10108000398", "BARE STORE", "BOKSTAVER", "NAV Aremark", "0118", "", "K"
        );

        assertThat(saksbehandler.navn, is(equalTo("Bare Store Bokstaver")));
    }
}