package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class LeggTilbakePanel extends Panel {

    public static final String LEGG_TILBAKE_ABRUTT = "leggtilbake.avbrutt";

    protected Aarsak valgtAarsak;
    protected Temagruppe temagruppe;
    protected String annenAarsakTekst;

    public LeggTilbakePanel(String id, Sporsmal sporsmal) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        add(new Label("temagruppe", new ResourceModel(sporsmal.temagruppe)));

        Form<LeggTilbakePanel> form = new Form<>("leggtilbakeform", new CompoundPropertyModel<>(this));

        WebMarkupContainer temagruppevelgerWrapper = new WebMarkupContainer("temagruppewrapper");
        final DropDownChoice<Temagruppe> temagruppevelger = new DropDownChoice<>("temagruppe", asList(Temagruppe.values()), new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        temagruppevelgerWrapper.add(hasCssClassIf("skjult", erIkkeValgtAarsak(Aarsak.FEIL_TEMAGRUPPE)));
        temagruppevelgerWrapper.add(temagruppevelger);

        final TextArea annenAarsak = new TextArea("annenAarsakTekst");
        annenAarsak.add(hasCssClassIf("skjult", erIkkeValgtAarsak(Aarsak.ANNEN)));

        RadioGroup<Aarsak> aarsaker = new RadioGroup<>("valgtAarsak");
        aarsaker.setRequired(true);
        aarsaker.add(new Radio<>("feiltema", Model.of(Aarsak.FEIL_TEMAGRUPPE)));
        aarsaker.add(temagruppevelgerWrapper);
        aarsaker.add(new Radio<>("inhabil", Model.of(Aarsak.INHABIL)));
        aarsaker.add(new Radio<>("annen", Model.of(Aarsak.ANNEN)));
        aarsaker.add(annenAarsak);

        form.add(aarsaker);

        aarsaker.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Aarsak aarsak = (Aarsak) getComponent().getDefaultModelObject();
                switch (aarsak) {
                    case FEIL_TEMAGRUPPE:
                        toggleRequired(true, temagruppevelger);
                        toggleRequired(false, annenAarsak);
                        break;
                    case ANNEN:
                        toggleRequired(true, annenAarsak);
                        toggleRequired(false, temagruppevelger);
                        break;
                    default:
                        toggleRequired(false, temagruppevelger, annenAarsak);
                        break;
                }
                target.add(LeggTilbakePanel.this);
            }
        });

        form.add(feedbackPanel);

        form.add(new AjaxButton("leggtilbake") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                leggTilbakeOppgave();
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        form.add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(LeggTilbakePanel.this, Broadcast.BUBBLE, LEGG_TILBAKE_ABRUTT);
            }
        });

        add(form);
    }

    private static void toggleRequired(boolean required, FormComponent... formComponents) {
        for (FormComponent formComponent : formComponents) {
            formComponent.setRequired(required);
        }
    }

    private AbstractReadOnlyModel<Boolean> erIkkeValgtAarsak(final Aarsak aarsak) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !aarsak.equals(valgtAarsak);
            }
        };
    }

    private void leggTilbakeOppgave() {
    }

    private static enum Aarsak {
        FEIL_TEMAGRUPPE,
        INHABIL,
        ANNEN
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});"));
    }
}
