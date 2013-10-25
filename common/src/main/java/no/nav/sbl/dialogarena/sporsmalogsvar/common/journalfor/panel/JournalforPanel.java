package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel;

import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.JournalforModell;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Journalforing;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class JournalforPanel extends Panel {

    public static final PackageResourceReference LESS_REFERENCE = new PackageResourceReference(JournalforPanel.class, "journalfor.less");
    private static final Logger log = LoggerFactory.getLogger(JournalforPanel.class);

    @Inject
    BesvareHenvendelsePortType besvareHenvendelsePortType;

    public JournalforPanel(String id, Traad traad, String fnr) {
        super(id);

        final JournalforForm journalforForm = new JournalforForm("journalfor-form", traad, fnr, besvareHenvendelsePortType);
        journalforForm.setVisibilityAllowed(false);
        journalforForm.setOutputMarkupPlaceholderTag(true);

        AjaxLink<Void> startJournalforing = new AjaxLink<Void>("start-journalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                journalforForm.setVisibilityAllowed(true);
                target.add(journalforForm);
            }
        };
        add(startJournalforing, journalforForm);
    }

    private static class JournalforForm extends Form<Journalforing> {

        private final JournalforModell modell;

        public JournalforForm(String id, final Traad traad, String fnr, BesvareHenvendelsePortType besvareHenvendelsePortType) {
            super(id);
            modell = new JournalforModell(traad, fnr, besvareHenvendelsePortType);
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
                            item.add(radio);
                            WebMarkupContainer sakContainer = new WebMarkupContainer("sak-container");
                            sakContainer.add(new AttributeModifier("for", radio.getMarkupId()));
                            Sak sak = item.getModelObject();
                            sakContainer.add(new Label("opprettetDato", Datoformat.kort(sak.opprettetDato)));
                            sakContainer.add(new Label("fagsystem", sak.fagsystem));
                            item.add(sakContainer);
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
                     //JournalforPanel.this.besvareHenvendelsePortType modell.getObject().getValgtSak()
                    log.info("Trodde jeg journalførte gitt");
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedback);
                }
            };

            AjaxLink<Void> avbryt = new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    modell.nullstill();
                    JournalforForm.this.setVisibilityAllowed(false);
                    target.add(JournalforForm.this);
                }
            };

            add(sakRadioGroup, sensitivRadioGroup, feedback, journalfor, avbryt);
        }

    }

}
