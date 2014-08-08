package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.ArrayList;

import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;

public class SaksoversiktLerret extends Lerret {

    // Brukes av Modia (containeren):
    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");
    public static final JavaScriptResourceReference SAKSOVERSIKT_JS = new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt.js");

    @Inject
    private SaksoversiktService saksoversiktService;

    private WebMarkupContainer hendelserContainer;
    private String fnr;
    private IModel<String> aktivtTema = new Model<>();

    public SaksoversiktLerret(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        hendelserContainer = lagHendelserContainer(fnr);
        add(hendelserContainer, lagSakerContainer(fnr));
    }

    private WebMarkupContainer lagHendelserContainer(String fnr) {
        return (WebMarkupContainer) new WebMarkupContainer("hendelserContainer")
                .add(new BehandlingerListView("behandlinger", new ArrayList<GenerellBehandling>(), fnr)).setOutputMarkupPlaceholderTag(true);
    }

    private Component lagSakerContainer(String fnr) {
        return new WebMarkupContainer("sakerContainer")
                .add(new SakerListView("saker", fnr, this).setOutputMarkupPlaceholderTag(true));
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void filtrerDetaljerPaaValgtTema(AjaxRequestTarget target, FeedItemPayload payload) {
        hentNyeHendelser(payload.getItemId());
    }

    public void hentNyeHendelser(String sakstema) {
        aktivtTema.setObject(sakstema);
        hendelserContainer.addOrReplace(new BehandlingerListView("behandlinger", saksoversiktService.hentBehandlingerForTemakode(fnr, sakstema), fnr));
    }

    public IModel<String> getAktivtTema() {
        return aktivtTema;
    }
}
