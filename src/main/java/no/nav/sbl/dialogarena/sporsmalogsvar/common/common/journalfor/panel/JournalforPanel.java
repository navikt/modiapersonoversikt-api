package no.nav.sbl.dialogarena.sporsmalogsvar.common.common.journalfor.panel;


import no.nav.sbl.dialogarena.sporsmalogsvar.common.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.journalfor.JournalforService;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.journalfor.domene.Journalforing;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.common.journalfor.domene.Sak;
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
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class JournalforPanel extends GenericPanel<Traad> {

    public static final PackageResourceReference LESS_REFERENCE = new PackageResourceReference(JournalforPanel.class, "journalfor.less");


    private final IModel<Boolean> erJournalfort = new AbstractReadOnlyModel<Boolean>() {
        @Override
        public Boolean getObject() {
            return getModelObject().erJournalfort();
        }
    };

    private final IModel<Boolean> synligJournalforForm = new AbstractReadOnlyModel<Boolean>() {
        @Override
        public Boolean getObject() {
            return journalforForm.isVisibleInHierarchy();
        }
    };

    private final AjaxLink<Void> journaforingExpander;
    private final JournalforForm journalforForm;

    private final JournalforService journalforService;

    @Inject
    private BesvareHenvendelsePortType besvareHenvendelsePortType;

    public JournalforPanel(String id, IModel<Traad> traad, String fnr) {
        super(id);
        setModel(new CompoundPropertyModel<>(traad));
        setOutputMarkupPlaceholderTag(true);
        journalforService = new JournalforService(besvareHenvendelsePortType);

        journaforingExpander = new AjaxLink<Void>("start-journalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                toggleSynlighet(target);
            }
        };

        journalforForm = new JournalforForm("journalfor-form", traad, fnr);
        journalforForm.setVisibilityAllowed(false);
        journalforForm.setOutputMarkupPlaceholderTag(true);

        journaforingExpander.add(
                hasCssClassIf("valgt", synligJournalforForm),
                visibleIf(not(erJournalfort)));
        journaforingExpander.setOutputMarkupId(true);


        WebMarkupContainer journalfortKvittering = new WebMarkupContainer("kvittering");
        journalfortKvittering.add(visibleIf(erJournalfort));
        journalfortKvittering.add(new Label("journalforingkvittering.dato"));
        journalfortKvittering.add(new Label("journalforingkvittering.saksId"));
        journalfortKvittering.add(new Label("journalforingkvittering.tema", new StringResourceModel("${journalforingkvittering.tema}", getModel())));


        add(journaforingExpander, journalforForm, journalfortKvittering);
    }

    private void toggleSynlighet(AjaxRequestTarget target) {
        journalforForm.setVisibilityAllowed(!journalforForm.isVisibleInHierarchy());
        target.add(journaforingExpander, journalforForm);
    }

    private class JournalforForm extends Form<Journalforing> {

        final IModel<Boolean> harSaker = new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return getModelObject().harSaker();
            }
        };

        private final RadioGroup<Sak> sakRadioGroup;

        public JournalforForm(String id, final IModel<Traad> traad, final String fnr) {
            super(id);

            setModel(new CompoundPropertyModel<>(new LoadableDetachableModel<Journalforing>() {
                @Override
                protected Journalforing load() {
                    return journalforService.opprettJournalforing(fnr, traad.getObject());
                }
            }));

            sakRadioGroup = new RadioGroup<>("valgtSak");
            sakRadioGroup.setRequired(true);
            sakRadioGroup.add(visibleIf(harSaker));
            sakRadioGroup.add(new ListView<String>("temakoder") {
                @Override
                protected void populateItem(ListItem<String> item) {
                    String temakode = item.getModelObject();
                    item.add(new Label("tema", new StringResourceModel(temakode, null)));
                    item.add(new ListView<Sak>("sak", JournalforForm.this.getModelObject().getSaker(temakode)) {
                        @Override
                        protected void populateItem(ListItem<Sak> item) {
                            Radio<Sak> radio = new Radio<>("radio", item.getModel());
                            Sak sak = item.getModelObject();
                            AttributeModifier radioReference = new AttributeModifier("for", radio.getMarkupId());
                            Label opprettetDato = new Label("opprettetDato", Datoformat.kort(sak.opprettetDato));
                            opprettetDato.add(radioReference);
                            Label sakstype = new Label("sakstype", sak.sakstype);
                            sakstype.add(radioReference);
                            Label statuskode = new Label("statuskode", sak.statuskode);
                            Label sakid = new Label("sakid", sak.saksId);
                            item.add(radio, opprettetDato, sakstype, statuskode, sakid);

                        }
                    });
                }
            });

            Label ingenSaker = new Label("ingen-saker",
                    new StringResourceModel("journalforpanel.ingen-saker", JournalforPanel.this, null));
            ingenSaker.add(visibleIf(not(harSaker)));

            final RadioGroup<Boolean> sensitivRadioGroup = new RadioGroup<>("sensitiv");
            sensitivRadioGroup.setRequired(true);
            sensitivRadioGroup.add(new Radio<>("ikke-sensitivt", Model.of(false)));
            sensitivRadioGroup.add(new Radio<>("sensitivt", Model.of(true)));

            final FeedbackPanel feedback = new FeedbackPanel("feedback");
            feedback.setOutputMarkupId(true);

            AjaxSubmitLink journalfor = new AjaxSubmitLink("journalfor-submit") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    if (!getModelObject().harSaker()) {
                        error(new StringResourceModel("journalforpanel.ingen-saker.submit", JournalforPanel.this, null).getString());
                        target.add(feedback);
                    } else {
                        journalforService.journalfor(getModelObject());
                        journalforForm.setVisibilityAllowed(false);
                        target.add(JournalforPanel.this);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedback);
                }
            };

            AjaxLink<Void> avbryt = new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    toggleSynlighet(target);
                }
            };

            add(sakRadioGroup, ingenSaker, sensitivRadioGroup, feedback, journalfor, avbryt);
        }
    }
}
