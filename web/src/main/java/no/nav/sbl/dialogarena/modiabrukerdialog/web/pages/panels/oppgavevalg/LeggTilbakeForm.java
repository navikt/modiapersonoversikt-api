package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

import javax.inject.Inject;

import no.nav.modig.lang.option.Optional;
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

import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg.Aarsak.INHABIL;

public class LeggTilbakeForm extends Form<AarsakVM> {

    @Inject
    OppgavebehandlingPortType service;

    public LeggTilbakeForm(String id, final Optional<String> oppgaveId) {
        super(id);
        final LeggTilbakeModell model = new LeggTilbakeModell(new AarsakVM(INHABIL));
        setDefaultModel(model);
        setOutputMarkupId(true);
        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        AjaxLink<Void> lukk = new AjaxLink<Void>("lukk") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                LeggTilbakeForm.this.setVisible(false);
                target.add(LeggTilbakeForm.this);
            }
        };
        final RadioGroup<Aarsak> valg = new RadioGroup<>("valg");
        valg.add(new Radio<Aarsak>("inhabil"));
        valg.add(new Radio<Aarsak>("annen"));

        TextArea<String> annenAarsakTekst = new TextArea<>("annenAarsakTekst");
        AjaxSubmitLink leggTilbake = new AjaxSubmitLink("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                String aarsak = model.getAarsakForTilbakeleggelse();
                if (aarsak != null) {
                    service.leggTilbakeOppgave(oppgaveId.get(), aarsak);
                    setResponsePage(HentPersonPage.class);
                } else {
                    info("Du må angi hvorfor du legger tilbake oppgaven");
                    target.add(feedback);
                }
            }
        };
        add(feedback, valg, lukk, annenAarsakTekst, leggTilbake);
    }
}
