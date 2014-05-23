package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.meldinger.WSSendHenvendelseRequest;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.joda.time.DateTime;

import javax.inject.Inject;

import static java.util.Arrays.asList;

public class Dialogpanel extends Panel {

    @Inject
    HenvendelseAktivitetV2PortType ws;

    public Dialogpanel(String id, final String fnr) {
        super(id);

        final Form<DialogVM> form = new Form<>("dialog-form", new CompoundPropertyModel<>(new DialogVM()));
        form.setOutputMarkupId(true);

        form.add(new EnhancedTextArea("tekstfelt", form.getModel()));

        form.add(new DropDownChoice<>("tema", asList(Tema.values()), new ChoiceRenderer<Tema>() {
            @Override
            public Object getDisplayValue(Tema object) {
                return new StringResourceModel(object.name(), Dialogpanel.this, getDefaultModel()).getObject();
            }
        }));

        form.add(new RadioGroup<Kanal>("kanal").add(
                new ListView<Kanal>("kanaler", asList(Kanal.values())) {
                    @Override
                    protected void populateItem(ListItem<Kanal> item) {
                        item.add(new Radio<>("kanalvalg", item.getModel()));
                        item.add(new Label("kanalnavn", new ResourceModel(item.getModelObject().toString())));
                    }
                }));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        form.add(feedbackPanel);

        form.add(new AjaxSubmitLink("send") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> submitForm) {
                DialogVM formInput = form.getModelObject();
                XMLBehandlingsinformasjonV2 info =
                        new XMLBehandlingsinformasjonV2()
                                .withHenvendelseType("referat")
                                .withAktor(new XMLAktor().withFodselsnummer(fnr))
                                .withOpprettetDato(DateTime.now())
                                .withAvsluttetDato(DateTime.now())
                                .withMetadataListe(new XMLMetadataListe().withMetadata(
                                        new XMLReferat().withTemagruppe(formInput.tema).withKanal(formInput.kanal).withFritekst(formInput.getFritekst())));

                ws.sendHenvendelse(new WSSendHenvendelseRequest().withType("referat").withFodselsnummer(fnr).withAny(info));
                form.setModelObject(new DialogVM());
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        add(form);
    }
}
