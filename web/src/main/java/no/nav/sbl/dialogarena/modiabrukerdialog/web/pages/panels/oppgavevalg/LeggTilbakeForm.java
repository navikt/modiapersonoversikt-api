package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.Modus;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.Aarsak.INHABIL;

public class LeggTilbakeForm extends Form<AarsakVM> {

    @Inject
    OppgavebehandlingPortType service;

    private String oppgaveId;

    public LeggTilbakeForm(String id) {
        super(id);
        setModel(new CompoundPropertyModel<>(new AarsakVM(INHABIL)));
        setOutputMarkupId(true);
        final FeedbackPanel feedback = (FeedbackPanel) new FeedbackPanel("feedback").setOutputMarkupId(true);
        add(
                feedback,
                createAarsakValg(),
                createLukkLink(),
                new TextArea<String>("annenAarsakTekst"),
                createSubmitLink(feedback)
        );
    }

    private AjaxSubmitLink createSubmitLink(final FeedbackPanel feedback) {
        return new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                String aarsak = getModelObject().getAarsakForTilbakeleggelse();
                if (aarsak != null) {
                    service.leggTilbakeOppgave(oppgaveId, aarsak);
                    setResponsePage(HentPersonPage.class);
                } else {
                    info("Du må angi hvorfor du legger tilbake oppgaven");
                    target.add(feedback);
                }
            }
        };
    }

    private RadioGroup<Aarsak> createAarsakValg() {
        RadioGroup<Aarsak> valg = new RadioGroup<>("valg");
        valg.add(new Radio<Aarsak>("inhabil"));
        valg.add(new Radio<Aarsak>("annen"));
        return valg;
    }

    private AjaxLink<Void> createLukkLink() {
        return new AjaxLink<Void>("lukk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LeggTilbakeForm.this.setVisibilityAllowed(false);
                target.add(LeggTilbakeForm.this);
            }
        };
    }

    @RunOnEvents(Modus.BESVARE)
    public void setOppgaveId(AjaxRequestTarget target, String oppgaveId) {
        this.oppgaveId = oppgaveId;
    }

}
