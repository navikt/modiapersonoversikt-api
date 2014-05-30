package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.model.MeldingBuffer;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(Innboks.class, "innboks.js");
    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";

    @Inject
    MeldingService meldingService;

    private CompoundPropertyModel<InnboksVM> innboksVM;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        MeldingBuffer.oppdaterMeldinger(meldingService.hentMeldinger(fnr));
        innboksVM = new CompoundPropertyModel<>(new InnboksVM(MeldingBuffer.getMeldinger()));
        setDefaultModel(innboksVM);
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", innboksVM), new TraaddetaljerPanel("detaljpanel", innboksVM));
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.getObject().oppdaterMeldinger(MeldingBuffer.getMeldinger());
        innboksVM.getObject().setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

    @RunOnEvents(WIDGET_HEADER_CLICKED)
    public void widgetHeaderClicked(AjaxRequestTarget target) {
        innboksVM.getObject().oppdaterMeldinger(MeldingBuffer.getMeldinger());
        target.add(this);
    }

}
