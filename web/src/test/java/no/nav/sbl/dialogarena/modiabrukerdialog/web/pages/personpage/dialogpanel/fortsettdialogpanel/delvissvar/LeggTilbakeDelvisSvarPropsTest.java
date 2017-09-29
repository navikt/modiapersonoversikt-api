package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar.LeggTilbakeDelvisSvarPanel.SVAR_DELVIS_CALLBACK_ID;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LeggTilbakeDelvisSvarPropsTest {

    private static final String BEHANDLINGS_ID = "BEHANDLINGS_ID";
    private static final String FRITEKST = "FRITEKST";
    private static final String FODSELSNUMMER = "10108000398";
    private static final String TRAAD_ID = "1337";

    @Test
    @DisplayName("Lager korrekte props til reactkomponenten")
    void lagerPropsSomForventet() {
        LeggTilbakeDelvisSvarProps leggTilbakeDelvisSvarProps = new LeggTilbakeDelvisSvarProps(lagMelding(), BEHANDLINGS_ID);

        Map<String, Object> props = leggTilbakeDelvisSvarProps.lagProps();

        assertAll("props",
                () -> assertEquals(BEHANDLINGS_ID, props.get("henvendelseId")),
                () -> assertEquals(FODSELSNUMMER, props.get("fodselsnummer")),
                () -> assertEquals(TRAAD_ID, props.get("traadId")),
                () -> assertEquals(SVAR_DELVIS_CALLBACK_ID, props.get("svarDelvisCallbackId")),
                () -> assertEquals(FRITEKST, props.get("sporsmal")));
    }

    private Melding lagMelding() {
        return new Melding()
                .withFnr(FODSELSNUMMER)
                .withTraadId(TRAAD_ID)
                .withFritekst(FRITEKST);
    }
}