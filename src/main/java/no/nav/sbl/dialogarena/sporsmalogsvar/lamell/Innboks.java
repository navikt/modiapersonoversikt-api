package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;

public class Innboks extends Lerret {

    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";

    @Inject
    private MeldingService meldingService;

    private String fnr;
    private IModel<InnboksVM> innboksVM;

    private AlleMeldingerPanel alleMeldingerPanel;
    private TraaddetaljerPanel traaddetaljerPanel;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.fnr = fnr;

        innboksVM = new CompoundPropertyModel<>(new InnboksVM(meldingService.hentMeldinger(fnr)));
        setDefaultModel(innboksVM);

        alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM);
        traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        add(alleMeldingerPanel, traaddetaljerPanel);
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        oppdaterMeldingeneIInnboksVM();
        super.onOpening(target);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.getObject().setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target){
        if(alleMeldingerPanel.isVisibleInHierarchy() || traaddetaljerPanel.isVisibleInHierarchy()){
            oppdaterMeldingeneIInnboksVM();
        }
        if(traaddetaljerPanel.isVisibleInHierarchy()){
            target.add(traaddetaljerPanel);
        }
        if(alleMeldingerPanel.isVisibleInHierarchy()){
            target.add(alleMeldingerPanel);
        }
    }

    private void oppdaterMeldingeneIInnboksVM() {
        innboksVM.getObject().oppdaterMeldinger(meldingService.hentMeldinger(fnr));
    }

}
