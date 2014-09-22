package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.navigation.KeyNavigationDependentResourceReference;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;

public class SaksoversiktLerret extends Lerret {
    // Brukes av Modia (containeren):
    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");
    public static final ConditionalCssResource SAKSOVERSIKT_IE_CSS = new ConditionalCssResource(new CssResourceReference(SaksoversiktLerret.class, "saksoversikt-ie.css"), "screen", "IE");
    public static final JavaScriptResourceReference SAKSOVERSIKT_JS = new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt.js");
    public static final ConditionalJavascriptResource SAKSOVERSIKT_IE_JS = new ConditionalJavascriptResource(new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt-ie.js"), "IE");
    public static final KeyNavigationDependentResourceReference NAVIGATION_JS = new KeyNavigationDependentResourceReference(SaksoversiktLerret.class, "saksoversikt-navigation.js");
    private String initial = "S";

    @Inject
    private SaksoversiktService saksoversiktService;

    private Component temaContainer;
    private WebMarkupContainer hendelserContainer;
    private IModel<String> aktivtTema = new Model<>();
    private Label feilmelding = (Label) new Label("feilmelding", "Feil ved kall til baksystem").setVisible(false);
    private Map<TemaVM, List<GenerellBehandling>> alleBahandlinger;
    private List<TemaVM> temaer;

    public SaksoversiktLerret(String id, String fnr) {
        super(id);
        alleBahandlinger = saksoversiktService.hentAlleBehandlinger(fnr);
        temaer = on(alleBahandlinger.keySet()).collect(new SistOppdaterteBehandlingComparator());

        hendelserContainer = lagHendelserContainer(fnr);
        temaContainer = lagTemaContainer(fnr);
        add(hendelserContainer, temaContainer);
        aapneForsteItem();
    }

    private WebMarkupContainer lagHendelserContainer(String fnr) {
        return (WebMarkupContainer) new WebMarkupContainer("hendelserContainer")
                .add(feilmelding)
                .add(new BehandlingSakerListView("behandling-sak", temaer, fnr, this))
                .setOutputMarkupPlaceholderTag(true);
    }

    private Component lagTemaContainer(String fnr) {
        return new WebMarkupContainer("sakerContainer")
                .add(new TemaListView("saker", fnr, this).setOutputMarkupPlaceholderTag(true))
                .setOutputMarkupId(true);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void filtrerDetaljerPaaValgtTema(AjaxRequestTarget target, FeedItemPayload payload) {
        settAktivtTema(payload.getItemId());
    }

    @SuppressWarnings("unused")
    @RunOnEvents(WIDGET_HEADER_CLICKED)
    private void onWidgetHeaderClicked(AjaxRequestTarget target, WidgetHeaderPayload payload) {
        aapneForsteItem();
    }

    private void aapneForsteItem() {
        if(!temaer.isEmpty()) {
            settAktivtTema(temaer.get(0).temakode);
        }
    }

    public void settAktivtTema(String sakstema) {
        aktivtTema.setObject(sakstema);
    }

    public IModel<String> getAktivtTema() {
        return aktivtTema;
    }

    public List<GenerellBehandling> getBehandlingerForTema(TemaVM sakstema) {
        return this.alleBahandlinger.get(sakstema);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new JavaScriptContentHeaderItem("resizeElement()", "saksoversikt-ie-js", "IE"));
        response.render(JavaScriptReferenceHeaderItem.forReference(NAVIGATION_JS));
        response.render(OnDomReadyHeaderItem.forScript("new Modig.Modia.SaksoversiktView('#" + temaContainer.getMarkupId() + "','" + initial + "');"));
        response.render(OnLoadHeaderItem.forScript("addLamellTemaOnClickListeners();"));
        response.render(OnLoadHeaderItem.forScript("$(\".sak-navigering > UL > LI.aktiv > A\").focus();"));
    }
}
