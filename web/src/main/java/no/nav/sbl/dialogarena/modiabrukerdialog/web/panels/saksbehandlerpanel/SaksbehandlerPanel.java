package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.Cookie;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class SaksbehandlerPanel extends Panel {

    private String valgtKontor;

    public SaksbehandlerPanel(String id) {
        super(id);

        Cookie cookie = ((WebRequest) getRequestCycle().getRequest()).getCookie(brukerSpesifikCookieId());
        valgtKontor = cookie != null ? cookie.getValue() : null;

        List<String> kontorer = asList("1111", "2222", "3333");

        RadioGroup<String> gruppe = new RadioGroup<>("kontor", new PropertyModel<String>(this, "valgtKontor"));
        gruppe.setRequired(true);

        gruppe.add(new ListView<String>("kontorvalg", kontorer) {
            protected void populateItem(ListItem<String> item) {
                item.add(new Radio<>("kontorknapp", item.getModel()));
                item.add(new Label("kontornavn", item.getModelObject()));
            }
        });

        final Form form = new Form<>("kontorform");
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
                lagreKontorValg();
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

    private void lagreKontorValg() {
        Cookie cookie = new Cookie(brukerSpesifikCookieId(), valgtKontor);
        cookie.setMaxAge(12 * 60 * 60);
        ((WebResponse) getRequestCycle().getResponse()).addCookie(cookie);
    }

    private String brukerSpesifikCookieId() {
        return "kontor-" + getSubjectHandler().getUid();
    }

    private void toggleSaksbehandlerPanel(AjaxRequestTarget target, WebMarkupContainer valgContainer) {
        valgContainer.setVisibilityAllowed(!valgContainer.isVisibilityAllowed());
        target.add(valgContainer);
    }
}
