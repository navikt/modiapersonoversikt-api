package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.constants.URLParametere.HENVENDELSEID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.constants.URLParametere.OPPGAVEID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference MELDINGER_JS = new JavaScriptResourceReference(Innboks.class, "meldinger.js");
    public static final ConditionalCssResource MELDINGER_IE_CSS = new ConditionalCssResource(new CssResourceReference(Innboks.class, "innboks-ie.css"), "screen", "IE");
    public static final String INNBOKS_OPPDATERT_EVENT = "sos.innboks.oppdatert";
    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";

    @Inject
    private GsakService gsakService;
    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private InnboksVM innboksVM;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVM = new InnboksVM(fnr, henvendelseBehandlingService);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));

        haandterSessionParametere();

        PropertyModel<Boolean> harTraader = new PropertyModel<>(innboksVM, "harTraader");

        TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        traaddetaljerPanel.setOutputMarkupId(true);
        traaddetaljerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));

        AlleMeldingerPanel alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM, traaddetaljerPanel.getMarkupId());
        alleMeldingerPanel.add(visibleIf(both(harTraader).and(not(innboksVM.harFeilmelding()))));


        WebMarkupContainer feilmeldingPanel = new WebMarkupContainer("feilmeldingpanel");
        feilmeldingPanel.add(new Label("feilmelding", new StringResourceModel("${feilmeldingKey}", getDefaultModel(), "")));
        feilmeldingPanel.add(visibleIf(innboksVM.harFeilmelding()));

        add(alleMeldingerPanel, traaddetaljerPanel, feilmeldingPanel);
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
        innboksVM.setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void oppdatertInnboks(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        send(getPage(), Broadcast.DEPTH, INNBOKS_OPPDATERT_EVENT);
        target.add(this);
    }

}
