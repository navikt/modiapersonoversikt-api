package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringPanel;
import no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering.OppsummeringProperties;
import no.nav.sbl.dialogarena.utbetaling.lamell.utbetaling.UtbetalingPanel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;


public class MaanedsPanel extends Panel {

    public MaanedsPanel(String id, List<Utbetaling> utbetalingsliste, FilterParametere filterParametere) {
        super(id);

        add(createOppsummeringsPanel(utbetalingsliste, filterParametere), createUtbetalingListView(utbetalingsliste));
    }

    private OppsummeringPanel createOppsummeringsPanel(List<Utbetaling> utbetalingsliste, FilterParametere filterParametere) {
        CompoundPropertyModel<OppsummeringProperties> oppsummeringsModel = createOppsummeringPropertiesModel(utbetalingsliste, filterParametere);
        OppsummeringPanel oppsummeringsPanel = new OppsummeringPanel("oppsummeringsPanel", oppsummeringsModel);
        oppsummeringsPanel.add(visibleIf(new Model<>(oppsummeringsModel.getObject().getUtbetalinger().size() > 1)));
        return oppsummeringsPanel;
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

        if (filterParametere.getStartDato().isAfter(startDato) && filterParametere.getStartDato().isBefore(sluttDato))
            startDato = filterParametere.getStartDato();
        if (filterParametere.getSluttDato().isBefore(sluttDato) && filterParametere.getSluttDato().isAfter(startDato))
            sluttDato = filterParametere.getSluttDato();

        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, startDato, sluttDato));
    }

}
