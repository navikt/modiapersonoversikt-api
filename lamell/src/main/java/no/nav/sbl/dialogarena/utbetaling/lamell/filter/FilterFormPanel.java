package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.ENDRET;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.FEIL;
import static no.nav.sbl.dialogarena.utbetaling.lamell.filter.FilterParametere.HOVEDYTELSER_ENDRET;
import static org.joda.time.LocalDate.now;

public class FilterFormPanel extends Panel {

    private static final int AAR_TILBAKE = 3;

    private FilterParametere filterParametere;

    private MarkupContainer ytelsesContainer;

    public FilterFormPanel(String id, FilterParametere filterParametere) {
        super(id);

        this.filterParametere = filterParametere;
        this.ytelsesContainer = createYtelser();

        add(createFilterForm());
    }

    private Form createFilterForm() {
        Form filterForm = new Form<>("filterForm");
        return (Form) filterForm.add(
                new FeedbackPanel("feedbackpanel"),
                createMottakerButton("visBruker"),
                createMottakerButton("visArbeidsgiver"),
                ytelsesContainer,
                createDateRangePicker())
                .add(createDateRangePickerChangeBehaviour(filterForm))
                .setOutputMarkupId(true);
    }

    private MarkupContainer createYtelser() {

        IModel<List<String>> alleYtelserModel = new AbstractReadOnlyModel<List<String>>() {
            @Override
            public List<String> getObject() {
                return new ArrayList<>(filterParametere.alleYtelser);
            }
        };
        ListView<String> listView = new ListView<String>("ytelseFilter", alleYtelserModel) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                final AjaxLink<String> knapp = new AjaxLink<String>("ytelseKnapp", item.getModel()) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        String ytelse = item.getModelObject();
                        if (filterParametere.uonskedeYtelser.contains(ytelse)) {
                            filterParametere.uonskedeYtelser.remove(ytelse);
                        } else {
                            filterParametere.uonskedeYtelser.add(ytelse);
                        }

                        sendFilterEndretEvent();
                        target.add(this);
                    }

                    @Override
                    public void renderHead(IHeaderResponse response) {
                        response.render(OnLoadHeaderItem.forScript(createSnurrepippJS("input:button", "click")));
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

    private AjaxLink<Boolean> createMottakerButton(final String mottaker) {
        AjaxLink<Boolean> mottakerButton = new AjaxLink<Boolean>(mottaker, new PropertyModel<Boolean>(filterParametere, mottaker)) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setModelObject(!getModelObject());
                sendFilterEndretEvent();
                target.add(this);
            }

            @Override
            public void renderHead(IHeaderResponse response) {
                response.render(OnLoadHeaderItem.forScript(createSnurrepippJS("input:button", "click")));
            }
        };
        mottakerButton.add(hasCssClassIf("valgt", mottakerButton.getModel()));

        return mottakerButton;
    }

    private DateRangePicker createDateRangePicker() {
        LocalDate minDato = now().minusYears(AAR_TILBAKE);
        LocalDate maksDato = now();

        DatePickerConfigurator datePickerConfigurator = datePickerConfigurator()
                .withMinDate("-" + AAR_TILBAKE + "y")
                .withMaxDate("0d")
                .withYearRange("-" + AAR_TILBAKE + "y:c")
                .build();

        DateRangeModel dateRangeModel = new DateRangeModel(
                new PropertyModel<LocalDate>(filterParametere, "startDato"),
                new PropertyModel<LocalDate>(filterParametere, "sluttDato"));

        return new DateRangePicker("datoFilter", dateRangeModel, datePickerConfigurator, minDato, maksDato) {

            @Override
            public void renderHead(IHeaderResponse response) {
                response.render(OnLoadHeaderItem.forScript(createSnurrepippJS("input", "change")));
                super.renderHead(response);
            }
        };
    }

    private AjaxFormSubmitBehavior createDateRangePickerChangeBehaviour(final Form<?> filterForm) {
        return new AjaxFormSubmitBehavior("onchange") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                sendFilterEndretEvent();
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                sendFilterFeilEvent();
                target.add(filterForm);
            }
        };
    }

    private String createSnurrepippJS(String selector, String event) {
        String contextRoot = WebApplication.get().getServletContext().getContextPath();
        String targetSelector = ".oppsummering-total";

        return "$('" + selector + "').on('" + event + "', function() {" +
                "   window.Modig.ajaxLoader.showLoader('" + targetSelector + "', '', '" + contextRoot + "/img/ajaxloader/graa/loader_graa_32.gif', '');" +
                "});";
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, ENDRET);
    }

    private void sendFilterFeilEvent() {
        send(getPage(), Broadcast.DEPTH, FEIL);
    }

    @RunOnEvents(HOVEDYTELSER_ENDRET)
    private void oppdaterYtelsesKnapper(AjaxRequestTarget target) {
        target.add(ytelsesContainer);
    }
}
