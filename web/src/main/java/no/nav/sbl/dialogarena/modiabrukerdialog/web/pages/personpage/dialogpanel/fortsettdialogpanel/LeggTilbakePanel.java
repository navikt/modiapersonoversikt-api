package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import com.codahale.metrics.Timer;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.metrics.MetricsFactory;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.GrunnInfo;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEqualTo;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakeVM.Aarsak;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakeVM.Aarsak.*;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.wicket.event.Broadcast.*;

public class LeggTilbakePanel extends Panel {

    public static final String LEGG_TILBAKE_AVBRUTT = "leggtilbake.avbrutt";
    public static final String LEGG_TILBAKE_FERDIG = "leggtilbake.ferdig";

    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private HenvendelseUtsendingService henvendelseUtsendingService;

    private final Radio<Aarsak> feiltema;
    private final Optional<String> oppgaveId;
    private final Melding sporsmal;
    private final IModel<Boolean> oppgaveLagtTilbake = Model.of(false);
    private final RadioGroup<Aarsak> aarsaker;
    private final LeggTilbakeVM leggTilbakeVM;
    private final WebMarkupContainer feedbackPanelSuccess;
    private final FeedbackPanel feedbackPanel;
    private final AjaxLink lukkKnapp;

    public LeggTilbakePanel(String id, String temagruppe, Temagruppe gjeldendeTemagruppe, final Optional<String> oppgaveId, Melding sporsmal) {
        super(id);
        this.oppgaveId = oppgaveId;
        this.sporsmal = sporsmal;
        setOutputMarkupPlaceholderTag(true);

        leggTilbakeVM = new LeggTilbakeVM();
        PropertyModel<Aarsak> valgtAarsak = new PropertyModel<>(leggTilbakeVM, "valgtAarsak");

        add(new Label("temagruppe", new ResourceModel(temagruppe, "")));

        Form<LeggTilbakeVM> form = new Form<>("leggtilbakeform", new CompoundPropertyModel<>(leggTilbakeVM));
        form.add(visibleIf(not(oppgaveLagtTilbake)));

        final DropDownChoice<Temagruppe> temagruppevelger = lagFeiltemagruppeKomponenter(valgtAarsak, sporsmal);
        final WebMarkupContainer temagruppevelgerDropdown = new WebMarkupContainer("temagruppewrapper-dropdown");

        final TextArea annenAarsak = new TextArea("annenAarsakTekst");
        annenAarsak.add(visibleIf(isEqualTo(valgtAarsak, ANNEN)));
        WebMarkupContainer temagruppeWrapper = new WebMarkupContainer("temagruppeWrapper");
        temagruppeWrapper.setVisibilityAllowed(gjeldendeTemagruppe != ANSOS);
        feiltema = new Radio<>("feiltema", Model.of(FEIL_TEMAGRUPPE));
        temagruppeWrapper.add(feiltema, temagruppevelger.getParent());

        aarsaker = new RadioGroup<>("valgtAarsak");
        aarsaker.setRequired(true);
        aarsaker.add(
                temagruppeWrapper,
                new Radio<>("inhabil", Model.of(INHABIL)),
                new Radio<>("annen", Model.of(ANNEN)),
                annenAarsak);
        form.add(aarsaker, temagruppevelgerDropdown);

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
        feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        form.add(feedbackPanel.setOutputMarkupPlaceholderTag(true));

        feedbackPanelSuccess = new WebMarkupContainer("feedbackOppgavePanel");
        feedbackPanelSuccess.setOutputMarkupPlaceholderTag(true).add(visibleIf(oppgaveLagtTilbake));
        lukkKnapp = new AjaxLink("lukkKnapp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), DEPTH, LEGG_TILBAKE_FERDIG);
            }
        };
        feedbackPanelSuccess.add(lukkKnapp);
        add(feedbackPanelSuccess);

        form.add(lagSubmitKnapp());

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

    private AjaxButton lagSubmitKnapp() {
        return new IndicatingAjaxButtonWithImageUrl("leggtilbake", "../img/ajaxloader/svart/loader_svart_48.gif") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Timer.Context timer = MetricsFactory.createTimer("hendelse.leggtilbake." + leggTilbakeVM.valgtAarsak + ".time").time();
                try {
                    oppgaveBehandlingService.leggTilbakeOppgaveIGsak(
                            oppgaveId,
                            leggTilbakeVM.lagBeskrivelse(
                                    new StringResourceModel(leggTilbakeVM.getBeskrivelseKey(), LeggTilbakePanel.this, null).getString()),
                            optional(leggTilbakeVM.nyTemagruppe)
                    );
                    oppgaveLagtTilbake.setObject(true);

                    if (leggTilbakeVM.valgtAarsak == FEIL_TEMAGRUPPE) {
                        henvendelseUtsendingService.oppdaterTemagruppe(sporsmal.id, leggTilbakeVM.nyTemagruppe.name());
                        if (leggTilbakeVM.nyTemagruppe == ANSOS) {
                            henvendelseUtsendingService.merkSomKontorsperret(sporsmal.fnrBruker, singletonList(sporsmal.id));
                        }
                    }

                    target.add(form, feedbackPanelSuccess);
                    target.focusComponent(lukkKnapp);
                    send(getPage(), BREADTH, LEGG_TILBAKE_UTFORT);
                } finally {
                    timer.stop();
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
    }

    private DropDownChoice<Temagruppe> lagFeiltemagruppeKomponenter(PropertyModel<Aarsak> valgtAarsak, Melding melding) {
        WebMarkupContainer nyTemagruppeSkjuler = new WebMarkupContainer("nyTemagruppeSkjuler");

        List<Temagruppe> temagrupper = new ArrayList<>(Temagruppe.LEGG_TILBAKE);
        if (isBlank(melding.tilknyttetEnhet)) {
            temagrupper.removeAll(Temagruppe.KOMMUNALE_TJENESTER);
        }

        final DropDownChoice<Temagruppe> temagruppevelger = new DropDownChoice<>("nyTemagruppe", temagrupper, new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        });
        nyTemagruppeSkjuler.add(visibleIf(isEqualTo(valgtAarsak, FEIL_TEMAGRUPPE)));
        nyTemagruppeSkjuler.add(temagruppevelger);

        return temagruppevelger;
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
