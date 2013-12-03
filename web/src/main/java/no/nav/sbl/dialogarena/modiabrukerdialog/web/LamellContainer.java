package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.brukerprofil.BrukerprofilPanel;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellFactory;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.lamell.LerretFactory;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.GenericLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.Oversikt;
import no.nav.sbl.dialogarena.utbetaling.lamell.UtbetalingLamell;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import org.apache.commons.collections15.Predicate;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.FORELDREPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;
import static org.apache.wicket.model.Model.of;

/**
 * Holder på lameller og tilhørende lerreter
 */
public class LamellContainer extends TokenLamellPanel implements Serializable {

    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_UTBETALINGER = "utbetaling";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_OVERSIKT = "oversikt";
    public static final String LAMELL_BRUKERPROFIL = "brukerprofil";
    public static final String PANEL = "panel";

    private static LerretHolder lerretHolder = new LerretHolder();

    private final List<Lerret> lerretList = new ArrayList<>();
    private String fnrFromRequest;

    public LamellContainer(String id, String fnrFromRequest) {
        super(id, createLamellFactories(fnrFromRequest));
        this.fnrFromRequest = fnrFromRequest;
        lerretList.addAll(lerretHolder.lerreter);
    }

    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload) {
        String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getType().toLowerCase();
        if (canHaveMoreThanOneFactory(type)) {
            lamellId = createFactoryIfMissing(type, feedItemPayload.getItemId());
        }
        if (hasFactory(lamellId)) {
            goToLamell(lamellId);
            sendToLamell(lamellId, event.getPayload());
        } else {
            throw new ApplicationException("Feedlenke med ukjent type <" + lamellId + "> klikket");
        }
    }

    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            goToLamell(LAMELL_KONTRAKTER);
        } else if (LAMELL_UTBETALINGER.equalsIgnoreCase(linkId)){
            goToLamell(LAMELL_UTBETALINGER);
        } else {
            throw new ApplicationException("Widgetlenke med ukjent id <" + linkId + "> klikket');");
        }
    }

    public boolean hasUnsavedChanges() {
        return on(lerretList).exists(MODIFIED_LERRET);
    }

    private String createFactoryIfMissing(String type, String itemId) {
        String factoryId = type + itemId;
        if (!hasFactory(factoryId)) {
            addNewFactory(createFactory(type, itemId));
        }
        return factoryId;
    }

    private LamellFactory createFactory(String type, String itemId) {
        final Panel panel;
        if (SYKEPENGER.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel(PANEL, of(fnrFromRequest), of(itemId));
        } else if (FORELDREPENGER.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel(PANEL, of(fnrFromRequest), of(itemId));
        } else {
            throw new ApplicationException("Ukjent type panel: " + type);
        }

        return newLamellFactory(type, itemId, "", true, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new GenericLerret(id, panel);
            }
        });
    }

    private boolean canHaveMoreThanOneFactory(String type) {
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
                return addLerretToListAndReturn(new BrukerprofilPanel(id, of(fnrFromRequest)));
            }
        });
    }

    private static LamellFactory createKontrakterLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new GenericLerret(id, new KontrakterPanel(PANEL, of(fnrFromRequest))));
            }
        });
    }

    private static LamellFactory createOversiktLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new Oversikt(id, fnrFromRequest));
            }
        });
    }

    private static LamellFactory createUtbetalingLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_UTBETALINGER, "U", true, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new UtbetalingLamell(id, fnrFromRequest));
            }
        });
    }

    private static Lerret addLerretToListAndReturn(Lerret lerret) {
        lerretHolder.lerreter.add(lerret);
        return lerret;
    }

    private static final Predicate<Lerret> MODIFIED_LERRET = new Predicate<Lerret>() {
        @Override
        public boolean evaluate(Lerret lerret) {
            return lerret.isModified();
        }
    };

    private static class LerretHolder {
        List<Lerret> lerreter;

        LerretHolder() {
            this.lerreter = new ArrayList<>();
        }
    }

}
