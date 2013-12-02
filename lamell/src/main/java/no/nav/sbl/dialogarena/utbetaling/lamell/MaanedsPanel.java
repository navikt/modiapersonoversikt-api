package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;


public class MaanedsPanel extends Panel {

    public MaanedsPanel(String maanedsPanel, List<Utbetaling> utbetalingListe) {
        super(maanedsPanel);

        CompoundPropertyModel<OppsummeringProperties> oppsummeringsModel = createOppsummeringPropertiesModel(utbetalingListe);

        OppsummeringPanel oppsummeringsPanel = new OppsummeringPanel("oppsummeringsPanel", oppsummeringsModel);
        oppsummeringsPanel.add(visibleIf(new Model<>(oppsummeringsModel.getObject().getUtbetalinger().size() > 1)));

        add(
                oppsummeringsPanel,
                createUtbetalingListView(oppsummeringsModel)
        );

    }

    private ListView<Utbetaling> createUtbetalingListView(IModel<OppsummeringProperties> oppsummeringsModel) {
        return new ListView<Utbetaling>("utbetalinger", oppsummeringsModel.getObject().getUtbetalinger()) {
            @Override
            protected void populateItem(ListItem<Utbetaling> item) {
                item.add(new UtbetalingPanel("utbetaling", item.getModelObject()));
            }
        };
    }

    private CompoundPropertyModel<OppsummeringProperties> createOppsummeringPropertiesModel(List<Utbetaling> liste) {
        if (liste.isEmpty()) {
            return new CompoundPropertyModel<>(new OppsummeringProperties(new ArrayList<Utbetaling>(), LocalDate.now(), LocalDate.now()));
        }
        LocalDate startDato = liste.get(liste.size() - 1).getUtbetalingsDato().dayOfMonth().withMinimumValue().toLocalDate();
        LocalDate sluttDato = liste.get(0).getUtbetalingsDato().dayOfMonth().withMaximumValue().toLocalDate();
        return new CompoundPropertyModel<>(new OppsummeringProperties(liste, startDato, sluttDato));
    }

}
