package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.modig.modia.events.FeedItemPayload;
import no.nav.modig.modia.events.InternalEvents;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.BesvareSporsmalVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.SporsmalOgSvar;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Svar;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private MeldingService service;

    public BesvareSporsmalPanel(String id) { //}, SporsmalOgSvar sos) {
        super(id);
        SporsmalOgSvar sos = service.plukkMelding();
        add(new SvarForm("svar-form", new CompoundPropertyModel<>(new BesvareSporsmalVM(
                sos.svar.id, sos.sporsmal.tema, sos.sporsmal.fritekst, sos.svar.fritekst,
                sos.sporsmal.opprettet, false))));
        add(new AttributeAppender("class", "visittkort"));

        AjaxLink brukerhenvendelserLink = new AjaxLink("brukerhenvendelserLink") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BUBBLE, new NamedEventPayload(InternalEvents.FEED_ITEM_CLICKED, new FeedItemPayload(null, null, "brukerhenvendelser")));
            }
        };

        add(brukerhenvendelserLink);

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
