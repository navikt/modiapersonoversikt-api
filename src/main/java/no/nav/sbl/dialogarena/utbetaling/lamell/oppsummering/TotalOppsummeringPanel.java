package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.erDefinertPeriode;

public class TotalOppsummeringPanel extends Panel {

    private IModel<Boolean> ekspandert = Model.of(false);

    public TotalOppsummeringPanel(String id, OppsummeringVM oppsummeringVM) {
        super(id, new CompoundPropertyModel<>(oppsummeringVM));
        setOutputMarkupPlaceholderTag(true);

        add(createShowHideBehavior());
        add(createTopplinje(), createYtelsesOppsummering());
        add(hasCssClassIf("ekspandert", ekspandert));
    }

    private AjaxEventBehavior createShowHideBehavior() {
        return new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                ekspandert.setObject(!ekspandert.getObject());
                target.appendJavaScript(
                        "$('#" + getMarkupId() + " .detaljpanel').animate({height: 'toggle'}, 300);" +
                                "$('#" + getMarkupId() + "').toggleClass('ekspandert');");
                target.add();
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
                target.appendJavaScript("Utbetalinger.skrivUt($('#" + TotalOppsummeringPanel.this.getMarkupId() + "'));");
            }
        };
    }

    private MarkupContainer createYtelsesOppsummering() {
        ListView<HovedYtelseVM> listView = new ListView<HovedYtelseVM>("hovedytelser") {
            @Override
            protected void populateItem(final ListItem<HovedYtelseVM> item) {

                HovedYtelseVM hovedYtelseVM = item.getModelObject();
                item.add(
                        new Label("hovedYtelsesBeskrivelse", hovedYtelseVM.getHovedYtelsesBeskrivelse()),
                        getHovedYtelsesPeriodeLabel(hovedYtelseVM),
                        new Label("bruttoUnderytelser", hovedYtelseVM.getBruttoUnderytelser()),
                        new Label("trekkUnderytelser", hovedYtelseVM.getTrekkUnderytelser()),
                        new Label("nettoUnderytelser", hovedYtelseVM.getNettoUnderytelser()),
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
        return new WebMarkupContainer("oppsummeringDetalj").add(listView);
    }

    private Label getHovedYtelsesPeriodeLabel(HovedYtelseVM hovedYtelseVM) {
        if (erDefinertPeriode(hovedYtelseVM.getStartPeriode(), hovedYtelseVM.getSluttPeriode())) {
            return new Label("hovedYtelsesPeriode", hovedYtelseVM.getHovedYtelsePeriode());
        }
        return (Label) new Label("hovedYtelsesPeriode",
                new StringResourceModel("utbetaling.lamell.total.oppsummering.udefinertperiode", TotalOppsummeringPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
    }
}
