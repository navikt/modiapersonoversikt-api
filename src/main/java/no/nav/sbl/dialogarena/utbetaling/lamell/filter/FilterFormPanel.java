package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.modig.wicket.component.daterangepicker.StrictDateRangePicker;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.domain.Mottakertype;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.YTELSE_FILTER_KLIKKET;
import static org.joda.time.LocalDate.now;

public class FilterFormPanel extends Panel {

    private static final int TRE_AAR_TILBAKE = 3;

    private FilterParametere filterParametere;

    private MarkupContainer ytelsesContainer;
    private FeedbackPanel valideringsfeil;
    private AjaxCheckBox visAlleYtelserCheckbox;

    private IModel<Boolean> visAlleYtelser;

    public FilterFormPanel(String id, final FilterParametere filterParametere) {
        super(id);

        this.filterParametere = filterParametere;
        this.visAlleYtelser = new Model<Boolean>() {
            @Override
            public Boolean getObject() {
                return filterParametere.isAlleYtelserValgt();
            }

            @Override
            public void setObject(Boolean visAlle) {
                filterParametere.toggleAlleYtelser(visAlle);
            }
        };
        this.ytelsesContainer = createYtelser();

        add(createFilterForm());
    }

    private Form createFilterForm() {
        Form filterForm = new AjaxIndicator.SnurrepippFilterForm("filterForm");
        valideringsfeil = new FeedbackPanel("feedbackpanel");
        return (Form) filterForm.add(
                valideringsfeil.setOutputMarkupId(true),
                createDateRangePicker(),
                createSokKnapp(),
                createMottakerButton("visBruker", Mottakertype.BRUKER),
                createMottakerButton("visAnnenMottaker", Mottakertype.ANNEN_MOTTAKER),
                ytelsesContainer)
                .setOutputMarkupId(true);
    }

    private AjaxCheckBox createAlleYtelserCheckbox() {
        return new AjaxCheckBox("visAlleYtelser", visAlleYtelser) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filterParametere.toggleAlleYtelser(this.getModelObject());
                sendFilterEndretEvent();
            }
        };
    }

    private MarkupContainer createYtelser() {
        IModel<List<String>> alleYtelserModel = new AbstractReadOnlyModel<List<String>>() {
            @Override
            public List<String> getObject() {
                ArrayList<String> ytelser = new ArrayList<>(filterParametere.getAlleYtelser());
                sort(ytelser);
                return ytelser;
            }
        };
        ListView<String> listView = new ListView<String>("ytelseFilter", alleYtelserModel) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                boolean erValgt = filterParametere.erYtelseOnsket(item.getModelObject());
                AjaxCheckBox checkbox = new AjaxCheckBox("visYtelse", new Model<>(erValgt)) {
                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        String ytelse = item.getModelObject();
//                        if (visAlleYtelser.getObject()) {
//                            visAlleYtelser.setObject(false);
//                            filterParametere.velgEnYtelse(ytelse);
//                        } else {
                            if (this.getModelObject()) {
                                filterParametere.leggTilOnsketYtelse(ytelse);
                            } else {
                                filterParametere.fjernOnsketYtelse(ytelse);
                            }
//                        }

                        sendYtelsesfilterCheckedEvent();
                    }
                };
                item.add(checkbox);
                item.add(new Label("ytelseLabel", item.getModel()).add(new AttributeModifier("for", checkbox.getMarkupId())));
            }
        };
        visAlleYtelserCheckbox = createAlleYtelserCheckbox();
        return (MarkupContainer) new WebMarkupContainer("ytelseContainer")
                .add(visAlleYtelserCheckbox)
                .add(listView)
                .setOutputMarkupId(true);
    }

    private AjaxCheckBox createMottakerButton(final String id, final Mottakertype mottaker) {
        return new AjaxCheckBox(id, new Model<>(filterParametere.viseMottaker(mottaker))) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                filterParametere.toggleMottaker(mottaker);
                sendFilterEndretEvent();
            }
        };
    }

    private DateRangePicker createDateRangePicker() {
        LocalDate minDato = now().minusYears(TRE_AAR_TILBAKE).withDayOfYear(1);
        LocalDate maksDato = now();

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy");

        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate(formatter.print(minDato))
                .withMaxDate("0d")
                .withYearRange("-" + TRE_AAR_TILBAKE + "y:c")
                .withParameter("showOn", "button")
                .build();

        DateRangeModel dateRangeModel = new DateRangeModel(
                new PropertyModel<LocalDate>(filterParametere, "startDato"),
                new PropertyModel<LocalDate>(filterParametere, "sluttDato"));

        return new StrictDateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, minDato, maksDato);
    }

    private AjaxButton createSokKnapp() {
        AjaxButton button = new AjaxButton("sok", new StringResourceModel("utbetaling.lamell.filter.sok", this, null)) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sendFilterEndretEvent();
                target.add(ytelsesContainer, valideringsfeil);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(valideringsfeil);
            }
        };

        button.add(new AttributeModifier("alt", new StringResourceModel("utbetaling.lamell.filter.sok.alt", this, null)));

        return button;
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, FILTER_ENDRET);
    }

    private void sendYtelsesfilterCheckedEvent() {
        send(getPage(), Broadcast.DEPTH, YTELSE_FILTER_KLIKKET);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(HOVEDYTELSER_ENDRET)
    private void oppdaterYtelsesKnapper(AjaxRequestTarget target) {
        target.add(ytelsesContainer);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(YTELSE_FILTER_KLIKKET)
    private void oppdaterVelgAlleCheckbox(AjaxRequestTarget target) {
        target.add(visAlleYtelserCheckbox);
    }

}
