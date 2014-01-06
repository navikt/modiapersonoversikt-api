package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class TotalOppsummeringPanel extends Panel {

    private IModel<Boolean> skjult = Model.of(true);

    public TotalOppsummeringPanel(String id, OppsummeringVM oppsummeringVM) {
        super(id, new CompoundPropertyModel<>(oppsummeringVM));
        setOutputMarkupId(true);

        add(createShowHideBehavior());
        add(createTopplinje(), createYtelsesOppsummering());
    }

    private AjaxEventBehavior createShowHideBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                skjult.setObject(!skjult.getObject());
                target.appendJavaScript("$('#" + getMarkupId() + " .detaljpanel').animate({height: 'toggle'}, 300);");
            }
        };
    }

    private MarkupContainer createTopplinje() {
        return new WebMarkupContainer("oppsummeringsLinje")
                .add(
                        new Label("oppsummertPeriode"),
                        new Label("utbetalt"),
                        new Label("trekk"),
                        new Label("brutto"),
                       createSkrivUtLink()
                );
    }

    private Component createSkrivUtLink() {
        return  new AjaxLink<Void>("skriv-ut") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript(
                        "var totalOppsummering = $('#" + TotalOppsummeringPanel.this.getMarkupId() + "').clone();" +
                        "totalOppsummering.children('.detaljpanel').css('display', 'block');" +
                        "skrivUt(totalOppsummering.html());");
            }
        };
    }

    private MarkupContainer createYtelsesOppsummering() {
        ListView<HovedYtelseVM> listView = new ListView<HovedYtelseVM>("hovedytelser") {
            @Override
            protected void populateItem(ListItem<HovedYtelseVM> item) {
                item.add(
                        new Label("hovedYtelsesBeskrivelse", item.getModelObject().getHovedYtelsesBeskrivelse()),
                        new Label("bruttoUnderytelser", item.getModelObject().getBruttoUnderytelser()),
                        new Label("trekkUnderytelser", item.getModelObject().getTrekkUnderytelser()),
                        new Label("nettoUnderytelser", item.getModelObject().getNettoUnderytelser()),
                        lagUnderBeskrivelseListView(item)
                );
            }

            private ListView<UnderYtelseVM> lagUnderBeskrivelseListView(final ListItem<HovedYtelseVM> item) {
                return new ListView<UnderYtelseVM>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                    @Override
                    protected void populateItem(ListItem<UnderYtelseVM> item) {
                        item.add(
                                new Label("underYtelsesBeskrivelse", item.getModelObject().getUnderYtelsesBeskrivelse()),
                                new Label("ytelsesBelop", item.getModelObject().getYtelsesBelop()),
                                new Label("trekkBelop", item.getModelObject().getTrekkBelop())
                        );
                    }
                };
            }
        };
        MarkupContainer oppsummeringDetalj = new WebMarkupContainer("oppsummeringDetalj").add(listView);
        oppsummeringDetalj.add(hasCssClassIf("skjult", skjult));
        return oppsummeringDetalj;
    }

}
