package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.events.Events.KVITTERING;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference JS_REFERENCE = new JavaScriptResourceReference(Innboks.class, "innboks.js");
    private static final String SPORSMAL = "SPORSMAL";
    private static final String SVAR = "SVAR";

    @Inject
    HenvendelsePortType henvendelsesbehandling;

    private CompoundPropertyModel<InnboksVM> innboks;
    private String fnr;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.fnr = fnr;
        innboks = new CompoundPropertyModel<>(new InnboksVM(henvendelsesbehandling.hentHenvendelseListe(fnr, asList(SPORSMAL, SVAR))));
        setDefaultModel(innboks);
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", innboks), new TraaddetaljerPanel("detaljpanel", fnr, innboks));
    }

    @RunOnEvents(KVITTERING)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        innboks.getObject().oppdaterMeldinger(henvendelsesbehandling.hentHenvendelseListe(fnr, asList(SPORSMAL, SVAR)));
        target.add(this);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboks.getObject().setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }
}
