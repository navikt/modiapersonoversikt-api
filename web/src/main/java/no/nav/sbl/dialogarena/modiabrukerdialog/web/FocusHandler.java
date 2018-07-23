package no.nav.sbl.dialogarena.modiabrukerdialog.web;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.constants.Events;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.fortsettdialogpanel.LeggTilbakePanel;
import org.apache.commons.collections15.Closure;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.request.cycle.RequestCycle;

import java.util.HashMap;
import java.util.Map;

public class FocusHandler {

    public static final String JS_DEOKORATOR_SOKEFELT_HTML_ID = "js-deokorator-sokefelt";

    public static void handleEvent(Page page, IEvent<?> event) {
        if (event.getPayload().getClass() == String.class) {
            String payload = ((String) event.getPayload());
            Closure<Page> handler = EVENT_HANDLERS.get(payload);
            if (handler != null) {
                handler.execute(page);
            }
        }
    }
    private static final Closure<Page> FOKUS_SOK_FELT = page -> {
        AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        target.appendJavaScript(String.format("$('#%s').focus()", JS_DEOKORATOR_SOKEFELT_HTML_ID));
    };

    private static final Map<String, Closure<Page>> EVENT_HANDLERS = new HashMap<String, Closure<Page>>() {{
        put(LeggTilbakePanel.LEGG_TILBAKE_FERDIG, FOKUS_SOK_FELT);
        put(Events.SporsmalOgSvar.OPPGAVE_OPPRETTET_FERDIG, FOKUS_SOK_FELT);
    }};
}
