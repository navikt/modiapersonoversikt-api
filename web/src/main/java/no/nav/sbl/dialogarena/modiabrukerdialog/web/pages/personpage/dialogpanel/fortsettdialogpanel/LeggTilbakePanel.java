package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel;

import no.nav.metrics.Timer;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.DialogSession;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
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
import static no.nav.metrics.MetricsFactory.createTimer;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.DialogPanel.NESTE_DIALOG_LENKE_VALGT;
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
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private final Radio<Aarsak> feiltema;
    private final String oppgaveId;
    private final Melding sporsmal;
    private final IModel<Boolean> oppgaveLagtTilbake = Model.of(false);
    private final RadioGroup<Aarsak> aarsaker;
    private final LeggTilbakeVM leggTilbakeVM;
    private final WebMarkupContainer feedbackPanelSuccess;
    private final FeedbackPanel feedbackPanel;
    private final AjaxLink lukkKnapp;
    private final AjaxLink nesteOppgaveKnapp;
    private final String behandlingsId;
    private final Temagruppe gjeldendeTemagruppe;
    private final Radio<Aarsak> inhabil;

    public LeggTilbakePanel(String id, String temagruppe, Temagruppe gjeldendeTemagruppe, final String oppgaveId, Melding sporsmal, String behandlingsId) {
        super(id);
        this.oppgaveId = oppgaveId;
        this.sporsmal = sporsmal;
        this.behandlingsId = behandlingsId;
        this.gjeldendeTemagruppe = gjeldendeTemagruppe;
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
        inhabil = new Radio<>("inhabil", Model.of(INHABIL));
        temagruppeWrapper.add(feiltema, temagruppevelger.getParent());

        aarsaker = new RadioGroup<>("valgtAarsak");
        aarsaker.setRequired(true);
        aarsaker.add(
                temagruppeWrapper,
                inhabil,
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
        nesteOppgaveKnapp = new AjaxLink("nesteOppgaveKnapp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                Page page = getPage();
                send(page, DEPTH, LEGG_TILBAKE_FERDIG);
                send(page, DEPTH, NESTE_DIALOG_LENKE_VALGT);
            }
        };
        nesteOppgaveKnapp.add(visibleIf(new Model<Boolean>(){
            @Override
            public Boolean getObject() {
                return DialogSession.read(LeggTilbakePanel.this).getPlukkedeOppgaver().size() > 0;
            }
        }));

        feedbackPanelSuccess.add(nesteOppgaveKnapp, lukkKnapp);
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
                Timer timer = createTimer("hendelse.leggtilbake." + leggTilbakeVM.valgtAarsak);
                timer.start();
                try {
                    LeggTilbakeOppgaveIGsakRequest request = new LeggTilbakeOppgaveIGsakRequest()
                            .withSaksbehandlersValgteEnhet(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())
                            .withOppgaveId(oppgaveId)
                            .withBeskrivelse(leggTilbakeVM.lagBeskrivelse(new StringResourceModel(leggTilbakeVM.getBeskrivelseKey(), LeggTilbakePanel.this, null).getString()))
                            .withTemagruppe(leggTilbakeVM.nyTemagruppe);

                    oppgaveBehandlingService.leggTilbakeOppgaveIGsak(request);
                    oppgaveLagtTilbake.setObject(true);

                    if (leggTilbakeVM.valgtAarsak == FEIL_TEMAGRUPPE) {
                        henvendelseUtsendingService.oppdaterTemagruppe(sporsmal.id, leggTilbakeVM.nyTemagruppe.name());
                        if (leggTilbakeVM.nyTemagruppe == ANSOS) {
                            henvendelseUtsendingService.merkSomKontorsperret(sporsmal.fnrBruker, singletonList(sporsmal.id));
                        }
                    }

                    target.add(form, feedbackPanelSuccess);

                    if(DialogSession.read(LeggTilbakePanel.this).getPlukkedeOppgaver().size() > 0) {
                        target.focusComponent(nesteOppgaveKnapp);
                    }  else {
                        target.focusComponent(lukkKnapp);
                    }

                    send(getPage(), BREADTH, LEGG_TILBAKE_UTFORT);
                    henvendelseUtsendingService.avbrytHenvendelse(behandlingsId);
                } finally {
                    timer.stop();
                    timer.report();
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
        if (isBlank(melding.brukersEnhet)) {
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
        if (gjeldendeTemagruppe == ANSOS) {
            return inhabil;
        } else{
            return feiltema;
        }
    }
}
