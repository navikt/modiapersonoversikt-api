package no.nav.sbl.dialogarena.besvare.web;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.besvare.consumer.BesvareSporsmalVM;
import no.nav.sbl.dialogarena.besvare.consumer.MeldingService;
import no.nav.sbl.dialogarena.besvare.consumer.SporsmalOgSvar;
import no.nav.sbl.dialogarena.besvare.consumer.Svar;
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
    private MeldingService service;

    public BesvareSporsmalPanel(String id, SporsmalOgSvar sos) {
        super(id);
        add(new SvarForm("svar-form", new CompoundPropertyModel<>(new BesvareSporsmalVM(
                sos.svar.id, sos.sporsmal.tema, sos.sporsmal.fritekst, sos.svar.fritekst,
                sos.sporsmal.opprettet, false))));
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
                    service.besvar(new Svar().withId(sos.behandlingsId).withTema(sos.tema).withFritekst(sos.svar).withSensitiv(sos.sensitiv));
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
