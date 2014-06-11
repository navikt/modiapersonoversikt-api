package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
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
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.shortcuts.Shortcuts.cssClass;
import static org.joda.time.DateTime.now;

public class Dialogpanel extends Panel {

    public static final PackageResourceReference DIALOGPANEL_LESS = new PackageResourceReference(Dialogpanel.class, "dialogpanel.less");

    @Inject
    private SendHenvendelsePortType ws;

    public Dialogpanel(String id, final String fnr) {
        super(id);

        final Kvitteringspanel kvittering = new Kvitteringspanel("kvittering");

        final Form<DialogVM> form = new Form<>("dialogform", new CompoundPropertyModel<>(new DialogVM()));
        form.setOutputMarkupPlaceholderTag(true);

        form.add(new DropDownChoice<>("tema", asList(Tema.values()), new ChoiceRenderer<Tema>() {
            @Override
            public Object getDisplayValue(Tema object) {
                return getString(object.name());
            }
        }).setRequired(true));

        form.add(new RadioGroup<Kanal>("kanal")
                .setRequired(true)
                .add(new ListView<Kanal>("kanalvalg", asList(Kanal.values())) {
                    @Override
                    protected void populateItem(ListItem<Kanal> item) {
                        item.add(new Radio<>("kanalknapp", item.getModel()));
                        item.add(new WebMarkupContainer("kanalikon").add(cssClass(item.getModelObject().name().toLowerCase())));
                    }}));

        form.add(new EnhancedTextArea("tekstfelt", form.getModel(),
                new EnhancedTextAreaConfigurator()
                        .withMaxCharCount(5000)
                        .withMinTextAreaHeight(250)
                        .withPlaceholderText(getString("dialogform.tekstfelt.placeholder"))));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new AjaxSubmitLink("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                sendHenvendelse(form.getModelObject(), fnr);

                form.setModelObject(new DialogVM());
                form.setVisibilityAllowed(false);

                kvittering.visISekunder(Duration.seconds(3), target, form);

                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(form, kvittering);
    }

    private void sendHenvendelse(DialogVM formInput, String fnr) {
        XMLBehandlingsinformasjon info =
                new XMLBehandlingsinformasjon()
                        .withHenvendelseType(REFERAT.name())
                        .withAktor(new XMLAktor().withFodselsnummer(fnr).withNavIdent(getSubjectHandler().getUid()))
                        .withOpprettetDato(now())
                        .withAvsluttetDato(now())
                        .withMetadataListe(new XMLMetadataListe().withMetadata(
                                new XMLReferat().withTemagruppe(formInput.tema).withKanal(formInput.kanal.name()).withFritekst(formInput.getFritekst())));

        ws.sendHenvendelse(new WSSendHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(fnr).withAny(info));
    }
}
