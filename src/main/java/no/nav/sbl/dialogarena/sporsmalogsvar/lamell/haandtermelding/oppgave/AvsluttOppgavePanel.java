package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.oppgave;

import no.nav.modig.lang.option.Optional;
import no.nav.modig.wicket.component.indicatingajaxbutton.IndicatingAjaxButtonWithImageUrl;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService.OppgaveErFerdigstilt;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class AvsluttOppgavePanel extends Panel {

    @Inject
    private GsakService gsakService;

    private final TextArea<String> beskrivelseFelt;
    private final Model<Boolean> oppgaveAvsluttet = Model.of(false);

    public AvsluttOppgavePanel(String id, final Optional<String> oppgaveId) {
        super(id);
        setOutputMarkupPlaceholderTag(true);

        final WebMarkupContainer feedbackPanelSuccess = new WebMarkupContainer("feedbackAvsluttOppgave");
        feedbackPanelSuccess.setOutputMarkupPlaceholderTag(true);
        feedbackPanelSuccess.add(visibleIf(oppgaveAvsluttet));

        final FeedbackPanel feedbackPanelError = new FeedbackPanel("feedbackError");
        feedbackPanelError.setOutputMarkupId(true);

        beskrivelseFelt = new TextArea<>("beskrivelse", new Model<String>());
        beskrivelseFelt.setRequired(true);
        add(new Form("form")
                .add(beskrivelseFelt)
                .add(new IndicatingAjaxButtonWithImageUrl("avsluttoppgave", "../img/ajaxloader/svart/loader_svart_48.gif") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        try {
                            gsakService.ferdigstillGsakOppgave(oppgaveId, beskrivelseFelt.getModelObject());
                            oppgaveAvsluttet.setObject(true);
                            etterSubmit(target);
                            target.add(form, feedbackPanelSuccess);
                        } catch (LagreOppgaveOptimistiskLasing e) {
                            error(getString("avsluttoppgave.feil.opptimistisklaasing"));
                            onError(target, form);
                        } catch (OppgaveErFerdigstilt e) {
                            error(getString("avsluttoppgave.feil.oppgaveferdigstilt"));
                            onError(target, form);
                        } catch (Exception e) {
                            error(getString("avsluttoppgave.feil.teknisk"));
                            onError(target, form);
                        }
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        target.add(feedbackPanelError);
                    }
                })
                .add(feedbackPanelError)
                .add(visibleIf(not(oppgaveAvsluttet)))
                .setOutputMarkupId(true));

        add(feedbackPanelSuccess);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(OnDomReadyHeaderItem.forScript(format("$('#%s').val('%s');", beskrivelseFelt.getMarkupId(), getString("avsluttoppgave.standardbeskrivelse"))));
    }

    protected void etterSubmit(AjaxRequestTarget target) {
    }
}
