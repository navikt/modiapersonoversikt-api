package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

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
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetServiceImpl;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;

public class LamellHandler implements Serializable {

    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_OVERSIKT = "oversikt";
    public static final String LAMELL_BRUKERPROFIL = "brukerprofil";
    private TokenLamellPanel lamellPanel;
    private String fnrFromRequest;

    public LamellHandler() {

    }

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

    private String createFactoryIfMissing(String type, String itemId) {
        String factoryId = type + itemId;
        if (!lamellPanel.hasFactory(factoryId)) {
            lamellPanel.addNewFactory(createFactory(type, itemId));
        }
        return factoryId;
    }

    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            lamellPanel.goToLamell(LAMELL_KONTRAKTER);
        } else {
            throw new ApplicationException("Widgetlenke med ukjent id <" + linkId + "> klikket');");
        }
    }

    public TokenLamellPanel createLamellPanel(String lameller, String fnrFromRequest) {
        this.fnrFromRequest = fnrFromRequest;
        lamellPanel = new TokenLamellPanel(lameller, createLamellFactories());
        return lamellPanel;
    }

    private LamellFactory createFactory(String type, String itemId) {
        final Panel panel;
        if (SykepengerWidgetServiceImpl.SYKEPENGER.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel("panel", new Model<>(fnrFromRequest), new Model<>(itemId));
        } else if (SykepengerWidgetServiceImpl.FORELDREPENGER.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel("panel", new Model<>(fnrFromRequest), new Model<>(itemId));
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
        if (SykepengerWidgetServiceImpl.SYKEPENGER.equalsIgnoreCase(type)) {
            return true;
        }
        if (SykepengerWidgetServiceImpl.FORELDREPENGER.equalsIgnoreCase(type)) {
            return true;
        }
        return false;
    }

    private List<LamellFactory> createLamellFactories() {
        return asList(
                newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new Oversikt(id, fnrFromRequest);
                    }
                }),
                newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new GenericLerret(id, new KontrakterPanel("panel", new Model<>(fnrFromRequest)));
                    }
                }),
                newLamellFactory(LAMELL_BRUKERPROFIL, "B", new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new GenericLerret(id, new BrukerprofilPanel("panel", new Model<>(fnrFromRequest)));
                    }
                })
        );
    }

    public boolean hasUnsavedChanges() {
        return true;
    }
}
