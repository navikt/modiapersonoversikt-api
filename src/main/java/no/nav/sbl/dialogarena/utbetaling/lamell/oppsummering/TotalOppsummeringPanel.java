package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

import no.nav.sbl.dialogarena.utbetaling.lamell.components.PrintEkspanderContainer;
import org.apache.wicket.MarkupContainer;
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
import static no.nav.sbl.dialogarena.utbetaling.util.VMUtils.erGyldigStartSluttVerdier;

public class TotalOppsummeringPanel extends Panel {

    private IModel<Boolean> ekspandert = Model.of(false);

    public TotalOppsummeringPanel(String id, OppsummeringVM oppsummeringVM) {
        super(id, new CompoundPropertyModel<>(oppsummeringVM));
        setOutputMarkupPlaceholderTag(true);

        add(createTopplinje(), createYtelsesOppsummering());
        add(hasCssClassIf("ekspandert", ekspandert));
    }

    private MarkupContainer createTopplinje() {
        MarkupContainer container = new WebMarkupContainer("oppsummeringsLinje");
        container.add(new Label("oppsummertPeriode"),
                new Label("utbetalt"),
                new Label("trekk"),
                new Label("brutto"));
        container.add(new PrintEkspanderContainer("printEkspander", TotalOppsummeringPanel.this.getMarkupId()));
        return container;
    }

    private MarkupContainer createYtelsesOppsummering() {
        ListView<HovedYtelseVM> listView = new ListView<HovedYtelseVM>("hovedytelser") {
            @Override
            protected void populateItem(final ListItem<HovedYtelseVM> item) {
                HovedYtelseVM hovedYtelseVM = item.getModelObject();

                item.add(new Label("hovedYtelsesBeskrivelse", hovedYtelseVM.getHovedYtelsesBeskrivelse().toLowerCase()),
                        getHovedYtelsesPeriodeLabel(hovedYtelseVM),
                        new Label("bruttoUnderytelser", hovedYtelseVM.getBruttoUnderytelser()),
                        new Label("trekkUnderytelser", hovedYtelseVM.getTrekkUnderytelser()),
                        new Label("nettoUnderytelser", hovedYtelseVM.getNettoUnderytelser()),
                        lagUnderBeskrivelseListView(item));
            }

            private ListView<UnderYtelseVM> lagUnderBeskrivelseListView(final ListItem<HovedYtelseVM> item) {
                return new ListView<UnderYtelseVM>("underYtelsesBeskrivelser", item.getModelObject().getUnderYtelsesBeskrivelser()) {
                    @Override
                    protected void populateItem(ListItem<UnderYtelseVM> item) {
                        item.add(new Label("underYtelsesBeskrivelse", item.getModelObject().getUnderYtelsesBeskrivelse()),
                                new Label("ytelsesBelop", item.getModelObject().getYtelsesBelop()),
                                new Label("trekkBelop", item.getModelObject().getTrekkBelop()));
                    }
                };
            }
        };
        return new WebMarkupContainer("oppsummeringDetalj").add(listView);
    }

    private Label getHovedYtelsesPeriodeLabel(HovedYtelseVM hovedYtelseVM) {
        if (erGyldigStartSluttVerdier(hovedYtelseVM.getStartPeriode(), hovedYtelseVM.getSluttPeriode())) {
            return new Label("hovedYtelsesPeriode", hovedYtelseVM.getHovedYtelsePeriode());
        }
        return (Label) new Label("hovedYtelsesPeriode",
                new StringResourceModel("utbetaling.lamell.total.oppsummering.udefinertperiode", TotalOppsummeringPanel.this, null).getString())
                .add(new AttributeAppender("class", "kursiv").setSeparator(" "));
    }
}
