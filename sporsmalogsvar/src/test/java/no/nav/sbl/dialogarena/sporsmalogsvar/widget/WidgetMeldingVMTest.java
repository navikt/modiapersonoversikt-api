package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Arrays;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static org.junit.Assert.assertEquals;


public class WidgetMeldingVMTest {

    @Test
    public void setterTraadLengdeEtterHvorMangeMeldinger() {
        WidgetMeldingVM widgetMeldingVM = new WidgetMeldingVM(Arrays.asList(mockMelding(), mockMelding().withType(SPORSMAL_SKRIFTLIG)), false);
        assertEquals(2, widgetMeldingVM.traadlengde);
    }

    private Melding mockMelding() {
        return new Melding().withOpprettetDato(DateTime.now());
    }

}