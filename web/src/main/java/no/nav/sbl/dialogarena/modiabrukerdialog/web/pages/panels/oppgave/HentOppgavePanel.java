package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class HentOppgavePanel extends Panel {

    @Inject
    OppgavebehandlingPortType service;

    public HentOppgavePanel(String id) {
        super(id);
        add(new Link("plukk-oppgave") {
            @Override
            public void onClick() {
                WSPlukkOppgaveResultat oppgaveResultat = service.plukkOppgave("tema");
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("fnr", oppgaveResultat.getFodselsnummer());
                pageParameters.add("oppgaveId", oppgaveResultat.getOppgaveId());
                setResponsePage(Intern.class, pageParameters);
            }
        });
    }

}
