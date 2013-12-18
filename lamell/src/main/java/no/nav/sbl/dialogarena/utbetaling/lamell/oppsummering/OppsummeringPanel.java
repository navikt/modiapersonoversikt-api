package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class OppsummeringPanel extends Panel {

    public OppsummeringPanel(String id, OppsummeringVM oppsummeringVM, boolean visDetaljer) {
        super(id, new CompoundPropertyModel<>(oppsummeringVM));

        MarkupContainer ytelsesDetalj = createYtelsesOppsummering(visDetaljer);

        add(createTopplinje(), ytelsesDetalj);
        add(createClickBehavior(visDetaljer, ytelsesDetalj));
    }

    private MarkupContainer createTopplinje() {
        return new WebMarkupContainer("oppsummeringsLinje")
                .add(
                        new Label("oppsummertPeriode"),
                        new Label("utbetalt"),
                        new Label("trekk"),
                        new Label("brutto")
                );
    }

    private MarkupContainer createYtelsesOppsummering(boolean visDetaljer) {
        ListView<HovedytelseVM> listView = new ListView<HovedytelseVM>("hovedytelser") {
            @Override
            protected void populateItem(ListItem<HovedytelseVM> item) {
                item.add(
                        new Label("hovedYtelsesBeskrivelse", item.getModelObject().getHovedYtelsesBeskrivelse()),
                        new Label("bruttoUnderytelser", item.getModelObject().getBruttoUnderytelser()),
                        new Label("trekkUnderytelser", item.getModelObject().getTrekkUnderytelser()),
                        new Label("nettoUnderytelser", item.getModelObject().getNettoUnderytelser()),
                        lagUnderBeskrivelseListView(item)
                );
            }

            private ListView<UnderytelseVM> lagUnderBeskrivelseListView(final ListItem<HovedytelseVM> item) {
                return new ListView<UnderytelseVM>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                    @Override
                    protected void populateItem(ListItem<UnderytelseVM> item) {
                        item.add(
                                new Label("underYtelsesBeskrivelse", item.getModelObject().getUnderYtelsesBeskrivelse()),
                                new Label("ytelsesBelop", item.getModelObject().getYtelsesBelop()),
                                new Label("trekkBelop", item.getModelObject().getTrekkBelop())
                        );
                    }
                };
            }
        };
        return (MarkupContainer) new WebMarkupContainer("oppsummeringDetalj")
                .add(listView)
                .setOutputMarkupPlaceholderTag(true)
                .setVisibilityAllowed(visDetaljer);
    }

    private AjaxEventBehavior createClickBehavior(final boolean visDetaljer, final MarkupContainer ytelsesDetalj) {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (!visDetaljer) {
                    return;
                }
                ytelsesDetalj.setVisibilityAllowed(!ytelsesDetalj.isVisibleInHierarchy());
                target.add(ytelsesDetalj);
            }
        };
    }
}
