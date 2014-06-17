package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;

import static java.util.Arrays.asList;

public abstract class DialogPanel extends Panel {

    public static final PackageResourceReference DIALOGPANEL_LESS = new PackageResourceReference(DialogPanel.class, "HestedialogpanelFjes.less");

    @Inject
    protected SendHenvendelsePortType ws;

    protected Form<DialogVM> form;

    public DialogPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);

        form = new Form<>("dialogform", new CompoundPropertyModel<>(new DialogVM()));
        form.setOutputMarkupPlaceholderTag(true);

        form.add(new DropDownChoice<>("tema", asList(Tema.values()), new ChoiceRenderer<Tema>() {
            @Override
            public Object getDisplayValue(Tema object) {
                return getString(object.name());
            }
        }).setRequired(true));

        form.add(new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderText(getString("dialogform.tekstfelt.placeholder"))
        ));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        final HesteKvitteringspanelFjes kvittering = new HesteKvitteringspanelFjes("kvittering");

        form.add(new AjaxButton("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                DialogVM dialogVM = form.getModelObject();
                sendHenvendelse(dialogVM, fnr);

                kvittering.visISekunder(Duration.seconds(3), target, form, getString(dialogVM.kanal.getKvitteringKey()));

                form.setModelObject(new DialogVM());

                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(
                form,
                kvittering
        );
    }

    protected abstract void sendHenvendelse(DialogVM dialogVM, String fnr);
}
