package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.lang.collections.IterUtils.on;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        add(
                new Label("mottakerid", utbetalingVM.getMottakerId()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                new Label("underytelser", utbetalingVM.getUnderytelser().toString()),
                new Label("periode", utbetalingVM.getPeriodeMedKortDato())
        );
    }
}
