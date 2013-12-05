package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.UnderYtelse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class OppsummeringPanel extends Panel {

    public OppsummeringPanel(String id, IModel<OppsummeringProperties> model) {
        super(id, model);

        add(
                new Label("oppsummertPeriode"),
                new Label("oppsummering.utbetalt"),
                new Label("oppsummering.trekk"),
                new Label("oppsummering.brutto"),
                createYtelsesOppsummering()
        );
    }

    private ListView<HovedYtelse> createYtelsesOppsummering() {
        ListView<HovedYtelse> listView = new ListView<HovedYtelse>("oppsummering.hovedYtelsesBeskrivelser") {
            @Override
            protected void populateItem(ListItem<HovedYtelse> item) {
                ListView<UnderYtelse> underBeskrivelseListView = new ListView<UnderYtelse>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                    @Override
                    protected void populateItem(ListItem<UnderYtelse> item) {
                        item.add(
                                new Label("underYtelsesBeskrivelse", item.getModelObject().getUnderYtelsesBeskrivelse()),
                                new Label("ytelsesBelop", item.getModelObject().getYtelsesBelop())
                        );
                    }
                };
                item.add(new Label("hovedYtelsesBeskrivelse", item.getModelObject().getHovedYtelsesBeskrivelse()));
                item.add(underBeskrivelseListView);
            }
        };
        return listView;
    }

}
