package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.AnsattService;
import org.apache.wicket.ajax.AjaxEventBehavior;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class SaksbehandlerInstillingerPanel extends Panel {

    @Inject
    private AnsattService ansattService;

    public SaksbehandlerInstillingerPanel(String id) {
        super(id);

        final SaksbehandlerInstillinger saksbehandlerInstillinger = new SaksbehandlerInstillinger();

        RadioGroup<String> gruppe = new RadioGroup<>("enhet", new PropertyModel<String>(saksbehandlerInstillinger, "valgtEnhet"));
        gruppe.setRequired(true);

        gruppe.add(new ListView<AnsattEnhet>("enhetsvalg", ansattService.hentEnhetsliste()) {
            protected void populateItem(ListItem<AnsattEnhet> item) {
                item.add(new Radio<>("enhetId", Model.of(item.getModelObject().enhetId)));
                item.add(new Label("enhetNavn", item.getModelObject().enhetNavn));
            }
        });

        final Form form = new Form<>("enhetsform");
        form.add(gruppe);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        final WebMarkupContainer valgContainer = new WebMarkupContainer("valgContainer");
        valgContainer.setOutputMarkupPlaceholderTag(true);
        valgContainer.setVisibilityAllowed(false);

        form.add(new AjaxButton("velg") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saksbehandlerInstillinger.lagreInstillingerCookie();
                toggleSaksbehandlerPanel(target, valgContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        valgContainer.add(form);

        add(new WebMarkupContainer("apneSaksbehandlerPanel")
                .add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        toggleSaksbehandlerPanel(target, valgContainer);
                    }
                }));

        add(new Label("navIdent", getSubjectHandler().getUid()), valgContainer);
    }

    private void toggleSaksbehandlerPanel(AjaxRequestTarget target, WebMarkupContainer valgContainer) {
        valgContainer.setVisibilityAllowed(!valgContainer.isVisibilityAllowed());
        target.add(valgContainer);
    }
}
