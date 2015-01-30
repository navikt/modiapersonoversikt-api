package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.utbetaling.domain.Hovedytelse;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.MaanedOppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringVM;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingVM;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static org.joda.time.LocalDate.now;

public class MaanedsPanel extends Panel {

    public MaanedsPanel(String id, List<Record<Hovedytelse>> utbetalingsliste) {
        super(id);
        add(
                createOppsummeringsPanel(utbetalingsliste),
                createUtbetalingListView(utbetalingsliste));
    }

    private MaanedOppsummeringPanel createOppsummeringsPanel(List<Record<Hovedytelse>> utbetalingsliste) {
        return new MaanedOppsummeringPanel("oppsummeringsPanel",
                createOppsummeringVM(utbetalingsliste));
    }

    private ListView<Record<Hovedytelse>> createUtbetalingListView(List<Record<Hovedytelse>> utbetalingsliste) {
        return new ListView<Record<Hovedytelse>>("utbetalinger", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Record<Hovedytelse>> item) {
                item.add(new UtbetalingPanel("utbetaling", new UtbetalingVM(item.getModelObject())));
            }
        };
    }

    private OppsummeringVM createOppsummeringVM(List<Record<Hovedytelse>> liste) {
        if (liste.isEmpty()) {
            return new OppsummeringVM(new ArrayList<Record<Hovedytelse>>(), now(), now());
        }

        LocalDate startDato = liste.get(liste.size() - 1).get(Hovedytelse.utbetalingsDato).dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate sluttDato = liste.get(0).get(Hovedytelse.utbetalingsDato).dayOfMonth().withMaximumValue().toLocalDate();

        return new OppsummeringVM(liste, startDato, sluttDato);
    }

}
