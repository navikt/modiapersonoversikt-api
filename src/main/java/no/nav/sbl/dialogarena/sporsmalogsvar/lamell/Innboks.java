package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.both;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Innboks extends Lerret {

    public static final JavaScriptResourceReference MELDINGER_JS = new JavaScriptResourceReference(Innboks.class, "meldinger.js");

    public static final String VALGT_MELDING_EVENT = "sos.innboks.valgt_melding";
    public static final String TRAAD_ID_PARAMETER_NAME = "henvendelseid";

    protected boolean harTilgang = true;

    private InnboksVM innboksVM;

    public Innboks(String id, String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVM = new InnboksVM(fnr);
        setDefaultModel(new CompoundPropertyModel<Object>(innboksVM));

        setValgtTraadBasertPaaTraadIdSessionParameter();

        PropertyModel<Boolean> harTraader = new PropertyModel<>(innboksVM, "harTraader");
        PropertyModel<Boolean> harTilgangModel = new PropertyModel<>(this, "harTilgang");

        AlleMeldingerPanel alleMeldingerPanel = new AlleMeldingerPanel("meldinger", innboksVM);
        alleMeldingerPanel.add(visibleIf(both(harTraader).and(harTilgangModel)));

        TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("detaljpanel", innboksVM);
        traaddetaljerPanel.add(visibleIf(both(harTraader).and(harTilgangModel)));

        WebMarkupContainer tilbakemeldingPanel = new WebMarkupContainer("tominnboksmelding");
        tilbakemeldingPanel.add(visibleIf(both(not(harTraader)).and(harTilgangModel)));

        WebMarkupContainer ikkeTilgangPanel = new WebMarkupContainer("ikketilgang");
        ikkeTilgangPanel.add(visibleIf(not(harTilgangModel)));

        add(alleMeldingerPanel, traaddetaljerPanel, tilbakemeldingPanel, ikkeTilgangPanel);
    }

    private void setValgtTraadBasertPaaTraadIdSessionParameter() {
        String traadIdParameter = ((String) getSession().getAttribute(TRAAD_ID_PARAMETER_NAME));
        if (!isBlank(traadIdParameter)) {
            Optional<MeldingVM> meldingITraad = innboksVM.getNyesteMeldingITraad(traadIdParameter);
            if (meldingITraad.isSome()) {
                innboksVM.setValgtMelding(meldingITraad.get());
            } else {
                harTilgang = false;
            }
            getSession().setAttribute(TRAAD_ID_PARAMETER_NAME, null);
        }
    }

    @Override
    public void onOpening(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        target.appendJavaScript("Meldinger.addKeyNavigation();");
        target.appendJavaScript("Meldinger.focusOnSelectedElement();");
        super.onOpening(target);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        innboksVM.setValgtMelding(feedItemPayload.getItemId());
        target.add(this);
    }

}
