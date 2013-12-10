package no.nav.sbl.dialogarena.utbetaling.lamell.filter;

import no.nav.modig.wicket.component.datepicker.DatePickerConfigurator;
import no.nav.modig.wicket.component.daterangepicker.DateRangeModel;
import no.nav.modig.wicket.component.daterangepicker.DateRangePicker;
import no.nav.sbl.dialogarena.utbetaling.filter.FilterParametere;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.joda.time.LocalDate;

import static no.nav.modig.wicket.component.datepicker.DatePickerConfigurator.DatePickerConfiguratorBuilder.datePickerConfigurator;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static org.joda.time.LocalDate.now;

public class FilterFormPanel extends Panel {

    public static final String FEIL = "filter.feil";
    private static final int AAR_TILBAKE = 3;

    private FilterParametere filterParametere;

    public FilterFormPanel(String id, FilterParametere filterParametere) {
        super(id);

        this.filterParametere = filterParametere;

        add(createFilterForm());
    }

    private Form createFilterForm() {
        Form filterForm = new Form("filterForm");
        return (Form) filterForm.add(
                new FeedbackPanel("feedbackpanel"),
                createMottakerButton("visBruker"),
                createMottakerButton("visArbeidsgiver"),
                createDateRangePicker())
                .add(createDateRangePickerChangeBehaviour(filterForm))
                .setOutputMarkupId(true);
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

    private AjaxFormSubmitBehavior createDateRangePickerChangeBehaviour(final Form filterForm) {
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
        String targetSelector = ".oppsummeringslinje:first";
        String insertMethod = "replace";

        return "$('"+selector+"').on('"+event+"', function() {" +
                "   window.Modig.ajaxLoader.showLoader('"+targetSelector+"', '"+insertMethod+"', '"+contextRoot+"/img/ajaxloader/hvit/loader_hvit_48.gif', '');" +
                "});";
    }

    private void sendFilterEndretEvent() {
        send(getPage(), Broadcast.DEPTH, FilterParametere.ENDRET);
    }

    private void sendFilterFeilEvent() {
        send(getPage(), Broadcast.DEPTH, FEIL);
    }
}
