package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.delvissvar;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.joda.time.DateTime;
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
    private static final String OPPGAVE_ID = "OPPGAVE_ID";

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
                () -> assertEquals(OPPGAVE_ID, props.get("oppgaveId")),
                () -> assertEquals(Temagruppe.ARBD.name(), props.get("temagruppe")),
                () -> assertEquals(FRITEKST, props.get("sporsmal")));
    }

    private Melding lagMelding() {
        return new Melding()
                .withOppgaveId(OPPGAVE_ID)
                .withFnr(FODSELSNUMMER)
                .withTraadId(TRAAD_ID)
                .withFritekst(new Fritekst(FRITEKST, new Saksbehandler("Jan", "Saksbehandler", "ident"), DateTime.now()))
                .withTemagruppe(Temagruppe.ARBD.name());
    }
}