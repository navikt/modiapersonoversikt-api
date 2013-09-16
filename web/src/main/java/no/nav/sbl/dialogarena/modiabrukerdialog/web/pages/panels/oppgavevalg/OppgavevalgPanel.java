package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;

import static java.util.Arrays.asList;

public class OppgavevalgPanel extends Panel {

    public OppgavevalgPanel(String id, final String oppgaveId) {
        super(id);
        final LeggTilbakeForm leggTilbakeForm = new LeggTilbakeForm("legg-tilbake-form", oppgaveId);
        leggTilbakeForm.setVisible(false);
        leggTilbakeForm.setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer valg = new WebMarkupContainer("oppgavevalg-liste");
        valg.add(new AjaxLink<Void>("legg-tilbake-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                valg.setVisible(false);
                leggTilbakeForm.setVisible(true);
                target.add(valg, leggTilbakeForm);


            }
        });

        valg.setVisible(false);
        valg.setOutputMarkupPlaceholderTag(true);

        add(valg, leggTilbakeForm, new AjaxLink<Void>("oppgavevalg-link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                valg.setVisible(!valg.isVisible());
                leggTilbakeForm.setVisible(false);
                target.add(valg, leggTilbakeForm);
            }
        });
    }

    private static class LeggTilbakeForm extends Form<String> {

        @Inject
        OppgavebehandlingPortType service;

        public LeggTilbakeForm(String id, final String oppgaveId) {
            super(id);
            final RadioChoice<String> valg = new RadioChoice<>("valg", asList("Jeg er inhabil", "Annen årsak"));
            AjaxSubmitLink leggTilbake = new AjaxSubmitLink("submit") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    service.leggTilbakeOppgave(oppgaveId, valg.getModelObject());
                }
            };
            add(valg, leggTilbake);
        }
    }
}
