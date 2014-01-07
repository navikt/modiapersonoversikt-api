package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.detaljvisning;

import no.nav.sbl.dialogarena.utbetaling.domain.Underytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class DetaljPanel extends Panel {

    public DetaljPanel(String id, UtbetalingVM utbetalingVM) {
        super(id);
        setMarkupId("detaljpanel-" + utbetalingVM.getUtbetalingId());

        add(
                new Label("mottakernavn", utbetalingVM.getMottakerNavn()),
                new Label("konto", utbetalingVM.getKontonr()),
                new Label("ytelsesinfo", utbetalingVM.getBeskrivelse()),
                createYtelsesrader(utbetalingVM),
                new Label("bilagsmelding", utbetalingVM.getMelding())
        );
    }

    private ListView createYtelsesrader(final UtbetalingVM utbetalingVM) {
        return new ListView<Underytelse>("underytelser", utbetalingVM.getUnderytelser()) {
            @Override
            protected void populateItem(ListItem<Underytelse> item) {
                item.add(new UnderytelsePanel("underytelse", item.getModelObject()));
            }
        };
    }

}
