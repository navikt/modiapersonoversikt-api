package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.referatpanel;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services.SakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.KvitteringsPanel;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.SvarOgReferatVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.Temagruppe;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;

public class ReferatPanel extends Panel {

    @Inject
    private SakService sakService;

    private final String fnr;
    private final KvitteringsPanel kvittering;

    public ReferatPanel(String id, String fnr) {
        super(id);
        this.fnr = fnr;
        setOutputMarkupPlaceholderTag(true);

        final Form<SvarOgReferatVM> form = new Form<>("referatform", new CompoundPropertyModel<>(new SvarOgReferatVM()));
        form.setOutputMarkupPlaceholderTag(true);

        form.add(new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderTextKey("referatform.tekstfelt.placeholder")
        ));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new DropDownChoice<>("temagruppe", asList(Temagruppe.values()), new ChoiceRenderer<Temagruppe>() {
            @Override
            public Object getDisplayValue(Temagruppe object) {
                return getString(object.name());
            }
        }).setRequired(true));

        form.add(new RadioGroup<>("kanal")
                .setRequired(true)
                .add(new ListView<ReferatKanal>("kanalvalg", asList(ReferatKanal.values())) {
                    @Override
                    protected void populateItem(ListItem<ReferatKanal> item) {
                        item.add(new Radio<>("kanalknapp", item.getModel()));
                        item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
                    }
                }));

        form.add(new AjaxButton("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                sendOgVisKvittering(target, form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        kvittering = new KvitteringsPanel("kvittering");

        add(form, kvittering);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('.temagruppevelger').selectmenu({appendTo:'.temagruppevelger-wrapper'});"));
    }

    private void sendOgVisKvittering(AjaxRequestTarget target, Form<SvarOgReferatVM> form) {
        SvarOgReferatVM svarOgReferatVM = form.getModelObject();
        sendHenvendelse(svarOgReferatVM);

        kvittering.visISekunder(3, getString(svarOgReferatVM.kanal.getKvitteringKey()), target, form);
    }

    private void sendHenvendelse(SvarOgReferatVM svarOgReferatVM) {
        Referat referat = new Referat()
                .withFnr(fnr)
                .withNavIdent(getSubjectHandler().getUid())
                .withTemagruppe(svarOgReferatVM.temagruppe.name())
                .withKanal(svarOgReferatVM.kanal.name())
                .withFritekst(svarOgReferatVM.getFritekst());

        sakService.sendReferat(referat);
    }
}
