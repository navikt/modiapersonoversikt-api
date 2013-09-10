package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.Arrays;

public class HentOppgavePanel extends Panel implements Temavelgerdelegat {

    @Inject
    OppgavebehandlingPortType service;
    private ModigModalWindow temavelger;

    public HentOppgavePanel(String id) {
        super(id);
        temavelger = new ModigModalWindow("temavelger");
        temavelger.setContent(new VelgTemaPanel(temavelger.getContentId(), Arrays.asList("Dagpenger", "Sykepenger", "Annet"), HentOppgavePanel.this));
        add(new AjaxLink("plukk-oppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                temavelger.show(target);
            }
        });
        add(temavelger);
    }

    @Override
    public void valgteTema(String tema) {
        WSPlukkOppgaveResultat oppgaveResultat = service.plukkOppgave(tema);
        if (oppgaveResultat == null) {
            return;
        }

        PageParameters pageParameters = new PageParameters();
        pageParameters.add("fnr", oppgaveResultat.getFodselsnummer());
        pageParameters.add("oppgaveId", oppgaveResultat.getOppgaveId());
        setResponsePage(Intern.class, pageParameters);
    }
}
