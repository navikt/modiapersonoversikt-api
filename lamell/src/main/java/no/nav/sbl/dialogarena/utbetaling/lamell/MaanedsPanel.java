package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


public class MaanedsPanel extends Panel {
    private static final boolean VIS_DETALJER = false;

    public MaanedsPanel(String id, List<Utbetaling> utbetalingsliste, FilterParametere filterParametere) {
        super(id);

        add(createOppsummeringsPanel(utbetalingsliste, filterParametere), createUtbetalingListView(utbetalingsliste));
    }

    private Component createOppsummeringsPanel(List<Utbetaling> utbetalingsliste, FilterParametere filterParametere) {
        return new OppsummeringPanel("oppsummeringsPanel",
                                     createOppsummeringPropertiesModel(utbetalingsliste, filterParametere),
                                     VIS_DETALJER);
    }

    private ListView<Utbetaling> createUtbetalingListView(List<Utbetaling> utbetalingsliste) {
        return new ListView<Utbetaling>("utbetalinger", utbetalingsliste) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
            }
        };
    }

    private CompoundPropertyModel<OppsummeringProperties> createOppsummeringPropertiesModel(List<Utbetaling> liste, FilterParametere filterParametere) {
        if (liste.isEmpty()) {
            return new CompoundPropertyModel<>(new OppsummeringProperties(new ArrayList<Utbetaling>(), LocalDate.now(), LocalDate.now()));
        }

        LocalDate startDato = liste.get(liste.size() - 1).getUtbetalingsDato().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate sluttDato = liste.get(0).getUtbetalingsDato().dayOfMonth().withMaximumValue().toLocalDate();

        if (filterParametere.getStartDato().isAfter(startDato) && filterParametere.getStartDato().isBefore(sluttDato)) {
            startDato = filterParametere.getStartDato();
        }
        if (filterParametere.getSluttDato().isBefore(sluttDato) && filterParametere.getSluttDato().isAfter(startDato)) {
            sluttDato = filterParametere.getSluttDato();
        }

        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, startDato, sluttDato));
    }

}
