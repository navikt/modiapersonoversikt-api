package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static no.nav.modig.modia.events.InternalEvents.*;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.URLParametere.*;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference MELDINGER_JS = new JavaScriptResourceReference(Innboks.class, "meldinger.js");
    public static final ConditionalCssResource MELDINGER_IE_CSS = new ConditionalCssResource(new CssResourceReference(Innboks.class, "innboks-ie.css"), "screen", "IE");
    public static final String INNBOKS_OPPDATERT_EVENT = "sos.innboks.oppdatert";
    public static final String BESVARMODUS_PARAM = "sos.innboks.modus.besvarer";

    @Inject
    private GsakService gsakService;
    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private InnboksVM innboksVM;

    public Innboks(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVM = new InnboksVM(fnr, henvendelseBehandlingService);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));

        haandterSessionParametere();

        PropertyModel<Boolean> harTraader = new PropertyModel<>(innboksVM, "harTraader");

        final TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        traaddetaljerPanel.setOutputMarkupId(true);
        traaddetaljerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));


        final AlleMeldingerPanel alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM, traaddetaljerPanel.getMarkupId());
        alleMeldingerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));

        final ReactComponentPanel meldingerSok = new ReactComponentPanel("meldingerSokContainer", "MeldingerSok", getMeldingerSokProps());
        meldingerSok.add(visibleIf(not(innboksVM.harFeilmelding())));


        final WebMarkupContainer meldingerSokToggleContainer = new WebMarkupContainer("meldingerSokToggleContainer");
        meldingerSokToggleContainer.setOutputMarkupId(true);
        AjaxLink meldingerSokToggleButton = new SokKnapp("meldingerSokToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksVM.oppdaterMeldinger();
                target.add(alleMeldingerPanel, traaddetaljerPanel);
                meldingerSok.callFunction(target, "vis", getMeldingerSokProps());
                target.add(meldingerSokToggleContainer);
            }
        };
        meldingerSokToggleButton.add(visibleIf(not(innboksVM.harFeilmelding())));
        meldingerSokToggleContainer.add(meldingerSokToggleButton);

        WebMarkupContainer feilmeldingPanel = new WebMarkupContainer("feilmeldingpanel");
        feilmeldingPanel.add(new Label("feilmelding", new StringResourceModel("${feilmeldingKey}", getDefaultModel(), "")));
        feilmeldingPanel.add(visibleIf(innboksVM.harFeilmelding()));

        add(meldingerSok, meldingerSokToggleContainer, alleMeldingerPanel, traaddetaljerPanel, feilmeldingPanel);
    }

    private Map<String, Object> getMeldingerSokProps() {
        Map<String, Object> props = new HashMap<>();
        props.put("fnr", innboksVM.getFnr());
        props.put("traadMarkupIds", traadRefs(innboksVM));
        return props;
    }

    private Map<String, String> traadRefs(InnboksVM innboksVM) {
        HashMap<String, String> traadRefs = new HashMap<>();

        for (MeldingVM meldingVM : innboksVM.getNyesteMeldingerITraad()) {
            traadRefs.put(meldingVM.melding.traadId, AlleMeldingerPanel.TRAAD_ID_PREFIX + meldingVM.melding.traadId);
        }

        return traadRefs;
    }

    private void haandterSessionParametere() {
        String traadIdParameter = (String) getSession().getAttribute(HENVENDELSEID);
        if (isNotBlank(traadIdParameter)) {
            innboksVM.setSessionHenvendelseId(traadIdParameter);
            Optional<MeldingVM> meldingITraad = innboksVM.getNyesteMeldingITraad(traadIdParameter);
            if (meldingITraad.isSome()) {
                innboksVM.setValgtMelding(meldingITraad.get());
            }
        }
        String oppgaveIdParameter = (String) getSession().getAttribute(OPPGAVEID);
        if (isNotBlank(oppgaveIdParameter) && gsakService.oppgaveKanManuelltAvsluttes(oppgaveIdParameter)) {
            innboksVM.setSessionOppgaveId(oppgaveIdParameter);
        }

        String fortsettDialogModus = (String) getSession().getAttribute(FORTSETTDIALOGMODUS);
        String traadBesvares = (String) getSession().getAttribute(BESVARMODUS_PARAM);
        if (isNotBlank(oppgaveIdParameter) && isNotBlank(traadIdParameter) && fortsettDialogModus != null && fortsettDialogModus.equals(TRUE.toString())) {
            innboksVM.traadBesvares = traadIdParameter;
        }else if (isNotBlank(traadBesvares)){
            innboksVM.traadBesvares = traadBesvares;
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(
                "$(document).on('click', '.innboksSokToggle button',function(){$(this).hide().siblings('img').show();});"));
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        if (target != null) {
            target.appendJavaScript("Meldinger.addKeyNavigation();");
        }
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        String itemId = feedItemPayload.getItemId();
        if (!itemId.equals(innboksVM.getValgtTraad().getNyesteMelding().melding.id)) {
            innboksVM.setValgtMelding(itemId);
            send(getPage(), Broadcast.DEPTH, MELDING_VALGT);
            target.add(this);
        }
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void oppdatertInnboks(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        send(getPage(), Broadcast.DEPTH, INNBOKS_OPPDATERT_EVENT);
        target.add(this);
    }

    @RunOnEvents(SVAR_PAA_MELDING)
    public void setBesvarModus(AjaxRequestTarget target, String traadId) {
        getSession().setAttribute(BESVARMODUS_PARAM, traadId);
        innboksVM.traadBesvares = traadId;
        target.add(this);
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, MELDING_SENDT_TIL_BRUKER})
    public void unsetBesvartModus(AjaxRequestTarget target) {
        getSession().setAttribute(BESVARMODUS_PARAM, null);
        innboksVM.traadBesvares = null;
        target.add(this);
    }

}
