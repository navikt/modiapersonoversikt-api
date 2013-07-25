package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.BrukerhenvendelserPanel;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.FORELDREPENGER;
import static no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl.SYKEPENGER;

public class LamellHandler implements Serializable {

    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_OVERSIKT = "oversikt";
    public static final String LAMELL_BRUKERPROFIL = "brukerprofil";
    public static final String LAMELL_BRUKERHENVENDELSER = "brukerhenvendelser";
    public static final String PANEL = "panel";

    private TokenLamellPanel lamellPanel;
    private String fnrFromRequest;
    private List<Lerret> lerretList = new ArrayList<>();

    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload) {
        final String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getType().toLowerCase();
        if (canHaveMoreThanOneFactory(type)) {
            lamellId = createFactoryIfMissing(type, feedItemPayload.getItemId());
        }
        if (lamellPanel.hasFactory(lamellId)) {
            lamellPanel.goToLamell(lamellId);
            lamellPanel.sendToLamell(lamellId, event.getPayload());
        } else {
            throw new ApplicationException("Feedlenke med ukjent type <" + lamellId + "> klikket');");
        }
    }

    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            lamellPanel.goToLamell(LAMELL_KONTRAKTER);
        } else {
            throw new ApplicationException("Widgetlenke med ukjent id <" + linkId + "> klikket');");
        }
    }

    public TokenLamellPanel createLamellPanel(String id, String fnrFromRequest) {
        this.fnrFromRequest = fnrFromRequest;
        lamellPanel = new TokenLamellPanel(id, createStaticLamellFactories());
        return lamellPanel;
    }

    private String createFactoryIfMissing(String type, String itemId) {
        String factoryId = type + itemId;
        if (!lamellPanel.hasFactory(factoryId)) {
            lamellPanel.addNewFactory(createFactory(type, itemId));
        }
        return factoryId;
    }

    private LamellFactory createFactory(String type, String itemId) {
        final Panel panel;
        if (SYKEPENGER.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel(PANEL, new Model<>(fnrFromRequest), new Model<>(itemId));
        } else if (FORELDREPENGER.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel(PANEL, new Model<>(fnrFromRequest), new Model<>(itemId));
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
        if (SYKEPENGER.equalsIgnoreCase(type)) {
            return true;
        }
        if (FORELDREPENGER.equalsIgnoreCase(type)) {
            return true;
        }
        return false;
    }

    private List<LamellFactory> createStaticLamellFactories() {
        return asList(
                createOversiktLamell(),
                createKontrakterLamell(),
                createBrukerprofilLamell(),
                createBrukerhenvendelserLamell()
        );
    }

    private LamellFactory createBrukerprofilLamell() {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new BrukerprofilPanel(id, new Model<>(fnrFromRequest)));
            }
        });
    }

    private LamellFactory createKontrakterLamell() {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new GenericLerret(id, new KontrakterPanel(PANEL, new Model<>(fnrFromRequest))));
            }
        });
    }

    private LamellFactory createOversiktLamell() {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new Oversikt(id, fnrFromRequest));
            }
        });
    }

    private LamellFactory createBrukerhenvendelserLamell() {
        return newLamellFactory(LAMELL_BRUKERHENVENDELSER, "H", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new BrukerhenvendelserPanel(id, fnrFromRequest));
            }
        });
    }

    private Lerret addLerretToListAndReturn(Lerret lerret) {
        lerretList.add(lerret);
        return lerret;
    }

    public boolean hasUnsavedChanges() {
        for (Lerret lerret : lerretList) {
            if (lerret.isModified()) {
                return true;
            }
        }
        return false;
    }

}
