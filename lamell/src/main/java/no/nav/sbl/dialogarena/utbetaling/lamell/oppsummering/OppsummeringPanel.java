package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedBeskrivelse;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.UnderBeskrivelse;
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

    private ListView<HovedBeskrivelse> createYtelsesOppsummering() {
        ListView<HovedBeskrivelse> listView = new ListView<HovedBeskrivelse>("oppsummering.hovedYtelsesBeskrivelser") {
            @Override
            protected void populateItem(ListItem<HovedBeskrivelse> item) {
                ListView<UnderBeskrivelse> underBeskrivelseListView = new ListView<UnderBeskrivelse>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                    @Override
                    protected void populateItem(ListItem<UnderBeskrivelse> item) {
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
