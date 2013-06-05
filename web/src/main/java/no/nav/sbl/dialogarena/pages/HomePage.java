package no.nav.sbl.dialogarena.pages;

import no.nav.kjerneinfo.hent.panels.HentPersonPanel;
import no.nav.kjerneinfo.web.pages.kjerneinfo.panel.kjerneinfo.PersonKjerneinfoPanel;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellFactory;
import no.nav.modig.modia.lamell.LamellPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.personsok.PersonsokPanel;
import no.nav.sbl.dialogarena.BasePage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.kjerneinfo.eventpayload.HentPerson.FODSELSNUMMER_FUNNET;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;


public class HomePage extends BasePage {
    public HomePage(PageParameters pageParameters) {
        final String fnrFromRequest = pageParameters.get("fnr").toString(null);

        add(
                new HentPersonPanel("searchPanel"),
                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                new PersonsokPanel("personsokPanel").setVisible(true)
                //                                new TokenLamellPanel("lameller", createLamellFactories(fnrFromRequest))
                //new SideBar("sideBar", fnrFromRequest).setVisible(true)
        );
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @RunOnEvents(FODSELSNUMMER_FUNNET)
    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
        throw new RestartResponseException(
                HomePage.class,
                new PageParameters().set("fnr", query)
        );
    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent event, FeedItemPayload feedItemPayload) {
        LamellPanel lameller = (LamellPanel) get("lameller");
        String lamellId = feedItemPayload.getWidgetId();
        lameller.goToLamell(lamellId);
        lameller.sendToLamell(lamellId, event.getPayload());
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {

        if ("saker".equals(linkId)) {
            LamellPanel lameller = (LamellPanel) get("lameller");
            lameller.goToLamell("sak");
        } else {
            target.appendJavaScript("alert('Lenke med id " + linkId + " klikket');");
        }
    }

    private List<LamellFactory> createLamellFactories(final String fnrFromRequest) {
        return asList(
                //                newLamellFactory("oversikt", "O", false, new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Oversikt(id);
                //                    }
                //                }),
                //                newLamellFactory("oppfolging", "P", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Oppfolging(id);
                //                    }
                //                }),
                //                newLamellFactory("kontrakter", "T", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new GenericLerret(id, new KontrakterPanel("panel", new Model<>("28105343770")));
                //                    }
                //                }),
                //
                //                newLamellFactory("foreldrepenger", "4", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new GenericLerret(id, new ForeldrepengerPanel("panel", new Model<>(fnrFromRequest)));
                //                    }
                //                }),
                //
                //                newLamellFactory("sykepenger", "5", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new GenericLerret(id, new SykmeldingsperiodePanel("panel", new Model<>(fnrFromRequest)));
                //                    }
                //                }),
                //
                //                newLamellFactory("oversikt2", "2", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Oversikt(id);
                //                    }
                //                }),
                //                newLamellFactory("oversikt3", "3", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Oversikt(id);
                //                    }
                //                }),
                //                newLamellFactory("feilhandtering", "F", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Feilhandtering(id);
                //                    }
                //                }),
                //                newLamellFactory("logg", "L", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new LoggLerret(id);
                //                    }
                //                }),
                //                newLamellFactory("dialog", "D", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new DialogLerret(id);
                //                    }
                //                }),
                //                newLamellFactory("soknad", "S", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new SoknadLerret(id);
                //                    }
                //                }),
                //                newLamellFactory("sak", "K", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new SakerLerret(id);
                //                    }
                //                })
        );
    }

}
