package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(Innboks.class, "innboks.js");
    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";

    @Inject
    private MeldingService meldingService;

    private String fnr;
    private IModel<InnboksVM> innboksVM;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.fnr = fnr;

        innboksVM = new CompoundPropertyModel<>(new InnboksVM(meldingService.hentMeldinger(fnr)));
        setDefaultModel(innboksVM);
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", innboksVM), new TraaddetaljerPanel("detaljpanel", innboksVM));
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        innboksVM.getObject().oppdaterMeldinger(meldingService.hentMeldinger(fnr));
        super.onOpening(target);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.getObject().setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

}
