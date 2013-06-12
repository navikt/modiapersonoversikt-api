package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel;
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
    public static final String LAMELL_EXAMPLE = "example";
    public static final String LAMELL_OVERSIKT = "oversikt";
    private TokenLamellPanel lamellPanel;
    private String fnrFromRequest;

    public LamellHandler() {

    }



    public void handleFeedItemEvent(IEvent<?> event, FeedItemPayload feedItemPayload) {
        final String type = feedItemPayload.getType().toLowerCase();
        String lamellId = feedItemPayload.getType().toLowerCase();
        if (canHaveMoreThanOneFactory(type)) {
            String itemId = feedItemPayload.getItemId();
            String factoryId = type + itemId;
            if(!lamellPanel.hasFactory(factoryId)){
                lamellPanel.addNewFactory(createFactory(type, itemId));
            }
            lamellId = type + itemId;
        }
        if(lamellPanel.hasFactory(lamellId)){
            lamellPanel.goToLamell(lamellId);
            lamellPanel.sendToLamell(lamellId, event.getPayload());
        }else{
            throw new ApplicationException("Feedlenke med ukjent type <" + lamellId + "> klikket');");
        }
    }
    public void handleWidgetItemEvent(String linkId) {
        if (LAMELL_KONTRAKTER.equals(linkId)) {
            lamellPanel.goToLamell(LAMELL_KONTRAKTER);
        } else {
            throw new ApplicationException("Widgetlenke med ukjent id <" + linkId + "> klikket');");
        }
    }


    public TokenLamellPanel createLamellPanel(String lameller, String fnrFromRequest) {
        this.fnrFromRequest = fnrFromRequest;
        return lamellPanel = new TokenLamellPanel(lameller, createLamellFactories(fnrFromRequest));
    }

    private LamellFactory createFactory(String type, String itemId){
        final Panel panel;
        if (SykepengerWidgetServiceImpl.SYKEPENGER.toLowerCase().equals(type)){
            panel = new SykmeldingsperiodePanel("panel", new Model<>(fnrFromRequest), new Model<>(itemId));
        } else if (SykepengerWidgetServiceImpl.FORELDREPENGER.toLowerCase().equals(type)) {
            panel = new ForeldrepengerPanel("panel", new Model<>(fnrFromRequest), new Model<>(itemId));
        } else {
            throw new RuntimeException("Unknown type in payload: " + type );
        }

        return newLamellFactory(type,itemId, "", true, new LerretFactory() {
            @Override
            public Lerret createLerret(String id) {
                return new GenericLerret(id, panel);
            }
        });
    }

    private boolean canHaveMoreThanOneFactory(String type) {
        if (SykepengerWidgetServiceImpl.SYKEPENGER.toLowerCase().equals(type)) {
            return true;
        }
        if (SykepengerWidgetServiceImpl.FORELDREPENGER.toLowerCase().equals(type)) {
            return true;
        }
        return false;
    }

    private List<LamellFactory> createLamellFactories(final String fnrFromRequest) {
        return asList(
                newLamellFactory(LAMELL_OVERSIKT, "O", false, new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new Oversikt(id);
                    }
                }),
                newLamellFactory(LAMELL_EXAMPLE, "", true, new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new GenericLerret(id, new ExamplePanel("panel"));
                    }
                }),
                newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
                    @Override
                    public Lerret createLerret(String id) {
                        return new GenericLerret(id, new KontrakterPanel("panel", new Model<>("28105343770")));
                    }
                })
        );
    }

}
