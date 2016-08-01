package no.nav.sbl.dialogarena.utbetaling.lamell.SammenlagtUtbetaling;

import no.nav.modig.modia.widget.utils.WidgetDateFormatter;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.domain.SammenlagtUtbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.List;
import java.util.stream.Collector;

import static java.util.stream.Collectors.summingDouble;

public class SammenlagtUtbetalingPanel extends Panel {

    private static Collector<Double, ?, Double> sumDouble = summingDouble((d) -> d);

    public SammenlagtUtbetalingPanel(String id, SammenlagtUtbetaling sammenlagtUtbetaling) {
        super(id);
        List<Hovedytelse> synligeHovedytelser = sammenlagtUtbetaling.getSynligeHovedytelser();
        add(createUtbetalingListView(synligeHovedytelser));
        add(new Label("utbetalingsdato", WidgetDateFormatter.date(sammenlagtUtbetaling.getUtbetalingsdato())).setVisible(sammenlagtUtbetaling.skalViseSammenlagtUtbetaling()));
        add(new Label("sammenlagtUtbetalingSum", finnSumAvHovedytelser(synligeHovedytelser)).setVisible(sammenlagtUtbetaling.skalViseSammenlagtUtbetaling()));
    }

    private Double finnSumAvHovedytelser(List<Hovedytelse> hovedytelser) {
        return hovedytelser.stream()
                .map(Hovedytelse::getNettoUtbetalt)
                .collect(sumDouble);
    }

    private ListView<Hovedytelse> createUtbetalingListView(List<Hovedytelse> utbetalingsliste) {
        return new ListView<Hovedytelse>("hovedytelser", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Hovedytelse> item) {
                item.add(new UtbetalingPanel("ytelseUtbetaling", new UtbetalingVM(item.getModelObject())));
            }
        };
    }

}

