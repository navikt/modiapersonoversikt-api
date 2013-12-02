package no.nav.sbl.dialogarena.utbetaling.lamell;

import no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling;
import no.nav.sbl.dialogarena.utbetaling.lamell.filter.OppsummeringProperties;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;


public class MaanedsPanel extends Panel {

    public MaanedsPanel(String id, IModel<OppsummeringProperties> oppsummeringsModel) {
        super(id, oppsummeringsModel);

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

}
