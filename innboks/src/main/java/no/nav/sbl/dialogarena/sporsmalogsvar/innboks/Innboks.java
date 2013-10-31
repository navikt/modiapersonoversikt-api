package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.meldinger.HentMeldingListe;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(Innboks.class, "innboks.js");

    @Inject
    HenvendelseMeldingerPortType service;


    private CompoundPropertyModel<InnboksVM> innboksVM;
    private String fnr;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.fnr = fnr;
        innboksVM = new CompoundPropertyModel<>(new InnboksVM(service.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding()));
        setDefaultModel(innboksVM);
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", innboksVM), new TraaddetaljerPanel("detaljpanel", fnr, innboksVM));
    }

    @RunOnEvents(KVITTERING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        innboksVM.getObject().oppdaterMeldinger(service.hentMeldingListe(new HentMeldingListe().withFodselsnummer(fnr)).getMelding());
        target.add(this);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.getObject().setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }
}
