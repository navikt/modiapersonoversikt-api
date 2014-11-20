package no.nav.sbl.dialogarena.sak.lamell;

import no.nav.modig.core.exception.SystemException;
import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.modia.events.FeedItemPayload;
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

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static org.slf4j.LoggerFactory.getLogger;

public class SaksoversiktLerret extends Lerret {
    // Brukes av Modia (containeren):
    public static final PackageResourceReference SAKSOVERSIKT_LESS = new PackageResourceReference(SaksoversiktLerret.class, "saksoversikt.less");
    public static final ConditionalCssResource SAKSOVERSIKT_IE_CSS = new ConditionalCssResource(new CssResourceReference(SaksoversiktLerret.class, "saksoversikt-ie.css"), "screen", "IE");
    public static final JavaScriptResourceReference SAKSOVERSIKT_JS = new JavaScriptResourceReference(SaksoversiktLerret.class, "saksoversikt.js");
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
    private String fnr;

    public SaksoversiktLerret(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        add(new Label("se-saker-label", cms.hentTekst("detaljer.tittel")),
            new Label("viktig-aa-vite-label", cms.hentTekst("saksinformasjon.tittel")));
        instansierFeilmeldingContainer();
    }

    @Override
    protected void onBeforeRender() {
        feilmelding.setVisible(false);
        hentBehandlingerOgTemaer(fnr);
        temaContainer = lagTemaContainer();

        addOrReplace(
            lagDetaljerContainer(fnr),
            temaContainer,
            lagOppdaterLenke(this),
            feilmelding
        );

        if (getAktivtTema().getObject() == null) {
            aapneForsteItem();
        }
        super.onBeforeRender();
    }

    private void instansierFeilmeldingContainer() {
        feilmelding = new WebMarkupContainer("feilmelding")
                .add(new Label("feil", cms.hentTekst("baksystem.behandlinger.feil")))
                .setVisible(false)
                .setOutputMarkupPlaceholderTag(true);
    }

    private void hentBehandlingerOgTemaer(String fnr) {
        try {
            behandlingerByTema = saksoversiktService.hentBehandlingerByTema(fnr);
            temaer = on(behandlingerByTema.keySet()).collect(new SistOppdaterteBehandlingComparator());
        } catch (SystemException e) {
            LOG.error("Kunne ikke hente saker til lamellen", e);
            feilmelding.setVisible(true);
            if (temaer == null) {
                temaer = emptyList();
            }
        }
    }

    private AjaxLink lagOppdaterLenke(final SaksoversiktLerret lerret) {
        AjaxLink link = new AjaxLink("oppdater-innhold") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.add(lerret);
                target.appendJavaScript("stopRotation()");
            }
        };
        link.add(new Label("oppdater-label", cms.hentTekst("saksoversikt.oppdater.saker")));
        return link;
    }

    private WebMarkupContainer lagDetaljerContainer(String fnr) {
        WebMarkupContainer detaljerContainer = new WebMarkupContainer("detaljerContainer");
        Component behandlingerContainer = new BehandlingSakerListView("behandling-sak", temaer, fnr, this);
        detaljerContainer.add(behandlingerContainer);
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
        response.render(JavaScriptReferenceHeaderItem.forReference(NAVIGATION_JS));
        response.render(OnDomReadyHeaderItem.forScript("initSaksoversikt('" + temaContainer.getMarkupId() + "','" + INITIAL + "');"));
    }
}
