package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlerpanel;

import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.service.SaksbehandlerInnstillingerService;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class SaksbehandlerInnstillingerPanel extends Panel {

    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    public String valgtEnhet;

    public SaksbehandlerInnstillingerPanel(String id) {
        super(id);

        valgtEnhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();

        RadioGroup<String> gruppe = new RadioGroup<>("enhet", new PropertyModel<String>(this, "valgtEnhet"));
        gruppe.setRequired(true);

        gruppe.add(new ListView<AnsattEnhet>("enhetsvalg", saksbehandlerInnstillingerService.hentEnhetsListe()) {
            protected void populateItem(ListItem<AnsattEnhet> item) {
                item.add(new Radio<>("enhetId", Model.of(item.getModelObject().enhetId)));
                item.add(new Label("enhetNavn", item.getModelObject().enhetNavn));
            }
        });

        final Form form = new Form<>("enhetsform");
        form.add(gruppe);

        final WebMarkupContainer valgContainer = new WebMarkupContainer("valgContainer");
        valgContainer.setOutputMarkupPlaceholderTag(true);
        valgContainer.setVisibilityAllowed(false);

        form.add(new AjaxButton("velg") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                saksbehandlerInnstillingerService.setSaksbehandlerValgtEnhetCookie(valgtEnhet);
                toggleSaksbehandlerPanel(target, valgContainer);
            }
        });

        valgContainer.add(form);

        add(new WebMarkupContainer("apneSaksbehandlerInnstillingerPanel")
                .add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        toggleSaksbehandlerPanel(target, valgContainer);
                    }
                }));

        add(new Label("navIdent", getSubjectHandler().getUid()), valgContainer);
    }

    private void toggleSaksbehandlerPanel(AjaxRequestTarget target, WebMarkupContainer valgContainer) {
        if (valgContainer.isVisibilityAllowed()) {
            target.prependJavaScript("saksbehandlerPanelLukket|$('.nav-enhet').slideUp(700,saksbehandlerPanelLukket)");
            valgContainer.setVisibilityAllowed(false);
        } else {
            valgContainer.setVisibilityAllowed(true);
            target.appendJavaScript("$('.nav-enhet').slideDown(700)");
        }
        target.add(valgContainer);
    }
}
