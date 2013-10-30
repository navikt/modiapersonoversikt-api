package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel;

import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.JournalforService;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Journalforing;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class JournalforPanel extends Panel {

    public static final PackageResourceReference LESS_REFERENCE = new PackageResourceReference(JournalforPanel.class, "journalfor.less");

    private final IModel<Boolean> aabnet;
    private final AjaxLink<Void> journaforingExpander;
    private final JournalforForm journalforForm;

    private final JournalforService journalforService;

    @Inject
    private BesvareHenvendelsePortType besvareHenvendelsePortType;


    public JournalforPanel(String id, IModel<Traad> traad, String fnr) {
        super(id);
        journalforService = new JournalforService(besvareHenvendelsePortType);
        aabnet = new Model<>(false);

        journaforingExpander = new AjaxLink<Void>("start-journalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                toggleSynlighet(target);
            }
        };
        journaforingExpander.add(hasCssClassIf("valgt", aabnet));
        journaforingExpander.setOutputMarkupId(true);

        journalforForm = new JournalforForm("journalfor-form", traad, fnr);
        journalforForm.setVisibilityAllowed(false);
        journalforForm.setOutputMarkupPlaceholderTag(true);


        add(journaforingExpander, journalforForm);
    }


    private void toggleSynlighet(AjaxRequestTarget target) {
        aabnet.setObject(!aabnet.getObject());
        journalforForm.setVisibilityAllowed(aabnet.getObject());
        target.add(journaforingExpander, journalforForm);
    }

    private class JournalforForm extends Form<Journalforing> {

        private final IModel<Journalforing> modell;

        public JournalforForm(String id, final IModel<Traad> traad, final String fnr) {
            super(id);

            modell = new LoadableDetachableModel<Journalforing>() {
                @Override
                protected Journalforing load() {
                    return journalforService.opprettJournalforing(fnr, traad.getObject());
                }
            };
            setModel(new CompoundPropertyModel<>(modell));

            final RadioGroup<Sak> sakRadioGroup = new RadioGroup<>("valgtSak");
            sakRadioGroup.setRequired(true);
            sakRadioGroup.add(new ListView<String>("temakoder") {
                @Override
                protected void populateItem(ListItem<String> item) {
                    String temakode = item.getModelObject();
                    item.add(new Label("tema", new StringResourceModel(temakode, null)));
                    item.add(new ListView<Sak>("sak", modell.getObject().getSaker(temakode)) {
                        @Override
                        protected void populateItem(ListItem<Sak> item) {
                            Radio<Sak> radio = new Radio<>("radio", item.getModel());
                            Sak sak = item.getModelObject();
                            AttributeModifier radioReference = new AttributeModifier("for", radio.getMarkupId());
                            Label opprettetDato = new Label("opprettetDato", Datoformat.kort(sak.opprettetDato));
                            opprettetDato.add(radioReference);
                            Label sakstype = new Label("sakstype", sak.sakstype);
                            sakstype.add(radioReference);
                            item.add(radio, opprettetDato, sakstype);
                        }
                    });
                }
            });

            final RadioGroup<Boolean> sensitivRadioGroup = new RadioGroup<>("sensitiv");
            sensitivRadioGroup.setRequired(true);
            sensitivRadioGroup.add(new Radio<>("ikke-sensitivt", Model.of(false)));
            sensitivRadioGroup.add(new Radio<>("sensitivt", Model.of(true)));

            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);

            AjaxSubmitLink journalfor = new AjaxSubmitLink("journalfor-submit") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    journalforService.journalfor(getModelObject());
                    toggleSynlighet(target);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedback);
                }
            };

            AjaxLink<Void> avbryt = new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
//                    modell.setObject(new Journalforing(traad.getObject(), Collections.<Sak>emptyList()));
                    toggleSynlighet(target);
                }
            };

            add(sakRadioGroup, sensitivRadioGroup, feedback, journalfor, avbryt);
        }
    }
}
