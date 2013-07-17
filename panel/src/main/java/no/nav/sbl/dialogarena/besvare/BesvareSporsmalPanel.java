package no.nav.sbl.dialogarena.besvare;

import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class BesvareSporsmalPanel extends GenericPanel<Void> {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public BesvareSporsmalPanel(String id, WSSporsmalOgSvar sos) {
        super(id);
        add(new SvarForm("svar-form", new CompoundPropertyModel<>(new BesvareSporsmalVM(
                sos.getSvar().getId(), sos.getSporsmal().getTema(), sos.getSporsmal().getFritekst(), sos.getSvar().getFritekst(),
                sos.getSporsmal().getOpprettet().toLocalDate(), false))));
        add(new AttributeAppender("class", "visittkort"));
    }


    private final class SvarForm extends Form<BesvareSporsmalVM> {

        public SvarForm(String id, final IModel<BesvareSporsmalVM> model) {
            super(id, model);
            WebMarkupContainer header = new WebMarkupContainer("header");
            header.add(new Label("opprettet"), new Label("tema"), new Label("sporsmal"));
            add(header);
            add(new TextArea<>("svar"));
            CheckBox sensitivInfoCheckBox = new CheckBox("sensitiv");
            add(sensitivInfoCheckBox);
            add(new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    BesvareSporsmalVM sos = getModelObject();
                    webservice.besvarSporsmal(new WSSvar()
                            .withBehandlingsId(sos.behandlingsId).withTema(sos.tema).withFritekst(sos.svar).withSensitiv(sos.sensitiv));
                    model.setObject(new BesvareSporsmalVM());
                    target.add(this.getPage());
                }
            });
            add(new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    model.setObject(new BesvareSporsmalVM());
                    target.add(getPage());
                }
            });
        }
    }

}
