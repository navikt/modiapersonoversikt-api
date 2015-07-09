package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel;
import org.apache.commons.collections15.Closure;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class FocusHandler {

    public static void handleEvent(Page page, IEvent<?> event) {
        if (event.getPayload().getClass() == String.class) {
            String payload = ((String) event.getPayload());
            Closure<Page> handler = eventHandlers.get(payload);
            if (handler != null) {
                handler.execute(page);
            }
        }
    }

    private static final Closure<Page> FOKUS_SOK_FELT = new Closure<Page>() {
        @Override
        public void execute(Page page) {
            AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
            TextField input = ComponentFinder.in(page).findWithId(TextField.class, "foedselsnummerInput");
            target.focusComponent(input);
        }
    };

    private static final Closure<Page> FOKUS_PERSON_NAVN = new Closure<Page>() {
        @Override
        public void execute(Page page) {
//        Venter på at visittkortet skal bli oppdatert
//        if (getPage().getClass() == PersonPage.class) {
//        Label label = ComponentFinder.in(getPage()).findWithId(Label.class, "personfakta.personnavn.fornavn");
//        target.focusComponent(label);
//        }
        }
    };

    private static final Map<String, Closure<Page>> eventHandlers = new HashMap<String, Closure<Page>>() {{
        put(SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT, FOKUS_SOK_FELT);
        put(LeggTilbakePanel.LEGG_TILBAKE_FERDIG, FOKUS_SOK_FELT);
        put(Events.SporsmalOgSvar.OPPGAVE_OPPRETTET_FERDIG, FOKUS_SOK_FELT);
        put(MerkePanel.TRAAD_MERKET, FOKUS_SOK_FELT);
        put(BasePage.SIDE_LASTET, FOKUS_PERSON_NAVN);
    }};

    static final class ComponentFinder {
        private final Page page;

        private ComponentFinder(Page page) {
            this.page = page;
        }

        public static ComponentFinder in(Page page) {
            return new ComponentFinder(page);
        }

        public <T extends Component> T findWithId(Class<T> type, final String id) {
            T t = page.visitChildren(type, new IVisitor<T, T>() {
                @Override
                public void component(T component, IVisit<T> iVisit) {
                    if (component.getId().equals(id)) {
                        iVisit.stop(component);
                    }
                }
            });
            if (t == null) {
                throw new ApplicationException(format("Fant ikke focus-element av typen %s med id %s", type.getName(), id));
            }
            return t;
        }

        public <T extends Component> T find(Class<T> type) {
            final List<T> list = new LinkedList<>();
            page.visitChildren(type, new IVisitor<T, T>() {
                @Override
                public void component(T component, IVisit<T> iVisit) {
                    list.add(component);
                }
            });
            if (list.size() > 1 || list.isEmpty()) {
                throw new ApplicationException(format("Fant %d elementer som matchet %s. Forventet bare ett element", list.size(), type.getName()));
            }
            return list.get(0);
        }
    }
}
