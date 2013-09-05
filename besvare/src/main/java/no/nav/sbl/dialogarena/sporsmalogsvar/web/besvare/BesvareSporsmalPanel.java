package no.nav.sbl.dialogarena.sporsmalogsvar.web.besvare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.BesvareModell;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.BesvareVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.SporsmalVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.web.modell.SvarVM;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.apache.commons.collections15.Transformer;
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
    @Named("besvareSso")
    BesvareHenvendelsePortType service;

    public static final String SPORSMAL_OPPDATERT = "hendelser.sporsmal_oppdatert";

    public BesvareSporsmalPanel(String id, String oppgaveId) {
        super(id);
        setOutputMarkupId(true);

        BesvareModell besvareModell = new BesvareModell(oppgaveId == null ? new BesvareVM() : TIL_BESVAREVM.transform(service.hentSporsmalOgSvar(oppgaveId)));
        setDefaultModel(besvareModell);
        FeedbackPanel feedbackPanel = new FeedbackPanel("feedback");
        feedbackPanel.setOutputMarkupId(true);
        add(
                feedbackPanel,
                new SporsmalPanel("sporsmal"),
                new SvarForm("svar", besvareModell, feedbackPanel));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(
                new CssResourceReference(BesvareSporsmalPanel.class, "../../stylesheets/besvare.css")));
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BesvareSporsmalPanel.class, "textarea.js")));
        response.render(OnDomReadyHeaderItem.forScript("textarea();"));
    }
    
    private final class SvarForm extends Form<BesvareVM> {

        public SvarForm(String id, final BesvareModell model, final FeedbackPanel feedbackPanel) {
            super(id);
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
                            SvarVM svar = model.getSvar();
                            String tema = model.getObject().sakTemaMapping.get(svar.saksid);
                            WSSvar wssvar = new WSSvar().withBehandlingsId(svar.behandlingsId).withTema(tema).withSaksid(svar.saksid).withOverskrift(svar.overskrift).withFritekst(svar.fritekst).withSensitiv(svar.sensitiv);
                            service.besvarSporsmal(wssvar);
                            model.nullstill();
                            info("Svaret er sendt.");
//                            send(getPage(), Broadcast.BREADTH, new NamedEventPayload(Innboks.MELDINGER_OPPDATERT));
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
				}

				@Override
				public List<String> getObject() {
					return new ArrayList<>(model.getObject().sakTemaMapping.keySet());
				}

				@Override
				public void setObject(List<String> object) {
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

    private static final Transformer<WSSporsmalOgSvar, BesvareVM> TIL_BESVAREVM = new Transformer<WSSporsmalOgSvar, BesvareVM>() {
        @Override
        public BesvareVM transform(WSSporsmalOgSvar wsSporsmalOgSvar) {
            SporsmalVM sporsmalVM = new SporsmalVM();
            sporsmalVM.behandlingsId = wsSporsmalOgSvar.getSporsmal().getBehandlingsId();
            sporsmalVM.fritekst = wsSporsmalOgSvar.getSporsmal().getFritekst();
            sporsmalVM.opprettetDato = wsSporsmalOgSvar.getSporsmal().getOpprettet().toLocalDate();
            sporsmalVM.overskrift = wsSporsmalOgSvar.getSporsmal().getOverskrift();

            SvarVM svarVM = new SvarVM();
            svarVM.behandlingsId = wsSporsmalOgSvar.getSvar().getBehandlingsId();

            return new BesvareVM(sporsmalVM, svarVM, new HashMap<String, String>());
        }
    };

}
