package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.HovedYtelse;
import no.nav.sbl.dialogarena.utbetaling.domain.oppsummering.UnderYtelse;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class OppsummeringPanel extends Panel {

    public OppsummeringPanel(String id, IModel<OppsummeringProperties> model, boolean visDetaljer) {
        super(id, model);

        add(
                new Label("oppsummertPeriode"),
                new Label("oppsummering.utbetalt"),
                new Label("oppsummering.trekk"),
                new Label("oppsummering.brutto"),
                createYtelsesOppsummering(visDetaljer)
        );
    }

    private MarkupContainer createYtelsesOppsummering(boolean visDetaljer) {
        ListView<HovedYtelse> listView = new ListView<HovedYtelse>("oppsummering.hovedYtelsesBeskrivelser") {
            @Override
            protected void populateItem(ListItem<HovedYtelse> item) {
                item.add(
                        new Label("hovedYtelsesBeskrivelse", item.getModelObject().getHovedYtelsesBeskrivelse()),
                        new Label("bruttoUnderytelser", item.getModelObject().getBruttoUnderytelser()),
                        new Label("trekkUnderytelser", item.getModelObject().getTrekkUnderytelser()),
                        new Label("nettoUnderytelser", item.getModelObject().getNettoUnderytelser()),
                        lagUnderBeskrivelseListView(item)
                );
            }

            private ListView<UnderYtelse> lagUnderBeskrivelseListView(final ListItem<HovedYtelse> item) {
                return new ListView<UnderYtelse>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                                @Override
                                protected void populateItem(ListItem<UnderYtelse> item) {
                                    item.add(
                                            new Label("underYtelsesBeskrivelse", item.getModelObject().getUnderYtelsesBeskrivelse()),
                                            new Label("ytelsesBelop", item.getModelObject().getYtelsesBelop()),
                                            new Label("trekkBelop", item.getModelObject().getTrekkBelop())
                                    );
                                }
                            };
            }
        };

        WebMarkupContainer detalj = new WebMarkupContainer("oppsummeringDetalj");
        detalj.add(listView)
              .setOutputMarkupPlaceholderTag(true)
              .setVisibilityAllowed(visDetaljer);

        return detalj;
    }

}
