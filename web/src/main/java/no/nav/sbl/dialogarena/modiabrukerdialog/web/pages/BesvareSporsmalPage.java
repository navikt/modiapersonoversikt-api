package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class BesvareSporsmalPage extends WebPage {

    public BesvareSporsmalPage(PageParameters params) {
        Intern redirect = new Intern(params);
        send(redirect, Broadcast.BREADTH, new NamedEventPayload(Modus.BESVARE, params.get("oppgaveId").toString()));
        throw new RestartResponseAtInterceptPageException(redirect);
    }


}
