package no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.maaned;

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

    public MaanedsPanel(String id, List<Hovedytelse> utbetalingsliste) {
        super(id);
        add(createOppsummeringsPanel(utbetalingsliste),
            createUtbetalingListView(utbetalingsliste));
    }

    private MaanedOppsummeringPanel createOppsummeringsPanel(List<Hovedytelse> utbetalingsliste) {
        return new MaanedOppsummeringPanel("oppsummeringsPanel",
                createOppsummeringVM(utbetalingsliste));
    }

    private ListView<Hovedytelse> createUtbetalingListView(List<Hovedytelse> utbetalingsliste) {
        return new ListView<Hovedytelse>("utbetalinger", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Hovedytelse> item) {
                item.add(new UtbetalingPanel("utbetaling", new UtbetalingVM(item.getModelObject())));
            }
        };
    }

    private OppsummeringVM createOppsummeringVM(List<Hovedytelse> liste) {
        if (liste.isEmpty()) {
            return new OppsummeringVM(new ArrayList<>(), now(), now());
        }

        LocalDate startDato = getStartDato(liste);
        LocalDate sluttDato = getSluttDato(liste);

        return new OppsummeringVM(liste, startDato, sluttDato);
    }

    protected LocalDate getSluttDato(List<Hovedytelse> liste) {
        return liste.get(0).getHovedytelsedato().dayOfMonth().withMaximumValue().toLocalDate();
    }

    protected LocalDate getStartDato(List<Hovedytelse> liste) {
        return liste.get(liste.size() - 1).getHovedytelsedato().dayOfMonth().withMinimumValue().toLocalDate();
    }

}
