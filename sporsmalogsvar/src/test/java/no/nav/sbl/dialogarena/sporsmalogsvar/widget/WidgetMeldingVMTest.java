package no.nav.sbl.dialogarena.sporsmalogsvar.widget;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.MockServiceTestContext;
import no.nav.sbl.dialogarena.sporsmalogsvar.config.ServiceTestContext;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_SKRIFTLIG;
import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = {MockServiceTestContext.class, ServiceTestContext.class})
@ExtendWith(SpringExtension.class)
public class WidgetMeldingVMTest {

    @Test
    public void setterTraadLengdeEtterHvorMangeMeldinger() {
        WidgetMeldingVM widgetMeldingVM = new WidgetMeldingVM(Arrays.asList(mockMelding(), mockMelding().withType(SPORSMAL_SKRIFTLIG)), false);
        assertEquals(2, widgetMeldingVM.traadlengde);
    }

    private Melding mockMelding() {
        return new Melding().withFerdigstiltDato(DateTime.now());
    }

}