package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class InternBesvaremodus extends Intern {

    public InternBesvaremodus(PageParameters params) {
        super(params);
        send(this, Broadcast.BREADTH, new NamedEventPayload(Modus.BESVARE, params.get("oppgaveId").toString()));
    }

}
