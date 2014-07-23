package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.plukkoppgavepanel;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.OPPGAVEID;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class PlukkOppgavePanel extends Panel {

    public static final String TEMAGRUPPE_ATTR = "sos-temagruppe";

    @Inject
    private SakService sakService;

    public PlukkOppgavePanel(String id) {
        super(id);

        final IModel<Temagruppe> valgtTemagruppe = new Model<>((Temagruppe) getSession().getAttribute(TEMAGRUPPE_ATTR));
        Form<Temagruppe> form = new Form<>("plukk-oppgave-form", valgtTemagruppe);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        AjaxButton plukkOppgave = new AjaxButton("plukk-oppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Optional<WSOppgave> oppgave = sakService.plukkOppgaveFraGsak(valgtTemagruppe.getObject().name());
                if (oppgave.isSome()) {
                    setResponsePage(PersonPage.class,
                            new PageParameters()
                                    .set("fnr", oppgave.get().getGjelder().getBrukerId())
                                    .set(OPPGAVEID, oppgave.get().getOppgaveId()));

                    getSession().setAttribute(TEMAGRUPPE_ATTR, valgtTemagruppe.getObject());
                } else {
                    error(getString("plukkoppgave.ingenoppgaverpaatemagruppe"));
                    target.add(feedbackPanel);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
        RadioGroup radioGroup = new RadioGroup<>("temagruppe", valgtTemagruppe);
        radioGroup.setRequired(true);
        radioGroup.add(new ListView<Temagruppe>("temagrupper", asList(Temagruppe.values())) {
            @Override
            protected void populateItem(ListItem<Temagruppe> item) {
                item.add(new Radio<>("temagruppevalg", item.getModel()));
                item.add(new Label("temagruppenavn", new ResourceModel(item.getModelObject().name())));
            }
        });
        form.add(plukkOppgave, radioGroup, feedbackPanel);

        add(form);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(PlukkOppgavePanel.class, "plukkoppgave.js")));
    }
}
