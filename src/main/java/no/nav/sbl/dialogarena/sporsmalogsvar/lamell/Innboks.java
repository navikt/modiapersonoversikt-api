package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentCallback;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
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
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events.SporsmalOgSvar.MELDING_VALGT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_MERKET;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference MELDINGER_JS = new JavaScriptResourceReference(Innboks.class, "meldinger.js");
    public static final JavaScriptResourceReference BESVAR_INDIKATOR_JS = new JavaScriptResourceReference(Innboks.class, "besvarIndikator.js");
    public static final String INNBOKS_OPPDATERT_EVENT = "sos.innboks.oppdatert";

    private InnboksVM innboksVM;

    public Innboks(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));
        this.innboksVM = innboksVM;
        innboksVM.oppdaterMeldinger();
        innboksVM.settForsteSomValgtHvisIkkeSatt();

        if (innboksVM.getSessionHenvendelseId().isSome()) {
            Optional<MeldingVM> meldingITraad = innboksVM.getNyesteMeldingITraad(innboksVM.getSessionHenvendelseId().get());
            if (meldingITraad.isSome()) {
                innboksVM.setValgtMelding(meldingITraad.get());
            }
        }

        PropertyModel<Boolean> harTraader = new PropertyModel<>(innboksVM, "harTraader");

        WebMarkupContainer meldingsliste = new WebMarkupContainer("meldingsliste");
        meldingsliste.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));

        final TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        traaddetaljerPanel.setOutputMarkupId(true);
        traaddetaljerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));


        final AlleMeldingerPanel alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM);
        final ReactComponentPanel meldingerSok = new ReactComponentPanel("meldingerSokContainer", "MeldingerSok", getMeldingerSokProps());


        final WebMarkupContainer meldingerSokToggleContainer = new WebMarkupContainer("meldingerSokToggleContainer");
        meldingerSokToggleContainer.setOutputMarkupId(true);
        AjaxLink meldingerSokToggleButton = new SokKnapp("meldingerSokToggle") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksVM.oppdaterMeldinger();
                target.add(alleMeldingerPanel, traaddetaljerPanel);
                meldingerSok.call("vis", getMeldingerSokProps());
                target.add(meldingerSokToggleContainer);
            }
        };
        meldingerSok.addCallback("reindekser", Void.class, new ReactComponentCallback<Void>() {
            @Override
            public void onCallback(AjaxRequestTarget target, Void data) {
                innboksVM.oppdaterMeldinger();
                target.add(alleMeldingerPanel, traaddetaljerPanel);
                meldingerSok.call("vis", getMeldingerSokProps());
                target.add(meldingerSokToggleContainer);
            }
        });
        meldingerSokToggleContainer.add(meldingerSokToggleButton);

        WebMarkupContainer feilmeldingPanel = new WebMarkupContainer("feilmeldingpanel");
        feilmeldingPanel.add(new Label("feilmelding", new StringResourceModel("${feilmeldingKey}", getDefaultModel(), "")));
        feilmeldingPanel.add(visibleIf(innboksVM.harFeilmelding()));

        meldingsliste.add(meldingerSok, meldingerSokToggleContainer, alleMeldingerPanel);
        add(meldingsliste, traaddetaljerPanel, feilmeldingPanel);
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

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript(
                "$(document).on('click', '.innboksSokToggle button',function(){$(this).hide().siblings('img').show();});"));
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        if (target != null) {
            if (innboksVM.focusValgtTraadOnOpen) {
                target.appendJavaScript("Meldinger.focusOnSelectedElement()");
            }
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
        innboksVM.focusValgtTraadOnOpen = true;
    }

    @RunOnEvents(Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER)
    public void oppdatertInnboks(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        send(getPage(), Broadcast.DEPTH, INNBOKS_OPPDATERT_EVENT);
        target.add(this);
    }

    @RunOnEvents(Events.SporsmalOgSvar.SVAR_PAA_MELDING)
    public void setBesvarModus(AjaxRequestTarget target, String traadId) {
        innboksVM.traadBesvares = traadId;
        target.add(this);
    }

    @RunOnEvents({Events.SporsmalOgSvar.SVAR_AVBRUTT, Events.SporsmalOgSvar.LEGG_TILBAKE_UTFORT, Events.SporsmalOgSvar.MELDING_SENDT_TIL_BRUKER, TRAAD_MERKET})
    public void leggTilTarget(AjaxRequestTarget target) {
        target.add(this);
    }

}
