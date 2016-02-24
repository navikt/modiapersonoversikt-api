package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.frontend.FrontendModule;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.reactkomponenter.ResourceReference;
import no.nav.sbl.dialogarena.reactkomponenter.utils.wicket.ReactComponentPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.resource.PackageResourceReference;

import java.util.HashMap;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;


public class SaksoversiktLerret extends Lerret {
    public static final FrontendModule RESOURCES = new FrontendModule.With()
            .less(new PackageResourceReference(ResourceReference.class, "build/saksoversikt-module.less"))
            .done();
    private final ReactComponentPanel lerret;

    public SaksoversiktLerret(String id, final String fnr) {
        super(id);

        lerret = new ReactComponentPanel("saksoversiktLerret", "SaksoversiktLerret", new HashMap<String, Object>() {{
            put("fnr", fnr);
        }});

        add(lerret);
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        String tema = feedItemPayload.getItemId();
        //TODO lerret.call("visTema", tema);
    }
}

