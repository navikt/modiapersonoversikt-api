package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern;

import no.nav.modig.wicket.events.NamedEventPayload;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus.BESVARE;
import static org.apache.wicket.event.Broadcast.BREADTH;

public class InternBesvaremodus extends Intern {

    public InternBesvaremodus(PageParameters params) {
        super(params);
        send(this, BREADTH, new NamedEventPayload(BESVARE, params.get("oppgaveId").toString()));
    }

}
