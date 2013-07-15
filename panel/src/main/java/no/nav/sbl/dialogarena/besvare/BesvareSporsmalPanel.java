package no.nav.sbl.dialogarena.besvare;

import java.io.Serializable;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;

public class BesvareSporsmalPanel extends GenericPanel<Void> {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public BesvareSporsmalPanel(String id, IModel<SporsmalOgSvar> modell) {
        super(id);
        add(new SvarForm("svar-form", modell));
        add(new AttributeAppender("class", "visittkort"));
    }


    private final class SvarForm extends Form<SporsmalOgSvar> {

        public SvarForm(String id, IModel<SporsmalOgSvar> model) {
            super(id, model);
            WebMarkupContainer header = new WebMarkupContainer("header");
            header.add(new Label("tema"), new Label("sporsmal"));
            add(header);
            add(new TextArea<>("svar"));
//            CheckBox sensitivInfoCheckBox = new CheckBox("sensitiv");
//            add(sensitivInfoCheckBox);
            add(new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    SporsmalOgSvar sos = getModelObject();
                    webservice.besvarSporsmal(new WSSvar().withBehandlingsId(sos.behandlingsId).withTema(sos.tema).withFritekst(sos.svar));
                    target.add(this.getPage());
                }
            });
            add(new AjaxLink<Void>("avbryt") {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    int i = 0;
                }
            });
        }
    }

    public static final class SvarWrapper implements Serializable {
        String sporsmal, svar, behandlingsid, tema;
        Boolean sensitiv;

        private SvarWrapper(String sporsmal, String behandlingsid, String tema, boolean sensitiv) {
            this.sporsmal = sporsmal;
            this.behandlingsid = behandlingsid;
            this.tema = tema;
            this.sensitiv = sensitiv;
        }
    }
}
