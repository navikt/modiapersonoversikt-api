package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.navenhetpanel;

import no.nav.modig.core.context.SubjectHandler;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import java.util.List;

import static java.util.Arrays.asList;

public class NAVEnhetPanel extends Panel {

    private Kontor valgtKontor;

    public NAVEnhetPanel(String id) {
        super(id);

        List<Kontor> kontorer = asList(new Kontor("Sagene"), new Kontor("Grunerløkka"), new Kontor("Schous Plass"));

        RadioGroup<Kontor> gruppe = new RadioGroup<>("kontor", new PropertyModel<Kontor>(this, "valgtKontor"));
        gruppe.setRequired(true);

        gruppe.add(new ListView<Kontor>("kontorvalg", kontorer) {
            protected void populateItem(ListItem<Kontor> item) {
                item.add(new Radio<>("kontorknapp", item.getModel()));
                item.add(new Label("kontornavn", item.getModelObject().navn));
            }
        });

        final Form form = new Form<>("kontorform");
        form.add(gruppe);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        final WebMarkupContainer valgContainer = new WebMarkupContainer("valgContainer");
        valgContainer.setOutputMarkupPlaceholderTag(true);

        form.add(new AjaxButton("velg") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                lukkNAVEnhetPanel(target, valgContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        valgContainer.add(form);

        add(new Label("navIdent", SubjectHandler.getSubjectHandler().getUid()), valgContainer);

    }

    private void lukkNAVEnhetPanel(AjaxRequestTarget target, WebMarkupContainer valgContainer) {
        valgContainer.setVisibilityAllowed(false);
        target.add(valgContainer);
    }
}
