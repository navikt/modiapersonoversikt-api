package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.modig.modia.utils.ComponentFinder;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel.SaksbehandlerInnstillingerPanel;
import org.apache.commons.collections15.Closure;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.HashMap;
import java.util.Map;

public class FocusHandler {

    public static void handleEvent(Page page, IEvent<?> event) {
        if (event.getPayload().getClass() == String.class) {
            String payload = ((String) event.getPayload());
            Closure<Page> handler = EVENT_HANDLERS.get(payload);
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

    private static final Map<String, Closure<Page>> EVENT_HANDLERS = new HashMap<String, Closure<Page>>() {{
        put(SaksbehandlerInnstillingerPanel.SAKSBEHANDLERINNSTILLINGER_VALGT, FOKUS_SOK_FELT);
        put(LeggTilbakePanel.LEGG_TILBAKE_FERDIG, FOKUS_SOK_FELT);
        put(Events.SporsmalOgSvar.OPPGAVE_OPPRETTET_FERDIG, FOKUS_SOK_FELT);
        put(BasePage.SIDE_LASTET, FOKUS_PERSON_NAVN);
    }};
}
