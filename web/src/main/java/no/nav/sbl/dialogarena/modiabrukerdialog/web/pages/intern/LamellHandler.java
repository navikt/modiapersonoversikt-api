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
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final String PANEL = "panel";


    private Map<String, TokenLamellPanel> lamellPanelMap = new HashMap<>();

    //private TokenLamellPanel lamellPanel;
//    private String fnrFromRequest;
    private List<Lerret> lerretList = new ArrayList<>();

    private TokenLamellPanel getTokenLamellPanelForFnr(String fnr){
        if(!lamellPanelMap.containsKey(fnr)){
            throw new ApplicationException("TokenLamellPanel for ukjent fnr <" + fnr + "> forsøkt funnet");
        }
        return lamellPanelMap.get(fnr);
    }

    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload, String fnr) {
        TokenLamellPanel panel = getTokenLamellPanelForFnr(fnr);
        final String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getType().toLowerCase();
        if (canHaveMoreThanOneFactory(type)) {
            lamellId = createFactoryIfMissing(panel, type, feedItemPayload.getItemId(), fnr);
        }
        if (panel.hasFactory(lamellId)) {
            panel.goToLamell(lamellId);
            panel.sendToLamell(lamellId, event.getPayload());
        } else {
            throw new ApplicationException("Feedlenke med ukjent type <" + lamellId + "> klikket");
        }
    }

    public void handleWidgetItemEvent(String linkId, String fnr) {
        TokenLamellPanel panel = getTokenLamellPanelForFnr(fnr);
        if (LAMELL_KONTRAKTER.equalsIgnoreCase(linkId)) {
            panel.goToLamell(LAMELL_KONTRAKTER);
        } else {
            throw new ApplicationException("Widgetlenke med ukjent id <" + linkId + "> klikket');");
        }
    }

    public TokenLamellPanel createLamellPanel(String id, String fnrFromRequest) {
        if(lamellPanelMap.containsKey(fnrFromRequest)){
            return lamellPanelMap.get(fnrFromRequest);
        }
        TokenLamellPanel tokenLamellPanel = new TokenLamellPanel(id, createStaticLamellFactories(fnrFromRequest));
        lamellPanelMap.put(fnrFromRequest, tokenLamellPanel);
        return tokenLamellPanel;
    }

    private String createFactoryIfMissing(TokenLamellPanel panel, String type, String itemId,String fnr) {
        String factoryId = type + itemId;
        if (!panel.hasFactory(factoryId)) {
            panel.addNewFactory(createFactory(type, itemId, fnr));
        }
        return factoryId;
    }

    private LamellFactory createFactory(String type, String itemId, String fnr) {
        final Panel panel;
        if (SYKEPENGER.equalsIgnoreCase(type)) {
            panel = new SykmeldingsperiodePanel(PANEL, new Model<>(fnr), new Model<>(itemId));
        } else if (FORELDREPENGER.equalsIgnoreCase(type)) {
            panel = new ForeldrepengerPanel(PANEL, new Model<>(fnr), new Model<>(itemId));
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

    private List<LamellFactory> createStaticLamellFactories(String fnr) {
        return asList(
                createOversiktLamell(fnr),
                createKontrakterLamell(fnr),
                createBrukerprofilLamell(fnr)
        );
    }

    private LamellFactory createBrukerprofilLamell(final String fnrFromRequest) {
        return newLamellFactory(LAMELL_BRUKERPROFIL, "B", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new BrukerprofilPanel(id, new Model<>(fnrFromRequest)));
            }
        });
    }

    private LamellFactory createKontrakterLamell(final String fnr) {
        return newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new GenericLerret(id, new KontrakterPanel(PANEL, new Model<>(fnr))));
            }
        });
    }

    private LamellFactory createOversiktLamell(final String fnr) {
        return newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return addLerretToListAndReturn(new Oversikt(id, fnr));
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
