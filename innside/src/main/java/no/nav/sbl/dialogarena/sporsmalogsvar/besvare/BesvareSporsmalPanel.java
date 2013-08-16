package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private MeldingService service;

    private FeedbackPanel feedbackPanel;
    public static final String SPORSMAL_OPPDATERT = "hendelser.sporsmal_oppdatert";

    public BesvareSporsmalPanel(String id, BesvareModell model, FeedbackPanel feedbackPanel) {
        super(id, model);
        this.feedbackPanel = feedbackPanel;
        setOutputMarkupId(true);
        add(
                new SporsmalPanel("sporsmal", model),
                new SvarForm("svar", model));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(
                new CssResourceReference(BesvareSporsmalPanel.class, "../stylesheets/besvare.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "textarea.js")));
        response.render(OnDomReadyHeaderItem.forScript("textarea();"));
    }
    
    private final class SvarForm extends Form<SporsmalOgSvarVM> {

        public SvarForm(String id, final BesvareModell model) {
            super(id, model);
            TextArea<Object> fritekst = new TextArea<>("svar.fritekst");
            fritekst.setRequired(true);
            add(
                    new TextField<>("svar.overskrift"),
                    fritekst,
                    new CheckBox("svar.sensitiv", Model.of(Boolean.FALSE)),
                    new MapBasedDropDownChoice("svar.saksid", model),
                    new AjaxSubmitLink("send") {
                        @Override
                        protected void onError(AjaxRequestTarget target, Form<?> form) {
                            target.add(feedbackPanel);
                        }

                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            SvarMeldingVM svar = model.getSvar();
                            String tema = model.getObject().sakTemaMapping.get(svar.saksid);
                            WSSvar wssvar = new WSSvar().withBehandlingsId(svar.behandlingsId).withTema(tema).withSaksid(svar.saksid).withOverskrift(svar.overskrift).withFritekst(svar.fritekst).withSensitiv(svar.sensitiv);
                            service.besvar(wssvar);
                            model.nullstill();
                            info("Svaret er sendt.");
                            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.MELDINGER_OPPDATERT));
                            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(SPORSMAL_OPPDATERT));
                            target.add(BesvareSporsmalPanel.this);
                        }
                    },
                    new AjaxLink<Void>("avbryt") {
                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            model.nullstill();
                            send(getPage(), Broadcast.BREADTH, SPORSMAL_OPPDATERT);
                        }
                    }
            );
        }
    }
    
    private static final class MapBasedDropDownChoice extends DropDownChoice<String> {
    	public MapBasedDropDownChoice(String id, final BesvareModell model) {
    		super(id);
    		setChoices(new IModel<List<String>>() {
				@Override
				public void detach() {
					System.out.println("Detach");
				}

				@Override
				public List<String> getObject() {
					System.out.println("GetObject");
					return new ArrayList<>(model.getObject().sakTemaMapping.keySet());
				}

				@Override
				public void setObject(List<String> object) {
					System.out.println("SetObject: " + object);
				}
			});
    		setChoiceRenderer(new ChoiceRenderer<String>() {
    			@Override
    			public Object getDisplayValue(String key) {
    				return model.getObject().sakTemaMapping.get(key);
    			}
    			@Override
    			public String getIdValue(String key, int index) {
    				return key;
    			}
    		});
    	}
    }

}
