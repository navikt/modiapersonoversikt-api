package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.util.List;
import javax.inject.Inject;
import no.nav.modig.wicket.events.NamedEventPayload;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Svar;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.tema.Temastruktur;
import no.nav.sbl.dialogarena.sporsmalogsvar.tema.TemastrukturVelger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;

import static java.util.Arrays.asList;

public class BesvareSporsmalPanel extends Panel {

    @Inject
    private MeldingService service;

    public static final String SPORSMAL_OPPDATERT = "hendelser.sporsmal_oppdatert";
    private final FeedbackPanel feedbackPanel;
    private List<Temastruktur> temastrukturListe = asList(
            new Temastruktur("Pensjon", "Alderspensjon", "Krigspensjon", "Uførepensjon"),
            new Temastruktur("Trygd", "Barnetrygd", "Uføretrygd"));

    public BesvareSporsmalPanel(String id, BesvareModell model) {
        super(id, model);
        setOutputMarkupId(true);
        feedbackPanel = new FeedbackPanel("feedback");
        add(
                feedbackPanel,
                new SporsmalPanel("sporsmal", model),
                new SvarForm("svar", model));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forReference(
                new CssResourceReference(BesvareSporsmalPanel.class, "../stylesheets/besvare.css")));
    }

    private final class SvarForm extends Form<SporsmalOgSvarVM> {

        public SvarForm(String id, final BesvareModell model) {
            super(id, model);
            add(
                    new Label("svar.overskrift"),
                    new TextArea<>("svar.fritekst"),
                    new CheckBox("svar.sensitiv", new Model<>(false)),
                    new TextField<>("svar.tema"),
                    new TemastrukturVelger("tema-velger", temastrukturListe),
                    new AjaxSubmitLink("send") {
                        @Override
                        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                            MeldingVM svar = model.getSvar();
                            service.besvar(new Svar().withId(svar.getId()).withOverskrift(svar.getOverskrift()).withFritekst(svar.getFritekst()).withTema(svar.getTema()));
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
                    });
        }
    }

}
