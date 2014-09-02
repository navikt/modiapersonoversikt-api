package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.util.string.StringValue;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;

public class Innboks extends Lerret {

    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";
    public static final String TRAAD_ID_PARAMETER_NAME = "henvendelseid";

    private InnboksVM innboksVM;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVM = new InnboksVM(fnr);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));

        setValgtTraadBasertPaaTraadIdPageParameter();

        add(new AlleMeldingerPanel("meldinger", innboksVM), new TraaddetaljerPanel("detaljpanel", innboksVM));
    }

    private void setValgtTraadBasertPaaTraadIdPageParameter() {
        StringValue traadIdParameter = getRequestCycle().getRequest().getRequestParameters().getParameterValue(TRAAD_ID_PARAMETER_NAME);
        if (!traadIdParameter.isEmpty()) {
            Optional<MeldingVM> meldingITraad = innboksVM.getNyesteMeldingITraad(traadIdParameter.toString());
            if (meldingITraad.isSome()) {
                innboksVM.setValgtMelding(meldingITraad.get());
            }
        }
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        super.onOpening(target);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

}
