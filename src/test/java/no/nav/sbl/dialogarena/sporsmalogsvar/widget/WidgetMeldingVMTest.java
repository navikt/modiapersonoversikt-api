package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.DELVIS_SVAR_SKRIFTLIG;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static org.junit.Assert.assertEquals;


public class WidgetMeldingVMTest {

    @Test
    public void setterTraadLengdeEtterHvorMangeMeldinger() {
        WidgetMeldingVM widgetMeldingVM = new WidgetMeldingVM(Arrays.asList(mockMelding(), mockMelding().withType(SPORSMAL_SKRIFTLIG)), false);
        assertEquals(2, widgetMeldingVM.traadlengde);
    }

    @Test
    public void setterTraadLengdeUtenDelviseSvar() {
        WidgetMeldingVM widgetMeldingVM = new WidgetMeldingVM(Arrays.asList(mockMelding(), mockMelding().withType(DELVIS_SVAR_SKRIFTLIG)), false);
        assertEquals(1, widgetMeldingVM.traadlengde);
    }

    private Melding mockMelding() {
        return new Melding().withOpprettetDato(DateTime.now());
    }

}