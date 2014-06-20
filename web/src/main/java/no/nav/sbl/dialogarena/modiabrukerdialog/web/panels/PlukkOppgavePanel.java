package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.Tema;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.PersonPage.OPPGAVEID;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class PlukkOppgavePanel extends Panel {

    private static final String TEMAGRUPPE_ATTR = "sos-temagruppe";

    @Inject
    private SakService sakService;

    public PlukkOppgavePanel(String id) {
        super(id);

        final IModel<Tema> valgtTema = new Model<>((Tema) getHttpSession().getAttribute(TEMAGRUPPE_ATTR));
        Form<Tema> form = new Form<>("plukk-oppgave-form", valgtTema);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback", new ContainerFeedbackMessageFilter(this));
        feedbackPanel.setOutputMarkupPlaceholderTag(true);

        AjaxSubmitLink plukkOppgave = new AjaxSubmitLink("plukk-oppgave") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Optional<Oppgave> oppgave = sakService.plukkOppgaveFraGsak(valgtTema.getObject().name());
                if (oppgave.isSome()) {
                    setResponsePage(PersonPage.class,
                            new PageParameters()
                                    .set("fnr", oppgave.get().getGjelder().getBrukerId())
                                    .set(OPPGAVEID, oppgave.get().getOppgaveId()));

                    getHttpSession().setAttribute(TEMAGRUPPE_ATTR, valgtTema.getObject());
                } else {
                    error(getString("plukkoppgave.ingenoppgaverpaatema"));
                    target.add(feedbackPanel);
                }
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        };
        RadioGroup radioGroup = new RadioGroup<>("tema", valgtTema);
        radioGroup.setRequired(true);
        radioGroup.add(new ListView<Tema>("temaer", asList(Tema.values())) {
            @Override
            protected void populateItem(ListItem<Tema> item) {
                item.add(new Radio<>("temavalg", item.getModel()));
                item.add(new Label("temanavn", getString(item.getModelObject().name())));
            }
        });
        form.add(plukkOppgave, radioGroup, feedbackPanel);

        add(form);
    }

    private HttpSession getHttpSession() {
        return ((HttpServletRequest) getRequestCycle().getRequest().getContainerRequest()).getSession();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(forReference(new JavaScriptResourceReference(PlukkOppgavePanel.class, "plukkoppgave.js")));
    }
}
