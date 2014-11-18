package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakeVM.Aarsak;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel.LeggTilbakeVM.Aarsak.*;
import static org.apache.wicket.event.Broadcast.BUBBLE;

public class LeggTilbakePanel extends Panel {

    public static final String LEGG_TILBAKE_AVBRUTT = "leggtilbake.avbrutt";
    public static final String LEGG_TILBAKE_UTFORT = "leggtilbake.utfort";
    public static final String LEGG_TILBAKE_FERDIG = "leggtilbake.ferdig";
    private final Radio<Aarsak> feiltema;

    @Inject
    protected OppgaveBehandlingService oppgaveBehandlingService;

    private IModel<Boolean> oppgaveLagtTilbake = Model.of(false);
    private final RadioGroup<Aarsak> aarsaker;

    public LeggTilbakePanel(String id, String temagruppe, final Optional<String> oppgaveId) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final LeggTilbakeVM leggTilbakeVM = new LeggTilbakeVM();
        PropertyModel valgtAarsak = new PropertyModel(leggTilbakeVM, "valgtAarsak");

        add(new Label("temagruppe", new ResourceModel(temagruppe, "")));

        Form<LeggTilbakeVM> form = new Form<>("leggtilbakeform", new CompoundPropertyModel<>(leggTilbakeVM));
        form.add(visibleIf(not(oppgaveLagtTilbake)));
        WebMarkupContainer nyTemagruppeSkjuler = new WebMarkupContainer("nyTemagruppeSkjuler");
        WebMarkupContainer temagruppevelgerWrapper = new WebMarkupContainer("temagruppewrapper");
        final DropDownChoice<Temagruppe> temagruppevelger = new DropDownChoice<>("nyTemagruppe", asList(Temagruppe.values()), new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        nyTemagruppeSkjuler.add(visibleIf(isEqualTo(valgtAarsak, FEIL_TEMAGRUPPE)));
        nyTemagruppeSkjuler.add(temagruppevelger);

        final TextArea annenAarsak = new TextArea("annenAarsakTekst");
        annenAarsak.add(visibleIf(isEqualTo(valgtAarsak, ANNEN)));
        feiltema = new Radio<>("feiltema", Model.of(FEIL_TEMAGRUPPE));

        aarsaker = new RadioGroup<>("valgtAarsak");
        aarsaker.setRequired(true);
        aarsaker.add(feiltema,
                nyTemagruppeSkjuler,
                new Radio<>("inhabil", Model.of(INHABIL)),
                new Radio<>("annen", Model.of(ANNEN)),
                annenAarsak);

        form.add(aarsaker, temagruppevelgerWrapper);

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
        form.add(feedbackPanel.setOutputMarkupPlaceholderTag(true));

        final WebMarkupContainer feedbackPanelSuccess = new WebMarkupContainer("feedbackOppgavePanel");
        feedbackPanelSuccess.setOutputMarkupPlaceholderTag(true).add(visibleIf(oppgaveLagtTilbake));
        final AjaxLink lukkKnapp = new AjaxLink("lukkKnapp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(LeggTilbakePanel.this, BUBBLE, LEGG_TILBAKE_FERDIG);
            }
        };
        feedbackPanelSuccess.add(lukkKnapp);
        add(feedbackPanelSuccess);

        form.add(new AjaxButton("leggtilbake") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                        oppgaveId,
                        leggTilbakeVM.lagBeskrivelse(
                            new StringResourceModel(leggTilbakeVM.getBeskrivelseKey(), LeggTilbakePanel.this, null).getString()),
                        leggTilbakeVM.lagTemagruppeTekst()
                );
                oppgaveLagtTilbake.setObject(true);
                send(LeggTilbakePanel.this, BUBBLE, LEGG_TILBAKE_UTFORT);

                target.add(form, feedbackPanelSuccess);
                target.focusComponent(lukkKnapp);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        form.add(new AjaxLink<Void>("avbryt") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                aarsaker.getModel().setObject(null);
                target.add(LeggTilbakePanel.this);
                send(LeggTilbakePanel.this, BUBBLE, LEGG_TILBAKE_AVBRUTT);
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

    public Component hentForsteFokusKomponent() {
        return feiltema;
    }
}
