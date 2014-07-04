package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;

public abstract class DialogPanel extends Panel {

    public static final PackageResourceReference DIALOGPANEL_LESS = new PackageResourceReference(DialogPanel.class, "DialogPanel.less");

    @Inject
    protected SakService sakService;

    protected String fnr;
    private final KvitteringsPanel kvittering;

    public DialogPanel(String id, final String fnr) {
        super(id);
        setOutputMarkupId(true);

        this.fnr = fnr;
        kvittering = new KvitteringsPanel("kvittering");

        add(kvittering);
    }

    protected void submit(AjaxRequestTarget target, Form<DialogVM> form) {
        submit(target, form, form);
    }

    protected void submit(AjaxRequestTarget target, Form<DialogVM> form, Component... components) {
        DialogVM dialogVM = form.getModelObject();
        sendHenvendelse(dialogVM, fnr);

        kvittering.visISekunder(Duration.seconds(3), target, getString(dialogVM.kanal.getKvitteringKey()), components);

        form.setModelObject(new DialogVM());

        target.add(components);
    }

    protected EnhancedTextArea lagTekstfelt(String id, Form<DialogVM> form) {
        return new EnhancedTextArea(id, form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderText(getString("dialogform.tekstfelt.placeholder"))
        );
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(ReferatPanel.class, "jquery-ui-selectmenu.min.js")));
    }

    protected abstract void sendHenvendelse(DialogVM dialogVM, String fnr);
}
