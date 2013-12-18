package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.LocalDate.now;

public class MaanedsPanel extends Panel {

    private static final boolean VIS_DETALJER = false;

    public MaanedsPanel(String id, List<Utbetaling> utbetalingsliste) {
        super(id);
        add(
                createOppsummeringsPanel(utbetalingsliste),
                createUtbetalingListView(utbetalingsliste));
    }

    private OppsummeringPanel createOppsummeringsPanel(List<Utbetaling> utbetalingsliste) {
        return new OppsummeringPanel("oppsummeringsPanel",
                createOppsummeringVM(utbetalingsliste),
                VIS_DETALJER);
    }

    private ListView<Utbetaling> createUtbetalingListView(List<Utbetaling> utbetalingsliste) {
        return new ListView<Utbetaling>("utbetalinger", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", new UtbetalingVM(item.getModelObject())));
            }
        };
    }

    private OppsummeringVM createOppsummeringVM(List<Utbetaling> liste) {
        if (liste.isEmpty()) {
            return new OppsummeringVM(new ArrayList<Utbetaling>(), now(), now());
        }

        LocalDate startDato = liste.get(liste.size() - 1).getUtbetalingsdato().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate sluttDato = liste.get(0).getUtbetalingsdato().dayOfMonth().withMaximumValue().toLocalDate();

        return new OppsummeringVM(liste, startDato, sluttDato);
    }

}
