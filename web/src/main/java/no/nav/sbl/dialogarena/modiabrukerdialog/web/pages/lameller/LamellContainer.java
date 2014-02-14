package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller;

import no.nav.brukerprofil.BrukerprofilPanel;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.LamellPayload;
import no.nav.modig.modia.events.WidgetHeaderPayload;
import no.nav.modig.modia.lamell.LamellFactory;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.lamell.LerretFactory;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.OversiktLerret;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLerret;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.FORELDREPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;

/**
 * Holder på lameller og tilhørende lerreter
 */
public class LamellContainer extends TokenLamellPanel implements Serializable {

    private Logger logger = LoggerFactory.getLogger(LamellContainer.class);

    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_UTBETALINGER = "utbetalinger";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_OVERSIKT = "oversikt";
    public static final String LAMELL_BRUKERPROFIL = "brukerprofil";
    public static final String PANEL = "panel";

    private String fnrFromRequest;

    public LamellContainer(String id, String fnrFromRequest) {
        super(id, createLamellFactories(fnrFromRequest));
        this.fnrFromRequest = fnrFromRequest;
    }

    public void handleLamellLinkClicked(LamellPayload lamellPayload) {
        gotoAndSendToLamell(lamellPayload.lamellId.toLowerCase(), lamellPayload);
    }

    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload) {
        String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getWidgetId().toLowerCase();

        if (canHaveMoreThanOneLamell(type)) {
            lamellId = createLamellIfMissing(type, feedItemPayload.getItemId());
        }

        gotoAndSendToLamell(lamellId, event.getPayload());
    }

    public void handleWidgetHeaderEvent(IEvent<?> event, WidgetHeaderPayload widgetHeaderPayload) {
        gotoAndSendToLamell(widgetHeaderPayload.getType().toLowerCase(), event.getPayload());
    }

    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            goToLamell(LAMELL_KONTRAKTER);
        } else {
            ApplicationException exc = new ApplicationException("Feedlenke med ukjent type <" + linkId + "> klikket");
            logger.warn("ukjent widgetId: {}", linkId, exc);
            throw exc;
        }
    }

    public boolean hasUnsavedChanges() {
        return on(getLameller()).exists(IS_LAMELL_MODIFIED);
    }

    private String createLamellIfMissing(String type, String itemId) {
        String factoryId = type + itemId;
        if (!hasFactory(factoryId)) {
            addNewFactory(createFactory(type, itemId));
        }
        return factoryId;
    }

    private LamellFactory createFactory(String type, String itemId) {
        final Panel panel;
        if (SYKEPENGER.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
        } else if (FORELDREPENGER.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel(PANEL, Model.of(fnrFromRequest), Model.of(itemId));
        } else {
            ApplicationException exc = new ApplicationException("Ukjent type lerret: " + type);
            logger.warn("ukjent lerret: {}", type, exc);
            throw exc;
        }

        return newLamellFactory(type, itemId, "", true, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new GenericLerret(id, panel);
            }
        });
    }

    private static boolean canHaveMoreThanOneLamell(String type) {
        return SYKEPENGER.equalsIgnoreCase(type) || FORELDREPENGER.equalsIgnoreCase(type);
    }

    private static List<LamellFactory> createLamellFactories(final String fnrFromRequest) {
        return asList(
                createOversiktLamell(fnrFromRequest),
                createKontrakterLamell(fnrFromRequest),
                createBrukerprofilLamell(fnrFromRequest),
                createUtbetalingLamell(fnrFromRequest)
        );
    }

    private static LamellFactory createBrukerprofilLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new BrukerprofilPanel(id, Model.of(fnrFromRequest));
            }
        });
    }

    private static LamellFactory createKontrakterLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new GenericLerret(id, new KontrakterPanel(PANEL, Model.of(fnrFromRequest)));
            }
        });
    }

    private static LamellFactory createOversiktLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new OversiktLerret(id, fnrFromRequest);
            }
        });
    }

    private static LamellFactory createUtbetalingLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_UTBETALINGER, "U", true, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new UtbetalingLerret(id, fnrFromRequest);
            }
        });
    }

    private static final Predicate<Lamell> IS_LAMELL_MODIFIED = new Predicate<Lamell>() {
        @Override
        public boolean evaluate(Lamell lamell) {
            return lamell.isModified();
        }
    };

    private void gotoAndSendToLamell(String lamellId, Object payload) {
        if (hasFactory(lamellId)) {
            goToLamell(lamellId);
            sendToLamell(lamellId, payload);
        } else {
            ApplicationException exc = new ApplicationException("Ukjent lamellId <" + lamellId + "> klikket");
            logger.warn("ukjent lamellId: {}", lamellId, exc);
            throw exc;
        }
    }

}
