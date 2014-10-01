package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.core.exception.SystemException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.frontend.ConditionalJavascriptResource;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.navigation.KeyNavigationDependentResourceReference;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.service.BulletproofCmsService;
import no.nav.sbl.dialogarena.sak.service.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptContentHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_HEADER_CLICKED;
import static org.slf4j.LoggerFactory.getLogger;

public class SaksoversiktLerret extends Lerret {
    // Brukes av Modia (containeren):
    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");
    public static final ConditionalCssResource SAKSOVERSIKT_IE_CSS = new ConditionalCssResource(new CssResourceReference(SaksoversiktLerret.class, "saksoversikt-ie.css"), "screen", "IE");
    public static final JavaScriptResourceReference SAKSOVERSIKT_JS = new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt.js");
    public static final ConditionalJavascriptResource SAKSOVERSIKT_IE_JS = new ConditionalJavascriptResource(new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt-ie.js"), "IE");
    public static final KeyNavigationDependentResourceReference NAVIGATION_JS = new KeyNavigationDependentResourceReference(SaksoversiktLerret.class, "saksoversikt-navigation.js");
    private static final String INITIAL = "S";

    @Inject
    private SaksoversiktService saksoversiktService;

    @Inject
    private BulletproofCmsService cms;

    private static final Logger LOG = getLogger(SaksoversiktLerret.class);

    private Component temaContainer;
    private IModel<String> aktivtTema = new Model<>();
    private Map<TemaVM, List<GenerellBehandling>> behandlingerByTema = new HashMap<>();
    private List<TemaVM> temaer;
    private Component feilmelding;

    public SaksoversiktLerret(String id, String fnr) {
        super(id);

        instansierFeilmeldingContainer();
        hentBehandlinger(fnr);
        temaer = on(behandlingerByTema.keySet()).collect(new SistOppdaterteBehandlingComparator());
        temaContainer = lagTemaContainer();

        add(
                lagDetaljerContainer(fnr),
                temaContainer,
                lagOppdaterLenke(fnr, this),
                feilmelding);
        aapneForsteItem();
    }

    private void instansierFeilmeldingContainer() {
        feilmelding = new WebMarkupContainer("feilmelding")
                .add(new Label("feil", cms.hentTekst("baksystem.behandlinger.feil")))
                .setVisible(false)
                .setOutputMarkupPlaceholderTag(true);
    }

    private void hentBehandlinger(String fnr) {
        try {
            behandlingerByTema = saksoversiktService.hentBehandlingerByTema(fnr);
        } catch (SystemException e) {
            LOG.error("Kunne ikke hente saker til lamellen", e);
            feilmelding.setVisible(true);
        }
    }

    private AjaxLink lagOppdaterLenke(final String fnr, final SaksoversiktLerret lerret) {
        AjaxLink link = new AjaxLink("oppdater-innhold") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                try {
                    behandlingerByTema = saksoversiktService.hentBehandlingerByTema(fnr);
                    target.add(lerret);
                } catch (SystemException e) {
                    LOG.error("Kunne ikke oppdatere innhold i lamell", e);
                    feilmelding.setVisible(true);
                    target.add(feilmelding);
                }
            }
        };

        return link;
    }

    private WebMarkupContainer lagDetaljerContainer(String fnr) {
        WebMarkupContainer detaljerContainer = new WebMarkupContainer("detaljerContainer");
        Component behandlingerContainer = new BehandlingSakerListView("behandling-sak", temaer, fnr, this);
        detaljerContainer.add(
                behandlingerContainer
        );
        detaljerContainer.setOutputMarkupPlaceholderTag(true);
        return detaljerContainer;
    }

    private Component lagTemaContainer() {
        return new WebMarkupContainer("sakerContainer")
                .add(new TemaListView("saker", temaer, this).setOutputMarkupPlaceholderTag(true))
                .setOutputMarkupId(true);
    }

    @SuppressWarnings("unused")
    @RunOnEvents(FEED_ITEM_CLICKED)
    private void filtrerDetaljerPaaValgtTema(AjaxRequestTarget target, FeedItemPayload payload) {
        settAktivtTema(payload.getItemId());
        target.appendJavaScript("$(\".sak-navigering > UL > LI.aktiv > A\").focus();");
    }

    @SuppressWarnings("unused")
    @RunOnEvents(WIDGET_HEADER_CLICKED)
    private void onWidgetHeaderClicked(AjaxRequestTarget target, WidgetHeaderPayload payload) {
        aapneForsteItem();
    }

    private void aapneForsteItem() {
        if (!temaer.isEmpty()) {
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
        return this.behandlingerByTema.get(sakstema);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(new JavaScriptContentHeaderItem("resizeElement()", "saksoversikt-ie-js", "IE"));
        response.render(JavaScriptReferenceHeaderItem.forReference(NAVIGATION_JS));
        response.render(OnDomReadyHeaderItem.forScript("addOnClickListeners();"));
        response.render(OnDomReadyHeaderItem.forScript("new Modig.Modia.SaksoversiktView('#" + temaContainer.getMarkupId() + "','" + INITIAL + "');"));
    }
}
