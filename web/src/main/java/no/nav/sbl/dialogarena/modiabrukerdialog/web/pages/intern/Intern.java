package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.dialogarena.modiabrukerdialog.example.component.ExamplePanel;
import no.nav.kjerneinfo.kontrakter.KontrakterPanel;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.lamell.LamellFactory;
import no.nav.modig.modia.lamell.LamellPanel;
import no.nav.modig.modia.lamell.Lerret;
import no.nav.modig.modia.lamell.LerretFactory;
import no.nav.modig.modia.lamell.TokenLamellPanel;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.BasePage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.GenericLerret;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.lameller.oversikt.Oversikt;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.sidebar.SideBar;
import no.nav.sykmeldingsperioder.SykmeldingsperiodePanel;
import no.nav.sykmeldingsperioder.foreldrepenger.ForeldrepengerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.modia.events.InternalEvents.FEED_ITEM_CLICKED;
import static no.nav.modig.modia.events.InternalEvents.WIDGET_LINK_CLICKED;
import static no.nav.modig.modia.lamell.DefaultLamellFactory.newLamellFactory;

public class Intern extends BasePage {

    private static final String FNR = "fnr";
    private static final String LAMELLER = "lameller";
    private static final String SAKER = "saker";
    private static final String SAK = "sak";
    public static final String LAMELL_KONTRAKTER = "kontrakter";
    public static final String LAMELL_FORELDREPENGER = "foreldrepenger";
    public static final String LAMELL_SYKEPENGER = "sykepenger";
    public static final String LAMELL_EXAMPLE = "example";
    public static final String LAMELL_OVERSIKT = "oversikt";


    public Intern(PageParameters pageParameters) {
        final String fnrFromRequest = pageParameters.get("fnr").toString(null);

        add(
                //                new HentPersonPanel("searchPanel"),
                //                new PersonKjerneinfoPanel("personKjerneinfoPanel", fnrFromRequest).setVisible(true),
                //                new PersonsokPanel("personsokPanel").setVisible(true),
                new TokenLamellPanel("lameller", createLamellFactories(fnrFromRequest)),
                new SideBar("sideBar", fnrFromRequest).setVisible(true)
        );
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    //    @RunOnEvents(FODSELSNUMMER_FUNNET)
    //    public void refreshKjerneinfo(AjaxRequestTarget target, String query) {
    //        throw new RestartResponseException(
    //                Intern.class,
    //                new PageParameters().set(FNR, query)
    //        );
    //    }

    @RunOnEvents(FEED_ITEM_CLICKED)
    public void feedItemClicked(AjaxRequestTarget target, IEvent<?> event, FeedItemPayload feedItemPayload) {
        final LamellPanel lameller = (LamellPanel) get(LAMELLER);
        try{
            EventRouter.handleFeedItemEvent(lameller, event, feedItemPayload);
        }catch(ApplicationException e){
            target.appendJavaScript("alert('" + e.getMessage()  +"');");
        }
    }

    @RunOnEvents(WIDGET_LINK_CLICKED)
    public void widgetLinkClicked(AjaxRequestTarget target, String linkId) {
        final LamellPanel lameller = (LamellPanel) get(LAMELLER);
        try{
            EventRouter.handleWidgetItemEvent(lameller, linkId);
        }catch(ApplicationException e){
            target.appendJavaScript("alert('" + e.getMessage()  +"');");
        }

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
                })
//                newLamellFactory(LAMELL_KONTRAKTER, "T", new LerretFactory() {
//                    @Override
//                    public Lerret createLerret(String id) {
//                        return new GenericLerret(id, new KontrakterPanel("panel", new Model<>("28105343770")));
//                    }
//                }),
//                newLamellFactory(LAMELL_FORELDREPENGER, "4", new LerretFactory() {
//                    @Override
//                    public Lerret createLerret(String id) {
//                        return new GenericLerret(id, new ForeldrepengerPanel("panel", new Model<>(fnrFromRequest), new Model<String>()));
//                    }
//                }),
//                newLamellFactory(LAMELL_SYKEPENGER, "5", new LerretFactory() {
//                    @Override
//                    public Lerret createLerret(String id) {
//                        return new GenericLerret(id, new SykmeldingsperiodePanel("panel", new Model<>(fnrFromRequest), new Model<String>()));
//                    }
//                })
                //                newLamellFactory("oppfolging", "P", new LerretFactory() {
                //                    @Override
                //                    public Lerret createLerret(String id) {
                //                        return new Oppfolging(id);
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
