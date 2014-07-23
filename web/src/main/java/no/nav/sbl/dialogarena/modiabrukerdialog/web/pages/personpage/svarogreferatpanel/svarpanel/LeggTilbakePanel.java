package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakeVM.Aarsak;

public class LeggTilbakePanel extends Panel {

    public static final String LEGG_TILBAKE_AVBRUTT = "leggtilbake.avbrutt";
    public static final String LEGG_TILBAKE_UTFORT = "leggtilbake.utfort";

    @Inject
    private SakService sakService;

    public LeggTilbakePanel(String id, String temagruppe, final Optional<String> oppgaveId) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final LeggTilbakeVM leggTilbakeVM = new LeggTilbakeVM();

        add(new Label("temagruppe", new ResourceModel(temagruppe)));

        Form<LeggTilbakeVM> form = new Form<>("leggtilbakeform", new CompoundPropertyModel<>(leggTilbakeVM));

        WebMarkupContainer temagruppevelgerWrapper = new WebMarkupContainer("temagruppewrapper");
        final DropDownChoice<Temagruppe> temagruppevelger = new DropDownChoice<>("nyTemagruppe", asList(Temagruppe.values()), new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        temagruppevelgerWrapper.add(hasCssClassIf("skjult", not(leggTilbakeVM.erValgtAarsak(Aarsak.FEIL_TEMAGRUPPE))));
        temagruppevelgerWrapper.add(temagruppevelger);

        final TextArea annenAarsak = new TextArea("annenAarsakTekst");
        annenAarsak.add(hasCssClassIf("skjult", not(leggTilbakeVM.erValgtAarsak(Aarsak.ANNEN))));

        final RadioGroup<Aarsak> aarsaker = new RadioGroup<>("valgtAarsak");
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
                Aarsak aarsak = aarsaker.getModelObject();
                switch (aarsak) {
                    case FEIL_TEMAGRUPPE:
                        setRequired(true, temagruppevelger);
                        setRequired(false, annenAarsak);
                        break;
                    case ANNEN:
                        setRequired(true, annenAarsak);
                        setRequired(false, temagruppevelger);
                        break;
                    default:
                        setRequired(false, temagruppevelger, annenAarsak);
                        break;
                }
                target.add(LeggTilbakePanel.this);
            }
        });

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupPlaceholderTag(true);
        form.add(feedbackPanel);

        form.add(new AjaxButton("leggtilbake") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                sakService.leggTilbakeOppgaveIGsak(
                        oppgaveId,
                        leggTilbakeVM.lagBeskrivelse(new StringResourceModel(leggTilbakeVM.getBeskrivelseKey(), LeggTilbakePanel.this, null).getString()),
                        leggTilbakeVM.lagTemagruppeTekst());
                send(LeggTilbakePanel.this, Broadcast.BUBBLE, LEGG_TILBAKE_UTFORT);
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        form.add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(LeggTilbakePanel.this, Broadcast.BUBBLE, LEGG_TILBAKE_AVBRUTT);
            }
        });

        add(form);
    }

    private static void setRequired(boolean required, FormComponent... formComponents) {
        for (FormComponent formComponent : formComponents) {
            formComponent.setRequired(required);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});"));
    }
}
