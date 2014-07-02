package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sak.config.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;

public class SaksoversiktLerret extends Lerret {

    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");

    @Inject
    private SaksoversiktService saksoversiktService;

    private WebMarkupContainer hendelserContainer;
    private String fnr;

    public SaksoversiktLerret(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        hendelserContainer = (WebMarkupContainer) new WebMarkupContainer("hendelserContainer")
                .setOutputMarkupPlaceholderTag(true);

        add(new Label("saksoversikt.fnr", fnr));
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    private void filtrerDetaljerPaaValgtTema(AjaxRequestTarget target, FeedItemPayload payload) {
        List<GenerellBehandling> behandlinger = saksoversiktService.hentBehandlingerForTemakode(fnr, payload.getItemId());
    }
}
