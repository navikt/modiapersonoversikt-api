package no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.panel;


import no.nav.sbl.dialogarena.sporsmalogsvar.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.Traad;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.JournalforModell;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Journalforing;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.domene.Sak;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSMelding;
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
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.journalfor.utils.Utils.TIL_WSMELDING;

public class JournalforPanel extends Panel {

    public static final PackageResourceReference LESS_REFERENCE = new PackageResourceReference(JournalforPanel.class, "journalfor.less");

    private IModel<Boolean> aabnet;
    private AjaxLink<Void> journaforingExpander;
    private JournalforForm journalforForm;

    @Inject
    BesvareHenvendelsePortType besvareHenvendelsePortType;

    public JournalforPanel(String id, IModel<Traad> traad, String fnr) {
        super(id);

        aabnet = new Model<>(false);

        journaforingExpander = new AjaxLink<Void>("start-journalforing") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                toggleSynlighet(target);
            }
        };
        journaforingExpander.add(hasCssClassIf("valgt", aabnet));
        journaforingExpander.setOutputMarkupId(true);

        journalforForm = new JournalforForm("journalfor-form", traad, fnr, besvareHenvendelsePortType);
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

        private final JournalforModell modell;

        public JournalforForm(String id, final IModel<Traad> traad, String fnr, final BesvareHenvendelsePortType besvareHenvendelsePortType) {
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
                            Sak sak = item.getModelObject();
                            AttributeModifier radioReference = new AttributeModifier("for", radio.getMarkupId());
                            Label opprettetDato = new Label("opprettetDato", Datoformat.kort(sak.opprettetDato));
                            opprettetDato.add(radioReference);
                            Label fagsystem = new Label("fagsystem", sak.fagsystem);
                            fagsystem.add(radioReference);
                            Label statuskode = new Label("statuskode", sak.statuskode);
                            Label sakstype = new Label("sakstype", sak.sakstype);
                            item.add(radio, opprettetDato, fagsystem, statuskode, sakstype);

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
                    Sak valgtSak = getModelObject().getValgtSak();
                    List<WSMelding> wsMeldinger = new ArrayList<>();
                    for (Melding melding : traad.getObject().getDialog()) {
                        WSMelding wsMelding = TIL_WSMELDING.transform(melding);
                        wsMelding.setSaksId(valgtSak.saksId);
                        wsMeldinger.add(wsMelding);
                    }
                    besvareHenvendelsePortType.journalforMeldinger(wsMeldinger);
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
                    modell.nullstill();
                    toggleSynlighet(target);
                }
            };

            add(sakRadioGroup, sensitivRadioGroup, feedback, journalfor, avbryt);
        }
    }
}
