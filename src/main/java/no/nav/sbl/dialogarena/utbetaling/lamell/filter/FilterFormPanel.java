package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.utbetaling.util.AjaxIndicator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;
import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.ARBEIDSGIVER;
import static no.nav.sbl.dialogarena.utbetaling.domain.Utbetaling.BRUKER;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FILTER_ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;
import static org.joda.time.LocalDate.now;

public class FilterFormPanel extends Panel {

    private static final int AAR_TILBAKE = 3;

    private FilterParametere filterParametere;

    private MarkupContainer ytelsesContainer;
    private FeedbackPanel valideringsfeil;

    public FilterFormPanel(String id, FilterParametere filterParametere) {
        super(id);

        this.filterParametere = filterParametere;
        this.ytelsesContainer = createYtelser();

        add(createFilterForm());
    }

    private Form createFilterForm() {
        Form filterForm = new AjaxIndicator.SnurrepippFilterForm("filterForm");
        valideringsfeil = new FeedbackPanel("feedbackpanel");
        return (Form) filterForm.add(
                valideringsfeil.setOutputMarkupId(true),
                createMottakerButton("visBruker", BRUKER),
                createMottakerButton("visArbeidsgiver", ARBEIDSGIVER),
                ytelsesContainer,
                createDateRangePicker())
                .add(createDateRangePickerChangeBehaviour())
                .setOutputMarkupId(true);
    }

    private MarkupContainer createYtelser() {

        IModel<List<String>> alleYtelserModel = new AbstractReadOnlyModel<List<String>>() {
            @Override
            public List<String> getObject() {
                ArrayList<String> ytelser = new ArrayList<>(filterParametere.alleYtelser);
                sort(ytelser);
                return ytelser;
            }
        };
        ListView<String> listView = new ListView<String>("ytelseFilter", alleYtelserModel) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                final AjaxButton knapp = new AjaxButton("ytelseKnapp", item.getModel()) {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        String ytelse = item.getModelObject();
                        if (filterParametere.uonskedeYtelser.contains(ytelse)) {
                            filterParametere.uonskedeYtelser.remove(ytelse);
                        } else {
                            filterParametere.uonskedeYtelser.add(ytelse);
                        }

                        sendFilterEndretEvent();
                        target.add(this);
                    }
                };
                knapp.add(new AttributeModifier("value", item.getModelObject()));
                knapp.add(hasCssClassIf("valgt", new AbstractReadOnlyModel<Boolean>() {
                    @Override
                    public Boolean getObject() {
                        return !filterParametere.uonskedeYtelser.contains(knapp.getModelObject());
                    }
                }));
                item.add(knapp);
            }
        };
        return (MarkupContainer) new WebMarkupContainer("ytelseContainer").add(listView).setOutputMarkupId(true);
    }

    private AjaxButton createMottakerButton(final String id, final String mottaker) {
        AjaxButton mottakerButton = new AjaxButton(id) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterParametere.toggleMottaker(mottaker);
                sendFilterEndretEvent();
                target.add(this);
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                Boolean erMottakerValgt = filterParametere.viseMottaker(mottaker);
                String newClassname = getMarkupAttributes().get("class") + (erMottakerValgt ? " valgt" : "");
                add(AttributeModifier.replace("class", newClassname));
            }
        };

        return mottakerButton;
    }

    private DateRangePicker createDateRangePicker() {
        LocalDate minDato = now().minusYears(AAR_TILBAKE);
        LocalDate maksDato = now();

        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate("-" + AAR_TILBAKE + "y")
                .withMaxDate("0d")
                .withYearRange("-" + AAR_TILBAKE + "y:c")
                .withParameter("showOn", "both")
                .build();

        DateRangeModel dateRangeModel = new DateRangeModel(
                new PropertyModel<LocalDate>(filterParametere, "startDato"),
                new PropertyModel<LocalDate>(filterParametere, "sluttDato"));

        return new DateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, minDato, maksDato);
    }

    private AjaxFormSubmitBehavior createDateRangePickerChangeBehaviour() {
        return new AjaxFormSubmitBehavior("onchange") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                sendFilterEndretEvent();
                target.add(ytelsesContainer, valideringsfeil);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(valideringsfeil);
            }
        };
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, FILTER_ENDRET);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(HOVEDYTELSER_ENDRET)
    private void oppdaterYtelsesKnapper(AjaxRequestTarget target) {
        target.add(ytelsesContainer);
    }
}
